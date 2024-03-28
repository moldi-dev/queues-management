package org.moldidev.model;

public class Task implements Comparable<Task> {
    private int id;
    private int arrivalTime;
    private int serviceTime;

    /*
    * @param id
    * @param arrivalTime
    * @param serviceTime
    *
    * The class' constructor initializes a new task with the given parameters.
    */
    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getId() {
        return this.id;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getServiceTime() {
        return this.serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    /*
    * @param other
    * @return int
    *
    * Compares two "Task" objects based on their arrival time.
    */
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.arrivalTime, other.arrivalTime);
    }
}
