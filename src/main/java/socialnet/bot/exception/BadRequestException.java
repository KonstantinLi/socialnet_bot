package socialnet.bot.exception;

import lombok.Getter;
import socialnet.bot.dto.response.ErrorRs;

@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorRs errorRs;

    public BadRequestException(ErrorRs errorRs) {
        super(errorRs.getErrorDescription());
        this.errorRs = errorRs;
    }
}
