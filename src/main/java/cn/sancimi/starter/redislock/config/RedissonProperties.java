package cn.sancimi.starter.redislock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prop.redis.lock.single")
public class RedissonProperties {

    /**
     * redis单点主机地址
     */
    private String host;

    /**
     * redis单点主机端口
     */
    private String port;

    /**
     * redis单点主机密码
     */
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
