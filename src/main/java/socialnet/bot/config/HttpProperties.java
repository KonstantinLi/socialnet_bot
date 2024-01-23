package socialnet.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("http")
public class HttpProperties {
    private String url;
    private String agent;
}
