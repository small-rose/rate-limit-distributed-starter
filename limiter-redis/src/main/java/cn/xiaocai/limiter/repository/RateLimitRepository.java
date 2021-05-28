package cn.xiaocai.limiter.repository;

import cn.xiaocai.limiter.serializer.RateLimitSerializer;

/**
 * @program: rate-limit-distributed-starter
 * @description
 * @function:
 * @author: zzy
 * @create: 2021-05-28 15:44
 **/
public interface RateLimitRepository {

    /**
     * Init.
     *
     * @param appName the app name
     */
    void init(String appName);

    /**
     * Sets serializer.
     *
     * @param rateLimitSerializer the RateLimitSerializer
     */
    void setSerializer(RateLimitSerializer rateLimitSerializer);

    /**
     * assist handle
     *
     */
    void assistantHandle();
}
