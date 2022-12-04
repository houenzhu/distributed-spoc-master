package com.zhe.spoc.distributed.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.zhe.common.api.RedisConstants.CACHE_NULL_TTL;

/**
 * @author HouEnZhu
 * @ClassName CacheClient
 * @Description TODO
 * @date 2022/11/4 15:10
 * @Version 1.0
 */

@Slf4j
@Component
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 写入redis
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 防止缓存穿透
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param time
     * @param unit
     * @return
     * @param <R>
     * @param <T>
     */
    public <R, T> R queryWithPassThrough(String keyPrefix, T id, Class<R> type, Function<T, R> dbFallback,
                                          Long time, TimeUnit unit) {
        String key = keyPrefix + id;

        // 查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        // 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            return  JSONUtil.toBean(json, type);
        }
        // 判断命中的是否空值 ""
        if (json != null) {
            return null;
        }
        // 4. 不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 存在 写入redis
        this.set(key, JSONUtil.toJsonStr(r), time, unit);

        // 返回
        return r;
    }

}
