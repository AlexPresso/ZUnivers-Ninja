package me.alexpresso.zuninja.services.taskexecutor;

import me.alexpresso.zuninja.classes.projection.action.Action;

import java.util.Set;

public interface TaskExecutorService {
    void runTasks(Set<Action> actions);
}
