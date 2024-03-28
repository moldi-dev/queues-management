package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    /*
    * @param servers
    * @param task
    * @return void
    *
    * Selects the server with the lowest number of tasks in order to add a new task to it.
    */
    @Override
    public void addTask(List<Server> servers, Task task) {
        Server shortestQueue = servers.getFirst();

        for (Server server : servers) {
            if (server.getTasks().length < shortestQueue.getTasks().length) {
                shortestQueue = server;
            }
        }

        shortestQueue.addTask(task);
    }
}
