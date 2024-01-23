package socialnet.bot.exception;

import java.io.IOException;

public class TokenException extends IOException {
    public TokenException(Long id) {
        super("Токен для пользователя id-" + id + " отсутствует");
    }
}
