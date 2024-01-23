package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.SessionState;

@Data
@Builder
public class UserSession {
    private Long id;
    private Long chatId;
    private Integer page;
    private SessionState sessionState;
}
