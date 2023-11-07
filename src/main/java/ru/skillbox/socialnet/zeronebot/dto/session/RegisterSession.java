package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.RegisterState;

@Data
@Builder
public class RegisterSession {
    private Long chatId;
    private String name;
    private String email;
    private String password;
    private String passwordConfirm;
    private String captchaCode;
    private RegisterState registerState;
}
