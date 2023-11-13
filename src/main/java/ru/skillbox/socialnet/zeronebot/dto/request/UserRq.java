package ru.skillbox.socialnet.zeronebot.dto.request;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.session.*;

@Data
@Builder
public class UserRq {
    private Update update;
    private Long chatId;
    private UserSession userSession;
    private LoginSession loginSession;
    private RegisterSession registerSession;
    private FriendsSession friendsSession;
    private FilterSession filterSession;
}
