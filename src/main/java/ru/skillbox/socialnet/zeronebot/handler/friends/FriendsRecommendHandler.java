package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.FriendsState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.PersonService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Callback.NEXT_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Callback.PREV_PERSON;
import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_RECOMMENDS;

@Component
@RequiredArgsConstructor
public class FriendsRecommendHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PersonService personService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final FriendsSessionService friendsSessionService;

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
            friendsRecommends(request);

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

    private void friendsRecommends(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        FriendsSession friendsSession = request.getFriendsSession();

        List<PersonRs> recommendations = httpService.recommendations(request);

        friendsSession.setIndex(0);
        friendsSession.setFriends(recommendations);
        friendsSession.setFriendsState(FriendsState.RECOMMENDS);
        friendsSessionService.saveSession(chatId, friendsSession);

        telegramService.sendMessage(chatId, messageService.recommend(recommendations.size()));
    }


    @Override
    public boolean isGlobal() {
        return false;
    }
}
