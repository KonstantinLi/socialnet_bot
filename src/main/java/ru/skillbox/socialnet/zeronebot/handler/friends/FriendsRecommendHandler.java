package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FriendsService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_RECOMMENDS;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_PERSON;

@Component
@RequiredArgsConstructor
public class FriendsRecommendHandler extends UserRequestHandler {
    private final FriendsService friendsService;
    private final PersonService personService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update  = request.getUpdate();

        return isTextMessage(update, FRIENDS_RECOMMENDS) ||
                isCallback(update, PREV_PERSON) ||
                isCallback(update, NEXT_PERSON);
    }

    @Override
    public void handle(UserRq request) throws IOException {
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
