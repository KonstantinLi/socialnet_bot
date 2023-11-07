package ru.skillbox.socialnet.zeronebot.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRq {
    private String code;
    private String codeSecret;
    private String email;
    private String firstName;
    private String lastName;
    private String passwd1;
    private String passwd2;
}
