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
    public Label validInputLabel;
    public Label simulationStatusLabel;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private double averageServiceTime = 0.0f;
    private double averageWaitingTime = 0.0f;
    private int peakHour = 0;
    private int peakHourTotalTasks = 0;

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
        this.validInputLabel = this.controller.getValidInputLabel();
        this.simulationStatusLabel = this.controller.getSimulationStatusLabel();

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

        Platform.runLater(() -> this.validInputLabel.setText("The simulation has been successfully set up! Performing the simulation..."));

        do {
            dispatchTasks(currentTime);

            updateSimulationLogsAndTime(currentTime, simulationLogs);

            try {
                Thread.sleep(1000);
            }

            catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            updateServerStates();

            currentTime++;
        } while (!((generatedTasks.isEmpty() && areAllServersIdle()) || (currentTime > this.timeLimit)));

        this.averageServiceTime /= this.numberOfClients;
        this.averageWaitingTime /= this.numberOfClients;

        simulationLogs.append("Time: ").append(currentTime).append("\n");
        simulationLogs.append("Waiting clients: none").append("\n");

        for (int i = 0; i < this.numberOfServers; i++) {
            simulationLogs.append("Queue ").append(i + 1).append(": closed").append("\n");
        }

        simulationLogs.append("\n");

        simulationLogs.append("Average waiting time: ").append(this.averageWaitingTime).append("\n");
        simulationLogs.append("Peak hour: ").append(this.peakHour).append("\n");
        simulationLogs.append("Average service time: ").append(this.averageServiceTime).append("\n");
        this.simulationLogs.setText(simulationLogs.toString());
        this.simulationLogs.positionCaret(this.simulationLogs.getText().length());

        this.controller.enableInputs();

        Platform.runLater(() -> {
            this.validInputLabel.setText("");
            this.simulationStatusLabel.setText("Simulation status: inactive");
        });
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
        int totalTasks = 0;

        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.getArrivalTime() <= currentTime && !isTaskDispatched(task)) {
                this.scheduler.dispatchTask(task);
                this.averageServiceTime += task.getServiceTime();
                this.averageWaitingTime += task.getServiceTime();
                iterator.remove();
            }
        }

        for (Server server : this.scheduler.getServers()) {
            totalTasks += server.getTasks().length;
        }

        if (totalTasks > this.peakHourTotalTasks) {
            this.peakHour = currentTime;
            this.peakHourTotalTasks = totalTasks;
        }

//        for (Server server : this.scheduler.getServers()) {
//            this.averageWaitingTime += server.getWaitingPeriod().doubleValue();
//        }
    }

    private boolean isTaskDispatched(Task task) {
        for (Server server : this.scheduler.getServers()) {
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
            boolean areClientsWaiting = false;
            this.simulationStatusLabel.setText("Simulation status: active\nCurrent simulation time: " + currentTime);

            simulationLogs.append("Time ").append(currentTime).append("\n");
            simulationLogs.append("Waiting clients: ");

            for (Task task : this.generatedTasks) {
                if (task.getArrivalTime() > currentTime) {
                    simulationLogs.append("(").append(task.getId()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append("); ");
                    areClientsWaiting = true;
                }
            }

            if (!areClientsWaiting) {
                simulationLogs.append("none");
            }

            simulationLogs.append("\n");

            for (int i = 0; i < this.numberOfServers; i++) {
                Server server = this.scheduler.getServers().get(i);

                simulationLogs.append("Queue ").append(i + 1).append(" (size = ").append(server.getTasks().length).append("): ");

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

            this.simulationLogs.setText(simulationLogs.toString());
            this.simulationLogs.positionCaret(this.simulationLogs.getText().length());
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
