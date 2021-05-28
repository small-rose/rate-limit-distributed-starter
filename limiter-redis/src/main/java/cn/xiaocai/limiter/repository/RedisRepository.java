/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xiaocai.limiter.repository;


import cn.xiaocai.limiter.common.exception.RateLimitRepositoryException;
import cn.xiaocai.limiter.common.utils.LogUtil;
import cn.xiaocai.limiter.common.utils.StringUtils;
import cn.xiaocai.limiter.config.ConfigEnv;
import cn.xiaocai.limiter.config.RateLimitRedisConfig;
import cn.xiaocai.limiter.jedis.JedisClient;
import cn.xiaocai.limiter.jedis.JedisClientCluster;
import cn.xiaocai.limiter.jedis.JedisClientSentinel;
import cn.xiaocai.limiter.jedis.JedisClientSingle;
import cn.xiaocai.limiter.serializer.RateLimitSerializer;
import cn.xiaocai.limiter.spi.RateLimitSPI;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * redis impl.
 *
 * @author dzc
 */
@RateLimitSPI("redisRepository")
public class RedisRepository implements RateLimitRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisRepository.class);

    private static final String RateLimit_Key_GLOBAL = "RateLimit_key_global";

    private String rootPathPrefix = "rateLimit";
    
    private String keyPrefix = "-";
    
    private String appName;
    
    private RateLimitSerializer  rateLimitSerializer;
    
    private JedisClient jedisClient;
    
    @Override
    public void init(final String appName) {
        this.appName = appName;
        RateLimitRedisConfig RateLimitRedisConfig = ConfigEnv.getInstance().getConfig(cn.xiaocai.limiter.config.RateLimitRedisConfig.class);
        try {
            buildJedisPool(RateLimitRedisConfig);
        } catch (Exception e) {
            LOGGER.error("redis init error please check you config:{}", e.getMessage());
            throw new RateLimitRepositoryException(e);
        }
    }

    @Override
    public void setSerializer(RateLimitSerializer rateLimitSerializer) {
        this.rateLimitSerializer = rateLimitSerializer;
    }

    public JedisClient getJedisClient() {
        return jedisClient;
    }

    @Override
    public void assistantHandle() {

    }

    
    private String buildRateLimitRootPath() {
        return rootPathPrefix + keyPrefix + RateLimit_Key_GLOBAL;
    }
    

    private void buildJedisPool(final RateLimitRedisConfig RateLimitRedisConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(RateLimitRedisConfig.getMaxIdle());
        config.setMinIdle(RateLimitRedisConfig.getMinIdle());
        config.setMaxTotal(RateLimitRedisConfig.getMaxTotal());
        config.setMaxWaitMillis(RateLimitRedisConfig.getMaxWaitMillis());
        config.setTestOnBorrow(RateLimitRedisConfig.isTestOnBorrow());
        config.setTestOnReturn(RateLimitRedisConfig.isTestOnReturn());
        config.setTestWhileIdle(RateLimitRedisConfig.isTestWhileIdle());
        config.setMinEvictableIdleTimeMillis(RateLimitRedisConfig.getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(RateLimitRedisConfig.getSoftMinEvictableIdleTimeMillis());
        config.setTimeBetweenEvictionRunsMillis(RateLimitRedisConfig.getTimeBetweenEvictionRunsMillis());
        config.setNumTestsPerEvictionRun(RateLimitRedisConfig.getNumTestsPerEvictionRun());
        JedisPool jedisPool;
        if (RateLimitRedisConfig.isCluster()) {
            LogUtil.info(LOGGER, () -> "build redis cluster ............");
            final String clusterUrl = RateLimitRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts =
                    Lists.newArrayList(Splitter.on(";")
                            .split(clusterUrl))
                            .stream()
                            .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else if (RateLimitRedisConfig.isSentinel()) {
            LogUtil.info(LOGGER, () -> "build redis sentinel ............");
            final String sentinelUrl = RateLimitRedisConfig.getSentinelUrl();
            final Set<String> hostAndPorts =
                    new HashSet<>(Lists.newArrayList(Splitter.on(";").split(sentinelUrl)));
            JedisSentinelPool pool =
                    new JedisSentinelPool(RateLimitRedisConfig.getMasterName(), hostAndPorts,
                            config, RateLimitRedisConfig.getTimeOut(), RateLimitRedisConfig.getPassword());
            jedisClient = new JedisClientSentinel(pool);
        } else {
            if (StringUtils.isNoneBlank(RateLimitRedisConfig.getPassword())) {
                jedisPool = new JedisPool(config, RateLimitRedisConfig.getHostName(), RateLimitRedisConfig.getPort(), RateLimitRedisConfig.getTimeOut(), RateLimitRedisConfig.getPassword());
            } else {
                jedisPool = new JedisPool(config, RateLimitRedisConfig.getHostName(), RateLimitRedisConfig.getPort(), RateLimitRedisConfig.getTimeOut());
            }
            jedisClient = new JedisClientSingle(jedisPool);
        }
    }
    
    /**
     * The interface Filter.
     *
     * @param <T> the type parameter
     */
    interface Filter<T> {
    
        /**
         * Filter boolean.
         *
         * @param t      the t
         * @param params the params
         * @return the boolean
         */
        boolean filter(T t, Object... params);
    }
    
}
