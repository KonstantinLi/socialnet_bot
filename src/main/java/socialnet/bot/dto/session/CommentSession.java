package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.CommentState;
import socialnet.bot.dto.request.CommentRq;

@Data
@Builder
public class CommentSession {
    private Long chatId;
    private Long postId;
    private Long parentId;
    private Integer index;
    private Integer subIndex;
    private CommentRq comment;
    private CommentState commentState;
}
