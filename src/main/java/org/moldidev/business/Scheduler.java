package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler implements Strategy {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler() {
        this.servers = new ArrayList<>();
        this.maxNoServers = 0;
        this.maxTasksPerServer = 0;
        this.strategy = new TimeStrategy();
    }

    public Scheduler(int maxNoServers, int maxTaskPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTaskPerServer;
        this.servers = new ArrayList<>();
        this.strategy = new TimeStrategy();

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            Thread serverThread = new Thread(server);
            serverThread.start();
            servers.add(server);
        }
    }

    public Scheduler(List<Server> servers, int maxNoServers, int maxTasksPerStrategy) {
        this.servers = servers;
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerStrategy;
    }

    public List<Server> getServers() {
        return this.servers;
    }

    public int getMaxNoServers() {
        return this.maxNoServers;
    }

    public int getMaxTasksPerServer() {
        return this.maxTasksPerServer;
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public void setMaxNoServers(int maxNoServers) {
        this.maxNoServers = maxNoServers;
    }

    public void setMaxTasksPerServer(int maxTasksPerServer) {
        this.maxTasksPerServer = maxTasksPerServer;
    }

    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if (selectionPolicy == SelectionPolicy.SHORTEST_TIME) {
            this.strategy = new TimeStrategy();
        }

        else if (selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            this.strategy = new ShortestQueueStrategy();
        }
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    @Override
    public void addTask(List<Server> servers, Task task) {

    }
}
