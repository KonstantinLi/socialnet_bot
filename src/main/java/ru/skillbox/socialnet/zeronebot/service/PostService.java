package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.exception.OutOfListException;
import ru.skillbox.socialnet.zeronebot.service.session.PostSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Like.LIKE_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Like.UNLIKE_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.*;


@Service
@RequiredArgsConstructor
public class PostService {
    private final HttpService httpService;
    private final FormatService formatService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final PostSessionService postSessionService;

    @Value("${zerone.page_size}")
    private Integer pageSize;

    public void navigatePost(UserRq request, Long authorId) throws IOException {
        Long chatId = request.getChatId();
        String callbackData = request.getUpdate().getCallbackQuery().getData();

        UserSession userSession = request.getUserSession();
        PostSession postSession = request.getPostSession();

        int page = Optional.ofNullable(userSession.getPage()).orElse(0);
        int index = Optional.ofNullable(postSession.getIndex()).orElse(0);

        List<PostRs> posts = postSession.getPosts();

        if (callbackData.equals(PREV_POST) || callbackData.startsWith(PREV_USER_POST)) {
            if (index == 0 && page > 0) {
                posts = callbackData.startsWith(PREV_USER_POST) ?
                        wall(request, authorId, --page) :
                        httpService.feeds(request, --page, pageSize);

                index = pageSize - 1;

                userSession.setPage(page);
                postSession.setPosts(posts);
            } else {
                index--;
            }
            postSession.setIndex(Math.max(index, 0));

        } else if (callbackData.startsWith(LIKE_POST.getCommand()) ||
                    callbackData.startsWith(UNLIKE_POST.getCommand()) ||
                    callbackData.equals(NEXT_POST) ||
                    callbackData.startsWith(NEXT_USER_POST)) {

            if (++index >= posts.size()) {
                posts = callbackData.startsWith(NEXT_USER_POST) ?
                        wall(request, authorId, ++page) :
                        httpService.feeds(request, ++page, pageSize);

                index = 0;

                userSession.setPage(page);
                postSession.setPosts(posts);
            }
            postSession.setIndex(index);
        }

        if (index == 0 && posts.isEmpty()) {
            userSession.setPage(0);
            postSessionService.deleteSession(chatId);
            throw new OutOfListException();
        }

        userSessionService.saveSession(chatId, userSession);
        postSessionService.saveSession(chatId, postSession);
    }

    public void feeds(UserRq request) throws IOException {
        Long chatId = request.getChatId();

        UserSession userSession = request.getUserSession();
        PostSession postSession = request.getPostSession();

        List<PostRs> posts = httpService.feeds(request, 0, pageSize);

        postSession.setIndex(0);
        postSession.setPosts(posts);
        postSessionService.saveSession(chatId, postSession);

        userSession.setPage(0);
        userSessionService.saveSession(chatId, userSession);
    }

    public void wall(UserRq request, Long authorId) throws IOException {
        Long chatId = request.getChatId();

        UserSession userSession = request.getUserSession();
        PostSession postSession = request.getPostSession();

        List<PostRs> posts = wall(request, authorId, 0);

        postSession.setIndex(0);
        postSession.setPosts(posts);
        postSessionService.saveSession(chatId, postSession);

        userSession.setPage(0);
        userSessionService.saveSession(chatId, userSession);
    }

    private List<PostRs> wall(UserRq request, Long authorId, int page) throws IOException {
        Long id = request.getUserSession().getId();

        return (id.equals(authorId) ?
                httpService.myWall(request, page, pageSize) :
                httpService.wall(request, authorId, page, pageSize));
    }

    public void sendPostDetailsNavigate(UserRq userRq, PostRs postRs, boolean userPost) {
        InlineKeyboardMarkup markupInLine;

        if (userPost) {
            markupInLine = keyboardService.buildUserPostMenuNavigate(
                    postRs,
                    userRq.getUserSession().getId());
        } else {
            markupInLine = keyboardService.buildPostMenuNavigate(postRs);
        }

        telegramService.sendMessage(userRq.getChatId(),
                formatService.formatPost(postRs),
                markupInLine);
    }
    public PostRs getPostById(UserRq userRq, List<PostRs> posts, Long id) throws IOException {
        return Optional.ofNullable(posts)
                .orElse(new ArrayList<>())
                .stream()
                .filter(post1 -> post1.getId().equals(id))
                .findAny()
                .orElse(httpService.getPostById(userRq, id));
    }
}
