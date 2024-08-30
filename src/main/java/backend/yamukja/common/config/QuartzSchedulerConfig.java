package backend.yamukja.common.config;

import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.job.PlaceJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class QuartzSchedulerConfig {

    private final CustomSpringBeanJobFactory jobFactory;
    private final QuartzProperties quartzProperties;

    @Bean
    public JobDetail placeJobDetail() {
        return JobBuilder.newJob(PlaceJob.class)
                .withIdentity("placeJobDetail", "group1")
                .storeDurably()
                .build();
    }

    /**
     * 매일 오전 6시에 Quartz Scheduler 가 시작됩니다.
     * @param url
     * @param apiName
     * @param intervalInSeconds
     * @return
     */
    private Trigger createTrigger(String url, String apiName, int intervalInSeconds) {
        String cronExpression = "0 0 6 * * ?";

        return TriggerBuilder.newTrigger()
                .forJob(placeJobDetail())
                .withIdentity("placeJobTrigger_" + apiName, "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData("url", url)
                .usingJobData("apiName", apiName)
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());

        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

        // 각 URL에 대한 트리거를 생성하고 추가합니다.
        Trigger chineseTrigger = createTrigger(Constants.CHINESE_URL_TEMPLATE, "Chinese", 10);
        Trigger japaneseTrigger = createTrigger(Constants.JAPANESE_URL_TEMPLATE, "Japanese", 10);
        Trigger fastFoodTrigger = createTrigger(Constants.FASTFOOD_URL_TEMPLATE, "Fastfood", 10);

        schedulerFactoryBean.setJobDetails(placeJobDetail());
        schedulerFactoryBean.setTriggers(chineseTrigger, japaneseTrigger, fastFoodTrigger);

        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start(); // 스케줄러를 시작합니다.
        return scheduler;
    }
}

