package backend.yamukja.place.service;

import backend.yamukja.common.service.RedisService;
import backend.yamukja.place.constant.Constants;
import backend.yamukja.place.controller.PlaceController;
import backend.yamukja.place.job.PlaceJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collections;

import static backend.yamukja.place.constant.Constants.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * 빠른 테스트 실행을 위해 PlaceJob class 의 runJobForUrl 메서드 Thread.sleep(10000 -> 1000) 1초로 set 해주세요
 */
@ExtendWith(MockitoExtension.class)
public class PlaceJobExecuteTest {

    @InjectMocks
    private PlaceJob placeJob;

    @Mock
    private PlaceController placeController;

    @Mock
    private RedisService redisService;

    @DisplayName("redis 의 current index 와 total page 를 고려한 횟수만큼 각 url 을 호출하며 모든 데이터 저장 후 다음 url 로 넘어갑니다")
    @Test
    public void testExecuteWithMultipleUrls() throws JobExecutionException {
        // Given
        JobExecutionContext context = mock(JobExecutionContext.class);

        String[] urls = {
                CHINESE_URL_TEMPLATE,
                JAPANESE_URL_TEMPLATE,
                FASTFOOD_URL_TEMPLATE
        };

        for (String url : urls) {
            when(placeController.getFoodInfo(100, null, null, url))
                    .thenReturn(Collections.emptyList());
            String apiName = URL_INFO_MAP.get(url)[1];

            when(redisService.getCurrentIndex(apiName)).thenReturn(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            when(redisService.getTotalCount(apiName)).thenReturn(getTotalCountForUrl(url));
        }

        // When
        placeJob.execute(context);

        // Then
        verify(placeController, times(10)).getFoodInfo(100, null, null, CHINESE_URL_TEMPLATE);
        verify(placeController, times(5)).getFoodInfo(100, null, null, JAPANESE_URL_TEMPLATE);
        verify(placeController, times(2)).getFoodInfo(100, null, null, FASTFOOD_URL_TEMPLATE);

        verify(redisService, times(10)).getCurrentIndex(eq(URL_INFO_MAP.get(CHINESE_URL_TEMPLATE)[1]));
        verify(redisService, times(5)).getCurrentIndex(eq(URL_INFO_MAP.get(JAPANESE_URL_TEMPLATE)[1]));
        verify(redisService, times(2)).getCurrentIndex(eq(URL_INFO_MAP.get(FASTFOOD_URL_TEMPLATE)[1]));
    }

    private int getTotalCountForUrl(String url) {
        switch (url) {
            case CHINESE_URL_TEMPLATE:
                return 1000;  // 10 페이지
            case JAPANESE_URL_TEMPLATE:
                return 500;   // 5 페이지
            case Constants.FASTFOOD_URL_TEMPLATE:
                return 200;   // 2 페이지
            default:
                throw new IllegalArgumentException(Constants.INVALID_URL);
        }
    }
}
