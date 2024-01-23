package socialnet.bot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.dto.enums.state.PostState;
import socialnet.bot.dto.request.PostRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.PostSessionService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static socialnet.bot.constant.Common.WITHOUT_TAGS;

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
    public void handle(SessionRq request) throws Exception {
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
