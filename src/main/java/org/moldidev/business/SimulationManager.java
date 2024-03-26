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
import java.util.concurrent.atomic.AtomicInteger;

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
    public Label validInputLabel;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private double averageServiceTime = 0.0f;

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
        this.validInputLabel = this.controller.getValidInputLabel();

        this.scheduler = new Scheduler(this.numberOfServers, this.numberOfClients / this.numberOfServers + 1);

        generateNRandomTasks();
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
        StringBuilder simulationLogs = new StringBuilder();

        this.controller.disableInputs();

        do {
            dispatchTasks(currentTime);

            updateSimulationLogsAndTime(currentTime, simulationLogs);

            try {
                Thread.sleep(1000);
            }

            catch (InterruptedException e) {
                e.printStackTrace();
            }

            updateServerStates();

            currentTime++;
        } while (!((generatedTasks.isEmpty() && areAllServersIdle()) || (currentTime > this.timeLimit)));


        double averageWaitingTime = 0.0f;

        for (Server server : this.scheduler.getServers()) {
            averageWaitingTime += server.getWaitingPeriod().doubleValue();
        }

        this.averageServiceTime /= this.numberOfClients;
        averageWaitingTime /= this.numberOfClients;

        simulationLogs.append("Average waiting time: ").append(averageWaitingTime).append("\n");
        simulationLogs.append("Average service time: ").append(averageServiceTime).append("\n");
        this.simulationLogs.setText(simulationLogs.toString());

        this.controller.enableInputs();
    }

    private boolean areAllServersIdle() {
        for (Server server : this.scheduler.getServers()) {
            if (server.getTasks().length > 0) {
                return false;
            }
        }

        return true;
    }

    private void dispatchTasks(int currentTime) {
        Iterator<Task> iterator = this.generatedTasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getArrivalTime() <= currentTime && !isTaskDispatched(task)) {
                this.scheduler.dispatchTask(task);
                this.averageServiceTime += task.getServiceTime();
                iterator.remove();
            }
        }
    }

    private boolean isTaskDispatched(Task task) {
        for (Server server : scheduler.getServers()) {
            for (Task currentTask : server.getTasks()) {
                if (currentTask.getId() == task.getId()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateSimulationLogsAndTime(int currentTime, StringBuilder simulationLogs) {
        Platform.runLater(() -> {
            simulationLogs.append("Time ").append(currentTime).append("\n");
            simulationLogs.append("Waiting clients: ");

            // Append waiting clients information
            for (Task task : this.generatedTasks) {
                if (task.getArrivalTime() > currentTime) {
                    simulationLogs.append("(").append(task.getId()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append("); ");
                }
            }

            simulationLogs.append("\n");

            // Append server queues information
            for (int i = 0; i < this.numberOfServers; i++) {
                Server server = this.scheduler.getServers().get(i);

                simulationLogs.append("Queue ").append(i + 1).append(": ");

                if (server.getTasks().length == 0) {
                    simulationLogs.append("closed\n");
                } else {
                    for (Task task : server.getTasks()) {
                        simulationLogs.append("(").append(task.getId()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append("); ");
                    }
                    simulationLogs.append("\n");
                }
            }

            simulationLogs.append("\n");

            this.currentSimulationTime.setText("Simulation time: " + currentTime);
            this.simulationLogs.setText(simulationLogs.toString());
        });
    }

    private void updateServerStates() {
        for (Server server : this.scheduler.getServers()) {
            if (server.getTasks().length > 0) {
                Task firstTask = server.getTasks()[0];
                firstTask.setServiceTime(firstTask.getServiceTime() - 1);
                if (firstTask.getServiceTime() == 0) {
                    server.removeTask(firstTask);
                }
            }
        }
    }
}
