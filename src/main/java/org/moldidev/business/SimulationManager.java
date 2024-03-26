package org.moldidev.business;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.moldidev.controller.Controller;
import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SimulationManager implements Runnable {
    private final Controller controller;
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int numberOfServers;
    public int numberOfClients;
    public int maxArrivalTime;
    public int minArrivalTime;
    public SelectionPolicy selectionPolicy;
    public TextArea simulationLogs;
    public Label currentSimulationTime;
    private Scheduler scheduler;
    private List<Task> generatedTasks;

    public SimulationManager(Controller controller) {
        this.generatedTasks = new ArrayList<>();

        this.controller = controller;

        if (this.controller.getSelectionPolicy() == 0) {
            this.selectionPolicy = SelectionPolicy.SHORTEST_TIME;
        }

        else {
            this.selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
        }

        this.timeLimit = Integer.parseInt(this.controller.getSimulationIntervalTextField().getText().replaceAll(" ", ""));
        this.maxProcessingTime = Integer.parseInt(this.controller.getMaximumServiceTimeTextField().getText().replaceAll(" ", ""));
        this.minProcessingTime = Integer.parseInt(this.controller.getMinimumServiceTimeTextField().getText().replaceAll(" ", ""));
        this.numberOfServers = Integer.parseInt(this.controller.getNumberOfQueuesTextField().getText().replaceAll(" ", ""));
        this.numberOfClients = Integer.parseInt(this.controller.getNumberOfClientsTextField().getText().replaceAll(" ", ""));
        this.maxArrivalTime = Integer.parseInt(this.controller.getMaximumArrivalTimeTextField().getText().replaceAll(" ", ""));
        this.minArrivalTime = Integer.parseInt(this.controller.getMinimumArrivalTimeTextField().getText().replaceAll(" ", ""));
        this.simulationLogs = this.controller.getSimulationLogsTextArea();
        this.currentSimulationTime = this.controller.getCurrentSimulationTimeLabel();

        this.scheduler = new Scheduler(this.numberOfServers, this.numberOfClients / this.numberOfServers);

        generateNRandomTasks();

        for (int i = 0; i < this.numberOfServers; i++) {
            Server server = new Server();
            Thread serverThread = new Thread(server);
            serverThread.start();
            this.scheduler.getServers().add(server);
        }
    }

    private void generateNRandomTasks() {
        for (int i = 1; i <= this.numberOfClients; i++) {
            Task task = new Task(i, getRandomNumber(this.minArrivalTime, this.maxArrivalTime), getRandomNumber(this.minProcessingTime, this.maxProcessingTime));
            this.generatedTasks.add(task);
        }

        Collections.sort(this.generatedTasks);
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void run() {
        int currentTime = 0;
        double averageWaitingTime = 0;
        StringBuilder simulationLogs = new StringBuilder();

        while (currentTime <= this.timeLimit) {
            Iterator<Task> iterator = this.generatedTasks.iterator();

            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getArrivalTime() <= currentTime) {
                    this.scheduler.dispatchTask(task);
                    averageWaitingTime += task.getServiceTime();
                    iterator.remove();
                }
            }

            int finalCurrentTime = currentTime;

            Platform.runLater(() -> {
                simulationLogs.append("Time ").append(finalCurrentTime).append("\n");
                simulationLogs.append("Waiting clients: ");

                for (Task task : this.generatedTasks) {
                    if (task.getArrivalTime() > finalCurrentTime) {
                        simulationLogs.append("(").append(task.getId()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append("); ");
                    }
                }

                simulationLogs.append("\n");

                for (int i = 0; i < this.numberOfServers; i++) {
                    Server server = this.scheduler.getServers().get(i);

                    simulationLogs.append("Queue ").append(i + 1).append(": ");

                    if (server.getTasks().length == 0) {
                        simulationLogs.append("closed\n");
                    }

                    else {
                        for (Task task : server.getTasks()) {
                            simulationLogs.append("(").append(task.getId()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append("); ");
                        }

                        simulationLogs.append("\n");
                    }
                }

                simulationLogs.append("\n");

                this.currentSimulationTime.setText("Simulation status: active\nSimulation time: " + finalCurrentTime);
                this.simulationLogs.setText(simulationLogs.toString());
            });

            try {
                Thread.sleep(1000);
            }

            catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Server server : this.scheduler.getServers()) {
                for (Task task : server.getTasks()) {
                    task.setServiceTime(task.getServiceTime() - 1);

                    if (task.getServiceTime() == 0) {
                        server.removeTask(task);
                    }
                }
            }

            currentTime++;
        }

        averageWaitingTime /= this.numberOfClients;
        simulationLogs.append("Average waiting time: ").append(averageWaitingTime).append("\n");
        this.simulationLogs.setText(simulationLogs.toString());
    }
}
