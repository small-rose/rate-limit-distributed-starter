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

package cn.xiaocai.limiter.config;

import cn.xiaocai.limiter.spi.RateLimitSPI;
import lombok.Data;

import java.util.Map;

/**
 * The HmilyRedisConfig.
 *
 * @author xiaoyu
 */
@Data
@RateLimitSPI("rateLimitRedisConfig")
public class RateLimitRedisConfig implements IConfig{

    public static final String REDIS_PREFIX = "rateLimit.repository.redis";

    private boolean cluster;

    private boolean sentinel;

    /**
     * cluster url example:ip:port;ip:port.
     */
    private String clusterUrl;

    /**
     * sentinel url example:ip:port;ip:port.
     */
    private String sentinelUrl;

    private String masterName;

    private String hostName;

    private int port;

    private String password;

    private int maxTotal = 8;

    private int maxIdle = 8;

    private int minIdle;

    private long maxWaitMillis = -1;

    private long minEvictableIdleTimeMillis = 1800000;

    private long softMinEvictableIdleTimeMillis = 1800000;

    private int numTestsPerEvictionRun = 3;

    private boolean testOnCreate;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean testWhileIdle;

    private long timeBetweenEvictionRunsMillis = -1;

    private boolean blockWhenExhausted = true;

    private int timeOut = 10000;
    
    @Override
    public String prefix() {
        return REDIS_PREFIX;
    }

    @Override
    public boolean isLoad() {
        return false;
    }

    @Override
    public void flagLoad() {

    }

    @Override
    public boolean isPassive() {
        return false;
    }

    @Override
    public void setSource(Map<String, Object> t) {

    }

    @Override
    public Map<String, Object> getSource() {
        return null;
    }
}
