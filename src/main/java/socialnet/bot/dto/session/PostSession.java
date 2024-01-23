package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.PostState;
import socialnet.bot.dto.request.PostRq;
import socialnet.bot.dto.response.PostRs;

import java.util.List;

@Data
@Builder
public class PostSession {
    private Long chatId;
    private Long authorId;
    private Integer index;
    private PostRq publish;
    private List<PostRs> posts;
    private PostState postState;
}
