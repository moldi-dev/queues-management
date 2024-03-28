package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler implements Strategy {
    private List<Server> servers;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.servers = new ArrayList<>();
        this.strategy = new TimeStrategy();

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            Thread serverThread = new Thread(server);
            serverThread.start();
            servers.add(server);
        }
    }

    public List<Server> getServers() {
        return this.servers;
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    @Override
    public void addTask(List<Server> servers, Task task) {

    }
}
