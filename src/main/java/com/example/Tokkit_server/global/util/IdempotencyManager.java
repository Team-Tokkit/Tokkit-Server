package com.example.Tokkit_server.global.util;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static Duration TTL_PROCESS = Duration.ofMinutes(5);
    private final static Duration TTL_RESPONSE = Duration.ofMinutes(10);

    /** (읽어보세용)
     * 멱등성 보장 실행 로직
     * @param key 프론트에서 헤더로 받은 Idempotency-Key
     * @param action 중복 방지를 적용 비즈니스 로직
     * @param <T> 실행 결과 타입
     * @return 캐싱된 결과 or 새로 실행한 결과
     */
    public <T> T execute(String key, Supplier<T> action) {

        // Redis 키 : 처리 중 여부 확인용
        String processKey = "idempotency:" + key;

        // Redis 키 구성 : 처리 결과 캐싱용
        String resultKey = processKey + ":result";

        // 처리 중 상태 등록 : 동시에 2개 이상 처리되지 않도록 락 역할을 함
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(processKey, "IN_PROGRESS", TTL_PROCESS);
        log.info("Redis Set: key={}, locked={}", processKey, locked);


        if (Boolean.FALSE.equals(locked)) { // 이미 처리 중인 요청일 경우

            // 결과 캐시가 존재하면 그것을 반환
            T cached = (T) redisTemplate.opsForValue().get(resultKey);
            log.info("Redis Get: key={}, result={}", resultKey, cached); //  캐시된 응답 로그



            if (cached != null) return cached;

            // 결과 캐시도 없으면 중복 요청으로 판단해 에러 발생
            throw new GeneralException(ErrorStatus.DUPLICATE_REQUEST); //
        }

        try {
            // 실제 로직 실행 (Wallet 서비스 로직 클래스 (Command, Query)
            T result = action.get();

            // 실행 결과를 캐시에 저장 (나중에 같은 키로 재요청 시 즉시 반환)
            redisTemplate.opsForValue().set(resultKey, result, TTL_RESPONSE);
            return result;
        } catch (Exception e) {

            // 실패한 경우 잠금 키 삭제 → 다른 요청이 다시 시도할 수 있도록
            redisTemplate.delete(processKey); // 실패 하면 롤백 처리
            throw e;
        }
    }
}
