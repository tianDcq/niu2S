package com.micro.frame;

import java.util.HashSet;
import java.util.Iterator;

public class TaskMgr {

    private HashSet<Task> tasks = new HashSet<Task>();
    private HashSet<Task> addTasks = new HashSet<Task>();
    private HashSet<Task> removeTasks = new HashSet<Task>();
    private boolean safe = true;

    int getTaskSize() {
        return tasks.size();
    }

    private void safeAdd(Task task) {
        (safe ? tasks : addTasks).add(task);
    }

    private void safeRemove(Task task) {
        (safe ? tasks : removeTasks).remove(task);
    }

    private void safeFlash() {
        if (safe) {
            if (addTasks.size() > 0) {
                tasks.addAll(addTasks);
                addTasks.clear();
            }

            if (removeTasks.size() > 0) {
                tasks.removeAll(removeTasks);
                removeTasks.clear();
            }
        }
    }

    public Timer createTimer(float time, Callback callback) {
        Timer task = new Timer(time, callback);
        safeAdd(task);
        return task;
    }

    public Trigger createTrigger(Callback callback) {
        Trigger task = new Trigger(callback);
        safeAdd(task);
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
        return createSchedule(callback, interval, repeat, delay, null);
    }

    public Timer createTimer(float time, Callback callback, Root target) {
        Timer task = new Timer(time, callback);
        task.setTarget(target);
        safeAdd(task);
        return task;
    }

    public Trigger createTrigger(Callback callback, Root target) {
        Trigger task = new Trigger(callback);
        task.setTarget(target);
        safeAdd(task);
        return task;
    }

    public Schedule createSchedule(Callback callback, Root target) {
        return createSchedule(callback, 0, -1, 0, target);

    }

    public Schedule createSchedule(Callback callback, float interval, Root target) {
        return createSchedule(callback, interval, -1, 0, target);

    }

    public Schedule createSchedule(Callback callback, float interval, int repeat, Root target) {
        return createSchedule(callback, interval, repeat, 0, target);
    }

    public Schedule createSchedule(Callback callback, float interval, int repeat, float delay, Root target) {
        Schedule task = new Schedule(callback, interval, repeat, delay);
        task.setTarget(target);
        safeAdd(task);
        return task;
    }

    public void remove(Task task) {
        safeRemove(task);
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

    void doPrepare() {
        // TODO
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
        safe = false;
        tasks.removeIf(task -> {
            task.update();
            return task.isExpired();
        });
        safe = true;
        safeFlash();
    }
}