package backend.yamukja.common.service;


import backend.yamukja.common.constant.RedisConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void pushApiState(String apiName, int totalCount) {
        String apiKey = RedisConstants.API_NAME + apiName;
        hashOperations.put(apiKey, RedisConstants.CURRENT_INDEX_FIELD, String.valueOf(1));
        hashOperations.put(apiKey, RedisConstants.TOTAL_COUNT_FIELD, String.valueOf(totalCount));
    }

    public int getCurrentIndex(String apiName) {
        String apiKey = RedisConstants.API_NAME + apiName;
        String currentIndexStr = hashOperations.get(apiKey, RedisConstants.CURRENT_INDEX_FIELD);
        return currentIndexStr != null ? Integer.parseInt(currentIndexStr) : 1;
    }

    public int getTotalCount(String apiName) {
        String apiKey = RedisConstants.API_NAME + apiName;
        String totalCountStr = hashOperations.get(apiKey, RedisConstants.TOTAL_COUNT_FIELD);
        return totalCountStr != null ? Integer.parseInt(totalCountStr) : 0;
    }

    public void setCurrentIndex(String apiName, Integer currentIndex) {
        hashOperations.put(RedisConstants.API_NAME + apiName, "currentIndex", String.valueOf(currentIndex));
    }
}
