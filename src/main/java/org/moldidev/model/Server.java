package org.moldidev.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    /*
    * @param maxTasksPerServer
    *
    * The class' constructor initializes a new server (queue) which can hold up to "maxTasksPerServer" tasks.
    */
    public Server(int maxTasksPerServer) {
        this.tasks = new ArrayBlockingQueue<>(maxTasksPerServer);
        this.waitingPeriod = new AtomicInteger(0);
    }

    public Task[] getTasks() {
        return this.tasks.toArray(new Task[0]);
    }

    public AtomicInteger getWaitingPeriod() {
        return this.waitingPeriod;
    }

    /*
    * @param newTask
    * @return void
    *
    * Adds a new task to the server and increments its current waiting period.
    */
    public synchronized void addTask(Task newTask) {
        this.tasks.add(newTask);
        this.waitingPeriod.incrementAndGet();
    }

    /*
    * @param taskToRemove
    * @return void
    *
    * Removes a task from the server based on its identifier.
    */
    public synchronized void removeTask(Task taskToRemove) {
        this.tasks.removeIf(task -> task.getId() == taskToRemove.getId());
    }

    /*
    * @return int
    *
    * Computes the server's total service time.
    */
    public synchronized int getTotalServiceTime() {
        int totalServiceTime = 0;

        for (Task task : this.tasks) {
            totalServiceTime += task.getServiceTime();
        }

        return totalServiceTime;
    }

    /*
    * @return void
    *
    * Gets the first task in queue, waits a number of seconds equal to the task's service time and then decrements the server's waiting period.
    */
    @Override
    public void run() {
        Task task = this.tasks.poll();

        if (task != null) {
            try {
                Thread.sleep(task.getServiceTime());
            }

            catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            finally {
                this.waitingPeriod.decrementAndGet();
            }
        }
    }
}
