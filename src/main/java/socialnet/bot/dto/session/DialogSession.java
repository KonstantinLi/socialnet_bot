package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import org.springframework.messaging.simp.stomp.StompSession;
import socialnet.bot.dto.response.DialogRs;

import java.util.List;

@Data
@Builder
public class DialogSession {
    private Long id;
    private Long chatId;
    private Integer index;
    private List<DialogRs> dialogs;
    private StompSession stompSession;
}
