package socialnet.bot.dto.request;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.session.*;

@Data
@Builder
public class SessionRq {
    private Long chatId;
    private Update update;
    private UserSession userSession;
    private EditSession editSession;
    private PostSession postSession;
    private LoginSession loginSession;
    private DialogSession dialogSession;
    private FilterSession filterSession;
    private CommentSession commentSession;
    private FriendsSession friendsSession;
    private RegisterSession registerSession;
}
