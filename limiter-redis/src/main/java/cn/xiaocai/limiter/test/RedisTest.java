package cn.xiaocai.limiter.test;

import cn.xiaocai.limiter.config.ConfigEnv;
import cn.xiaocai.limiter.config.ConfigScan;
import cn.xiaocai.limiter.config.IConfig;
import cn.xiaocai.limiter.config.RateLimitRedisConfig;
import cn.xiaocai.limiter.repository.RateLimitRepository;
import cn.xiaocai.limiter.spi.ExtensionLoaderFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @program: rate-limit-distributed-starter
 * @description
 * @function:
 * @author: zzy
 * @create: 2021-05-28 16:43
 **/
public class RedisTest {
    public static void main(String[] args) {
        ConfigScan.scan();
        RateLimitRedisConfig redisConfig = ConfigEnv.getInstance().getConfig(RateLimitRedisConfig.class);
        System.out.println("load -- >" + redisConfig);

        RateLimitRepository repository = ExtensionLoaderFactory.load(RateLimitRepository.class,"redisRepository");
        System.out.println("repository -- >" + repository);

        ServiceLoader<RateLimitRedisConfig> load = ServiceLoader.load(RateLimitRedisConfig.class);
        Iterator<RateLimitRedisConfig> iterator = load.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }


        RateLimitRedisConfig myTest = (RateLimitRedisConfig) ExtensionLoaderFactory.load(IConfig.class,"rateLimitRedisConfig");
        System.out.println(myTest);
    }
}
