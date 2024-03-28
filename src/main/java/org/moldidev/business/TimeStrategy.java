package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {
    /*
    * @param servers
    * @param task
    * @return void
    *
    * Selects the server with the lowest total service time in order to add a new task to it.
    */
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
