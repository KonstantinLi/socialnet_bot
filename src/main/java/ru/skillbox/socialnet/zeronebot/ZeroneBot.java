package ru.skillbox.socialnet.zeronebot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.skillbox.socialnet.zeronebot.config.BotProperties;
import ru.skillbox.socialnet.zeronebot.constant.Menu;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.*;
import ru.skillbox.socialnet.zeronebot.service.session.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZeroneBot extends TelegramLongPollingBot {
    private final UserSessionService userSessionService;
    private final PostSessionService postSessionService;
    private final LoginSessionService loginSessionService;
    private final DialogSessionService dialogSessionService;
    private final CommentSessionService commentSessionService;
    private final RegisterSessionService registerSessionService;
    private final FriendsSessionService friendsSessionService;
    private final FilterSessionService filterSessionService;

    private final BotProperties botProperties;
    private final Dispatcher dispatcher;

    @Autowired
    public ZeroneBot(
            UserSessionService userSessionService,
            PostSessionService postSessionService,
            LoginSessionService loginSessionService,
            DialogSessionService dialogSessionService,
            CommentSessionService commentSessionService,
            RegisterSessionService registerSessionService,
            FriendsSessionService friendsSessionService,
            FilterSessionService filterSessionService,
            BotProperties botProperties,
            Dispatcher dispatcher) {

        this.userSessionService = userSessionService;
        this.postSessionService = postSessionService;
        this.loginSessionService = loginSessionService;
        this.dialogSessionService = dialogSessionService;
        this.commentSessionService = commentSessionService;
        this.registerSessionService = registerSessionService;
        this.friendsSessionService = friendsSessionService;
        this.filterSessionService = filterSessionService;
        this.botProperties = botProperties;
        this.dispatcher = dispatcher;

        try {
            execute(new SetMyCommands(commands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return botProperties.getKey();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() ||
            update.hasCallbackQuery()) {

            Long chatId = update.getMessage() != null ? update.getMessage().getChatId() :
                    update.getCallbackQuery().getMessage().getChatId();

            UserSession userSession = userSessionService.getSession(chatId);
            LoginSession loginSession = loginSessionService.getSession(chatId);
            DialogSession dialogSession = dialogSessionService.getSession(chatId);
            CommentSession commentSession = commentSessionService.getSession(chatId);
            RegisterSession registerSession = registerSessionService.getSession(chatId);
            FriendsSession friendsSession = friendsSessionService.getSession(chatId);
            FilterSession filterSession = filterSessionService.getSession(chatId);
            PostSession postSession = postSessionService.getSession(chatId);

            UserRq userRq = UserRq
                    .builder()
                    .update(update)
                    .userSession(userSession)
                    .postSession(postSession)
                    .loginSession(loginSession)
                    .dialogSession(dialogSession)
                    .commentSession(commentSession)
                    .registerSession(registerSession)
                    .friendsSession(friendsSession)
                    .filterSession(filterSession)
                    .chatId(chatId)
                    .build();

            try {
                dispatcher.dispatch(userRq);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<BotCommand> commands() {
        List<BotCommand> commands = new ArrayList<>();
        for (Menu command : Menu.values()) {
            commands.add(new BotCommand(command.getCommand(), command.getText()));
        }
        return commands;
    }
}
