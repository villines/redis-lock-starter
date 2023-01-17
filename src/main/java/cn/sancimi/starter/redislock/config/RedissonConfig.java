package cn.sancimi.starter.redislock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

    /**
     * redis属性配置
     */
    private RedissonProperties properties;

    /**
     * 构造函数
     *
     * @param properties
     */
    public RedissonConfig(RedissonProperties properties) {
        this.properties = properties;
    }

    /**
     * 单机模式
     * 依次设置redis地址和密码
     */
    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + properties.getHost() + ":" + properties.getPort()).setPassword(properties.getPassword());
        return Redisson.create(config);
    }

}
