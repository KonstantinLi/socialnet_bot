package ru.skillbox.socialnet.zeronebot.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorRs {
    private String error;
    private Long timestamp;
    private String errorDescription;

    public ErrorRs(RuntimeException exception) {
        this.error = exception.getClass().getSimpleName();
        this.errorDescription = exception.getLocalizedMessage();
        this.timestamp = new Date().getTime();
    }
}
