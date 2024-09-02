package backend.yamukja.place.config;

import backend.yamukja.auth.service.TokenService;
import backend.yamukja.common.config.CustomSpringBeanJobFactory;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.job.PlaceJob;
import org.junit.jupiter.api.*;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan(basePackages = {"backend.yamukja.auth.filter"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuartzSchedulerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Scheduler getScheduler() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }


    /**
     * Quartz와 Spring의 통합을 위해 CustomSpringBeanJobFactory와 같은 클래스를 사용하여
     * Spring의 DI 컨테이너와 Quartz의 Job 인스턴스를 연결하는 작업이 필요합니다.
     */
    @Test
    @Order(1)
    @DisplayName("CustomSpringBeanJobFactory 가 올바르게 주입되는지 확인")
    void testCustomSpringBeanJobFactory() throws Exception {
        CustomSpringBeanJobFactory jobFactory = applicationContext.getBean(CustomSpringBeanJobFactory.class);
        assertNotNull(jobFactory);

        JobDetail jobDetail = JobBuilder.newJob(PlaceJob.class)
                .withIdentity("testJob", "testGroup")
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("testTrigger", "testGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ?"))
                .build();

        // CustomSpringBeanJobFactory가 AutowireBeanFactory를 사용하여 Job을 잘 생성하는지 확인
        Object jobInstance = jobFactory.createJobInstance(new TriggerFiredBundle(jobDetail, (OperableTrigger) trigger, null, false, null, null, null, null));
        assertNotNull(jobInstance);
        assertTrue(jobInstance instanceof PlaceJob);
    }

    @Test
    @Order(2)
    @DisplayName("placeJobDetail 이 잘 생성되었는지 확인")
    void testJobDetailConfiguration() throws Exception {
        Scheduler scheduler = getScheduler();

        JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey("placeJobDetail", "group1"));

        assertNotNull(jobDetail);
        assertEquals(PlaceJob.class.getName(), jobDetail.getJobClass().getName());
        assertEquals("placeJobDetail", jobDetail.getKey().getName());
        assertEquals("group1", jobDetail.getKey().getGroup());
    }

    @Test
    @Order(3)
    @DisplayName("url 별 trigger 들이 잘 생성되었는지 확인")
    void testTriggerConfiguration() throws Exception {
        Scheduler scheduler = getScheduler();

        Trigger chineseTrigger = scheduler.getTrigger(TriggerKey.triggerKey("placeJobTrigger_Chinese", "group1"));
        Trigger japaneseTrigger = scheduler.getTrigger(TriggerKey.triggerKey("placeJobTrigger_Japanese", "group1"));
        Trigger fastFoodTrigger = scheduler.getTrigger(TriggerKey.triggerKey("placeJobTrigger_Fastfood", "group1"));

        // 오전 6시에 실행되는지 cron expression 확인
        assertEquals("0 0 6 * * ?", ((CronTrigger) chineseTrigger).getCronExpression());
        assertEquals("0 0 6 * * ?", ((CronTrigger) japaneseTrigger).getCronExpression());
        assertEquals("0 0 6 * * ?", ((CronTrigger) fastFoodTrigger).getCronExpression());

        // Trigger 별 JobDataMap 데이터 확인
        JobDataMap chineseJobDataMap = chineseTrigger.getJobDataMap();
        assertEquals(Constants.CHINESE_URL_TEMPLATE, chineseJobDataMap.get("url"));
        assertEquals("Chinese", chineseJobDataMap.get("apiName"));

        JobDataMap japaneseJobDataMap = japaneseTrigger.getJobDataMap();
        assertEquals(Constants.JAPANESE_URL_TEMPLATE, japaneseJobDataMap.get("url"));
        assertEquals("Japanese", japaneseJobDataMap.get("apiName"));

        JobDataMap fastFoodJobDataMap = fastFoodTrigger.getJobDataMap();
        assertEquals(Constants.FASTFOOD_URL_TEMPLATE, fastFoodJobDataMap.get("url"));
        assertEquals("Fastfood", fastFoodJobDataMap.get("apiName"));
    }

    @Test
    @Order(4)
    @DisplayName("스케줄러가 예외 없이 초기화되고 시작 종료되는지 확인, shutdown() 으로 제일 마지막 Order 로 실행")
    void testSchedulerInitialization() throws Exception {
        Scheduler scheduler = getScheduler();
        assertNotNull(scheduler);
        assertDoesNotThrow(() -> scheduler.start());
        assertDoesNotThrow(() -> scheduler.shutdown(false));
    }
}
