package org.moldidev.business;

import org.moldidev.model.Server;
import org.moldidev.model.Task;

import java.util.List;

public interface Strategy {
    void addTask(List<Server> servers, Task task);
}
