package socialnet.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    @Bean
    public Jedis getJedis() {
        return new Jedis("redis", 6379);
    }
}
