package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.EditState;
import socialnet.bot.dto.request.EditRq;

@Data
@Builder
public class EditSession {
    private Long chatId;
    private EditRq editRq;
    private EditState editState;
}
