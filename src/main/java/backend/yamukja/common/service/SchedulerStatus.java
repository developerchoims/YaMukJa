package backend.yamukja.common.service;

import jakarta.annotation.PostConstruct;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerStatus {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void checkSchedulerStatus() throws SchedulerException {
        if (scheduler.isStarted()) {
            System.out.println("-- Scheduler is RUNNING --");
        } else {
            System.out.println("-- Scheduler is NOT RUNNING --");
        }
    }
}
