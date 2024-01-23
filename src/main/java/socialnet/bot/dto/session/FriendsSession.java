package socialnet.bot.dto.session;

import lombok.Builder;
import lombok.Data;
import socialnet.bot.dto.enums.state.FriendsState;
import socialnet.bot.dto.response.PersonRs;

import java.util.List;

@Data
@Builder
public class FriendsSession {
    private Long chatId;
    private Integer index;
    private FriendsState friendsState;
    private List<PersonRs> friends;
}
