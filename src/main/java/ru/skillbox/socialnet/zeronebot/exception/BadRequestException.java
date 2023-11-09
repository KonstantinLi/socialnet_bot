package ru.skillbox.socialnet.zeronebot.exception;

import lombok.Getter;
import ru.skillbox.socialnet.zeronebot.dto.response.ErrorRs;

@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorRs errorRs;

    public BadRequestException(ErrorRs errorRs) {
        super(errorRs.getErrorDescription());
        this.errorRs = errorRs;
    }
}
