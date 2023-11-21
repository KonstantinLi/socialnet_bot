package ru.skillbox.socialnet.zeronebot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.PostState;
import ru.skillbox.socialnet.zeronebot.dto.request.PostRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.PostSessionService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.skillbox.socialnet.zeronebot.constant.Common.WITHOUT_TAGS;

@Component
@RequiredArgsConstructor
public class PostTagsHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        PostState postState = request.getPostSession().getPostState();
        return isTextMessage(request.getUpdate()) && postState == PostState.TAGS_WAIT;
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        PostSession postSession = request.getPostSession();

        PostRq postRq = postSession.getPublish();

        if (!isTextMessage(request.getUpdate(), WITHOUT_TAGS)) {
            String tagString = request.getUpdate().getMessage().getText();
            Set<String> tags = Arrays.stream(tagString.split("\\s+"))
                    .filter(tag -> tag.startsWith("#"))
                    .map(tag -> tag.replace("#", ""))
                    .collect(Collectors.toSet());
            postRq.setTags(tags);
        }

        httpService.createPost(request, postRq);

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(
                chatId,
                "Ваш новый пост успешно опубликован",
                keyboardRemove);

        postSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
