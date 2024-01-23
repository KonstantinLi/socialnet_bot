package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.RegisterRq;

@Data
@Builder
public class RegisterSession {
    private Long chatId;
    private RegisterRq registerRq;
    private RegisterState registerState;
}
