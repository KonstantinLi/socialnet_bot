package ru.skillbox.socialnet.zeronebot.dto.request;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.session.*;

@Data
@Builder
public class UserRq {
    private Long chatId;
    private Update update;
    private UserSession userSession;
    private PostSession postSession;
    private LoginSession loginSession;
    private DialogSession dialogSession;
    private FilterSession filterSession;
    private CommentSession commentSession;
    private FriendsSession friendsSession;
    private RegisterSession registerSession;
}
