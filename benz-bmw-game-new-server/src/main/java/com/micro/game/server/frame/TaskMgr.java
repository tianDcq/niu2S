package com.micro.game.server.frame;

import java.util.HashSet;
import java.util.Iterator;

public class TaskMgr {

    private HashSet<Task> tasks;

    public TaskMgr() {
        tasks = new HashSet<Task>();
    }

    public Task createTask(int time, Callback callback) {
        Task task = new Timer(time, callback);
        tasks.add(task);
        task.setTaskMgr(this);
        return task;
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public void removeAllTimers() {
        tasks.clear();
    }

    public void stopTimer(Task task) {
        task.stop();
    }

    public void stopAllTimers() {
        for (Task task : tasks) {
            task.stop();
        }

        tasks.clear();
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