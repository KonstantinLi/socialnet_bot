package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.CommentState;
import ru.skillbox.socialnet.zeronebot.dto.request.CommentRq;

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
