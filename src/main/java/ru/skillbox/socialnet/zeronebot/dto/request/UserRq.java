package ru.skillbox.socialnet.zeronebot.dto.request;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;

@Data
@Builder
public class UserRq {
    private Update update;
    private Long chatId;
    private UserSession userSession;
    private LoginSession loginSession;
    private RegisterSession registerSession;
}
