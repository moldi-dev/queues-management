package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task) {
        Server shortestServer = servers.getFirst();

        for (Server server : servers) {
            if (server.getTotalServiceTime() < shortestServer.getTotalServiceTime()) {
                shortestServer = server;
            }
        }

        shortestServer.addTask(task);
    }
}
