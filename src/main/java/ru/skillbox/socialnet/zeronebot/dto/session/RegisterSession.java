package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.RegisterRq;

@Data
@Builder
public class RegisterSession {
    private Long chatId;
    private RegisterRq registerRq;
    private RegisterState registerState;
}
