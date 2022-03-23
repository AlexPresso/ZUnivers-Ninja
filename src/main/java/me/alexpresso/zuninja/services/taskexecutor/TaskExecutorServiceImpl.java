package me.alexpresso.zuninja.services.taskexecutor;

import me.alexpresso.zuninja.classes.projection.action.Action;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TaskExecutorServiceImpl implements TaskExecutorService {

    @Override
    public void runTasks(final Set<Action> actions) {
        for(final var action : actions) {
            if(action.getRunnable().isEmpty())
                continue;

            action.getRunnable().get().run();
        }
    }
}
