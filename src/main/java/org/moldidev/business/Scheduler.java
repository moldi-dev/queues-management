package org.moldidev.business;

import org.moldidev.controller.Controller;
import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler implements Strategy {
    private List<Server> servers;
    private Strategy strategy;
    private Controller controller;

    /*
    * @param maxNoServers
    * @param maxTasksPerServer
    *
    * The class' constructor initializes the servers list, the controller, the selection policy,
    * the strategy and the required number of servers (threads).
    */
    public Scheduler(int maxNoServers, int maxTasksPerServer, Controller controller) {
        this.servers = new ArrayList<>();
        this.controller = controller;

        if (this.controller.getSelectionPolicy() == 0) {
            this.strategy = new TimeStrategy();
        }

        else {
            this.strategy = new ShortestQueueStrategy();
        }

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            Thread serverThread = new Thread(server);
            serverThread.start();
            this.servers.add(server);
        }
    }

    public List<Server> getServers() {
        return this.servers;
    }

    /*
    * @param Task
    * @return void
    *
    * Calls the user's chosen strategy's "addTask" method.
    */
    public void dispatchTask(Task task) {
        this.strategy.addTask(this.servers, task);
    }

    @Override
    public void addTask(List<Server> servers, Task task) {

    }
}
