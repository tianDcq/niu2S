package com.micro.frame;

import java.util.HashSet;
import java.util.Iterator;

public class TaskMgr {

    private HashSet<Task> tasks = new HashSet<Task>();

    public Timer createTimer(int time, Callback callback) {
        Timer task = new Timer(time, callback);
        tasks.add(task);
        return task;
    }

    public Trigger createTrigger(Callback callback) {
        Trigger task = new Trigger(callback);
        tasks.add(task);
        return task;
    }

    public Schedule createSchedule(Callback callback) {
        return createSchedule(callback, 0, -1, 0);

    }

    public Schedule createSchedule(Callback callback, float interval) {
        return createSchedule(callback, interval, -1, 0);

    }

    public Schedule createSchedule(Callback callback, float interval, int repeat) {
        return createSchedule(callback, interval, repeat, 0);
    }

    public Schedule createSchedule(Callback callback, float interval, int repeat, float delay) {
        Schedule task = new Schedule(callback, interval, repeat, delay);
        tasks.add(task);
        return task;
    }

    public void remove(Task task) {
        tasks.remove(task);
    }

    public void removeAll() {
        tasks.clear();
    }

    public void stop(Task task) {
        task.stop();
    }

    public void stopAll() {
        for (Task task : tasks) {
            task.stop();
        }

        tasks.clear();
    }

    void doStop() {
        // TODO
    }

    void doDestroy() {
        // TODO
    }

    void doTerminate() {
        // TODO
    }

    public void update() {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            task.update();
            if (task.isExpired()) {
                it.remove();
            }
        }
    }
}