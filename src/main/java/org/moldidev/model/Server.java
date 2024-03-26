package org.moldidev.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public Server(BlockingQueue<Task> tasks, AtomicInteger waitingPeriod) {
        this.tasks = tasks;
        this.waitingPeriod = waitingPeriod;
    }

    public Task[] getTasks() {
        return this.tasks.toArray(new Task[0]);
    }

    public AtomicInteger getWaitingPeriod() {
        return this.waitingPeriod;
    }

    public void setTasks(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public void addTask(Task newTask) {
        this.tasks.add(newTask);
        this.waitingPeriod.incrementAndGet();
    }

    public void removeTask(Task taskToRemove) {
        this.tasks.removeIf(task -> task.getId() == taskToRemove.getId());
    }

    public int getTotalServiceTime() {
        int totalServiceTime = 0;

        for (Task task : this.tasks) {
            totalServiceTime += task.getServiceTime();
        }

        return totalServiceTime;
    }

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
