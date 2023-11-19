package ru.skillbox.socialnet.zeronebot.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRq {
    private Set<String> tags;
    private String title;
    private String postText;
    private Boolean isDeleted;
    private LocalDateTime timeDelete;
}
