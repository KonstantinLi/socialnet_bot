package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.LoginState;
import socialnet.bot.dto.request.LoginRq;

@Data
@Builder
public class LoginSession {
    private Long chatId;
    private LoginRq loginRq;
    private LoginState loginState;
}
