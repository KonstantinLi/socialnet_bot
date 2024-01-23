package socialnet.bot.exception;

import java.io.IOException;

public class IdException extends IOException {
    public IdException() {
        super("Id отсутствует");
    }
}
