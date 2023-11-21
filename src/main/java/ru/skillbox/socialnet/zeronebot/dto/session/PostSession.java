package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.PostState;
import ru.skillbox.socialnet.zeronebot.dto.request.PostRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;

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
