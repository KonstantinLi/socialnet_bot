package socialnet.bot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.FriendsState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.dto.session.FriendsSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FriendsService;
import socialnet.bot.service.PersonService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Friends.FRIENDS_RECOMMENDS;
import static socialnet.bot.constant.Navigate.NEXT_PERSON;
import static socialnet.bot.constant.Navigate.PREV_PERSON;

@Component
@RequiredArgsConstructor
public class FriendsRecommendHandler extends UserRequestHandler {
    private final FriendsService friendsService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_RECOMMENDS) ||
                isCallback(update, PREV_PERSON) ||
                isCallback(update, NEXT_PERSON);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Update update = request.getUpdate();
        FriendsSession friendsSession = request.getFriendsSession();

        if (isTextMessage(update, FRIENDS_RECOMMENDS)) {
            friendsService.friendsRecommend(request);

        } else if (friendsSession.getFriendsState() == FriendsState.RECOMMENDS) {
            personService.navigatePerson(request);
        }

        List<PersonRs> recommendations = friendsSession.getFriends();

        if (recommendations == null ||
                recommendations.isEmpty() ||
                friendsSession.getFriendsState() != FriendsState.RECOMMENDS) {
            return;
        }

        int index = Optional.ofNullable(request.getFriendsSession().getIndex()).orElse(0);
        PersonRs recommendFriend = recommendations.get(index);
        personService.sendPersonDetailsNavigate(request, recommendFriend);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
