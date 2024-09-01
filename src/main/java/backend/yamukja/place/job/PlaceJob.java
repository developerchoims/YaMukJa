package backend.yamukja.place.job;

import backend.yamukja.common.service.RedisService;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.controller.PlaceController;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlaceJob implements Job {

    @Autowired
    private PlaceController placeController;

    @Autowired
    private RedisService redisService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String[] urls = {
                Constants.CHINESE_URL_TEMPLATE,
                Constants.JAPANESE_URL_TEMPLATE,
                Constants.FASTFOOD_URL_TEMPLATE
        };

        for (String url : urls) {
            runJobForUrl(url);
        }
    }

    public void runJobForUrl(String url) throws JobExecutionException {
        boolean shouldContinue = true;

        while (shouldContinue) {
            placeController.getFoodInfo(100, null, null, url);

            // Redis에서 현재 page index 상태를 가져옵니다
            String[] info = Constants.URL_INFO_MAP.get(url);
            int currentIndex = redisService.getCurrentIndex(info[1]);
            int totalCount = redisService.getTotalCount(info[1]);

            // 총 페이지 수 계산
            int totalPages = (int) Math.ceil((double) totalCount / 100);
            log.info("URL :: {} Current Index :: {} Total Pages :: {}", url, currentIndex, totalPages);

            // 현재 인덱스가 총 페이지 수를 초과하면 다음 URL로 넘어갑니다.
            if (currentIndex >= totalPages - 1) {
                shouldContinue = false;
            }

            // 다음 실행 전 대기 (10초)
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JobExecutionException(e);
            }
        }
    }

}

