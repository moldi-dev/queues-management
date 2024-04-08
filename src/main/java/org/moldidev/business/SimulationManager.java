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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationManager implements Runnable {
    private final Controller controller;
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int numberOfServers;
    private int numberOfClients;
    private int maxArrivalTime;
    private int minArrivalTime;
    private TextArea simulationLogs;
    private Label validInputLabel;
    private Label invalidInputLabel;
    private Label simulationStatusLabel;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private double averageServiceTime = 0.0f;
    private double averageWaitingTime = 0.0f;
    private double averageArrivalTime = 0.0f;
    private int peakHour = 0;
    private int peakHourTotalTasks = 0;

    /*
    * @param controller
    *
    * The class' constructor initializes the UI fields and calls the "generateNRandomTasks()" method.
    */
    public SimulationManager(Controller controller) {
        this.generatedTasks = new ArrayList<>();

        this.controller = controller;

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
        this.invalidInputLabel = this.controller.getInvalidInputLabel();

        this.scheduler = new Scheduler(this.numberOfServers, this.numberOfClients / this.numberOfServers + 1, this.controller);

        generateNRandomTasks();
    }

    /*
     * @return void
     *
     * Generates N = numberOfClients random tasks, with the arrival time intervals and processing time intervals specified by the user.
     */
    private void generateNRandomTasks() {
        for (int i = 1; i <= this.numberOfClients; i++) {
            Task task = new Task(i, getRandomNumber(this.minArrivalTime, this.maxArrivalTime), getRandomNumber(this.minProcessingTime, this.maxProcessingTime));
            this.generatedTasks.add(task);
        }

        Collections.sort(this.generatedTasks);
    }

    /*
    * @param min
    * @param max
    * @return int
    *
    * Returns a random integer in the [min, max] interval
    */
    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /*
    * @return void
    *
    * Runs the thread
    */
    @Override
    public void run() {
        // Initialize the current time, simulation logs string, disable the inputs in the UI and set the simulation starting message

        int currentTime = 0;
        StringBuilder simulationLogs = new StringBuilder();

        this.controller.disableInputs();

        Platform.runLater(() -> {
            this.invalidInputLabel.setText("");
            this.validInputLabel.setText("The simulation has been successfully set up! Performing the simulation...");
        });

        // While the simulation is running, dispatch the required tasks, update the simulation logs,
        // wait one second and update the servers' states and increment the "currentTime" variable
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
        } while (!((this.generatedTasks.isEmpty() && areAllServersIdle()) || (currentTime > this.timeLimit)));

        // The simulation is done, so finish computing the average service and waiting times and append to the simulation logs
        // all the necessary output

        if (this.generatedTasks.isEmpty() && areAllServersIdle()) {
            simulationLogs.append("Time ").append(currentTime).append("\n");

            for (int i = 0; i < this.numberOfServers; i++) {
                simulationLogs.append("Queue ").append(i + 1).append(" (size = 0): closed").append("\n");
            }

            simulationLogs.append("\n");
        }

        this.averageServiceTime /= this.numberOfClients;
        this.averageWaitingTime /= this.numberOfClients;
        this.averageArrivalTime /= this.numberOfClients;

        simulationLogs.append("Peak hour: ").append(this.peakHour).append("\n");
        simulationLogs.append("Average arrival time: ").append(this.averageArrivalTime).append("\n");
        simulationLogs.append("Average service time: ").append(this.averageServiceTime).append("\n");
        simulationLogs.append("Average waiting time: ").append(this.averageWaitingTime).append("\n");

        saveSimulationLogsToFile(simulationLogs.toString(), "simulationLogs.txt");

        this.simulationLogs.setText(simulationLogs.toString());
        this.simulationLogs.positionCaret(this.simulationLogs.getText().length());

        this.controller.enableInputs();

        Platform.runLater(() -> {
            this.validInputLabel.setText("");
            this.simulationStatusLabel.setText("Simulation status: inactive");
        });
    }

    private void saveSimulationLogsToFile(String simulationLog, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(simulationLog);
            writer.newLine();
            writer.newLine();
            writer.newLine();
            writer.write("-----------------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
            writer.newLine();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @return boolean
     *
     * Checks if all the servers are in idle state (i.e. they have no tasks).
     */
    private boolean areAllServersIdle() {
        for (Server server : this.scheduler.getServers()) {
            if (server.getTasks().length > 0) {
                return false;
            }
        }

        return true;
    }

    /*
    * @param currentTime
    * @return void
    *
    * Dispatches the tasks which have their arrival time less than or equal to the current thread time.
    * Updates the peak hour (or peak time) and computes the sum of the dispatched tasks' service and waiting times.
    */
    private void dispatchTasks(int currentTime) {
        Iterator<Task> iterator = this.generatedTasks.iterator();
        int totalTasks = 0;

        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.getArrivalTime() <= currentTime && !isTaskDispatched(task)) {
                this.scheduler.dispatchTask(task);
                this.averageServiceTime += task.getServiceTime();
                this.averageArrivalTime += task.getArrivalTime();

                for (Server server : this.scheduler.getServers()) {
                    totalTasks += server.getTasks().length;

                    for (int i = 0; i < server.getTasks().length; i++) {
                        if (i == 0) {
                            this.averageWaitingTime += server.getTasks()[i].getServiceTime();
                        }

                        else {
                            for (int k = 0; k <= i; k++) {
                                this.averageWaitingTime += server.getTasks()[k].getServiceTime();
                            }
                        }
                    }
                }

                iterator.remove();
            }
        }

        if (totalTasks > this.peakHourTotalTasks) {
            this.peakHour = currentTime;
            this.peakHourTotalTasks = totalTasks;
        }
    }

    /*
    * @param task
    * @return boolean
    *
    * Checks if the given task has been already dispatched.
    */
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

    /*
    * @param currentTime
    * @param simulationLogs
    * @return void
    *
    * Updates the simulation logs (i.e. shows on the UI log of events which clients are waiting, the current simulation time and the queues).
    */
    private void updateSimulationLogsAndTime(int currentTime, StringBuilder simulationLogs) {
        Platform.runLater(() -> {
            boolean areClientsWaiting = false;
            this.simulationStatusLabel.setText("Simulation status: active\nCurrent simulation time: " + currentTime);

            simulationLogs.append("Time ").append(currentTime).append("\n");
            simulationLogs.append("Waiting clients: ");

            for (Task task : this.generatedTasks) {
                if (task.getArrivalTime() > currentTime) {
                    simulationLogs.append("(").append(task.getId()).append(", ").append(task.getArrivalTime()).append(", ").append(task.getServiceTime()).append("); ");
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
                        simulationLogs.append("(").append(task.getId()).append(", ").append(task.getArrivalTime()).append(", ").append(task.getServiceTime()).append("); ");
                    }
                    simulationLogs.append("\n");
                }
            }

            simulationLogs.append("\n");

            this.simulationLogs.setText(simulationLogs.toString());
            this.simulationLogs.positionCaret(this.simulationLogs.getText().length());
        });
    }

    /*
    * @return void
    *
    * Updates the current states of the servers (i.e. decreases at each moment the service time of each task by 1
    * and removes the tasks with a service time equal to 0).
    */
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
