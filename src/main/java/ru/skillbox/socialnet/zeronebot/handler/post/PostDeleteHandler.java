package ru.skillbox.socialnet.zeronebot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Post.POST_DELETE;

@Component
@RequiredArgsConstructor
public class PostDeleteHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), POST_DELETE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        Long postId = messageService.getIdFromCallback(request, POST_DELETE.getCommand());

        httpService.deletePost(request, postId);
        telegramService.sendMessage(chatId, "<b>Пост удален</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
