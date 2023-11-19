package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.request.LoginRq;

@Data
@Builder
public class LoginSession {
    private Long chatId;
    private LoginRq loginRq;
    private LoginState loginState;
}
