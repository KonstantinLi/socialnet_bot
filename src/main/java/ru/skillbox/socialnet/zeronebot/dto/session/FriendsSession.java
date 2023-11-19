package ru.skillbox.socialnet.zeronebot.dto.session;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;

import java.util.List;

@Data
@Builder
public class FriendsSession {
    private Long chatId;
    private Integer index;
    private FriendsState friendsState;
    private List<PersonRs> friends;
}
