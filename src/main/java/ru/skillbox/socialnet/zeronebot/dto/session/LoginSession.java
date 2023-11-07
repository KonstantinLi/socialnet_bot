package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.LoginState;

@Data
@Builder
public class LoginSession {
    private Long chatId;
    private String email;
    private LoginState loginState;
}
