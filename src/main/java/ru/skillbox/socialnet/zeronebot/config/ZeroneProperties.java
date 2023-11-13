package ru.skillbox.socialnet.zeronebot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("zerone")
public class ZeroneProperties {
    private String pageSize;
    private String photo;
    private String welcome;
}
