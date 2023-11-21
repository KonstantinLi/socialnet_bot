package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Person;
import ru.skillbox.socialnet.zeronebot.dto.request.DialogUserShortListRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.DialogRs;
import ru.skillbox.socialnet.zeronebot.dto.response.MessageRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.exception.OutOfListException;
import ru.skillbox.socialnet.zeronebot.service.session.DialogSessionService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Dialog.MESSAGE;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_DIALOG;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_DIALOG;

@Service
@RequiredArgsConstructor
public class DialogService {
    private final HttpService httpService;
    private final FormatService formatService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;
    private final DialogSessionService dialogSessionService;

    public void dialogs(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        DialogSession dialogSession = request.getDialogSession();

        List<DialogRs> dialogs = httpService.getDialogs(request)
                .stream()
                .sorted(dialogComparator())
                .toList();

        dialogSession.setIndex(0);
        dialogSession.setDialogs(dialogs);
        dialogSessionService.saveSession(chatId, dialogSession);

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(
                chatId,
                messageService.dialogs(dialogs.size()),
                keyboardRemove);
    }

    public void navigateDialog(UserRq request) {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        DialogSession dialogSession = request.getDialogSession();

        List<DialogRs> dialogs = dialogSession.getDialogs();
        int index = Optional.ofNullable(dialogSession.getIndex()).orElse(0);

        if (update.getCallbackQuery().getData().equals(PREV_DIALOG)) {
            dialogSession.setIndex(Math.max(--index, 0));

        } else if (update.getCallbackQuery().getData().equals(NEXT_DIALOG)) {
            dialogSession.setIndex(Math.min(dialogs.size() - 1, ++index));
        }

        if (index >= dialogs.size()) {
            dialogSessionService.deleteSession(chatId);
            throw new OutOfListException();
        }

        dialogSessionService.saveSession(chatId, dialogSession);
    }

    public void openDialog(UserRq userRq, DialogRs dialogRs) throws IOException {
        Long chatId = userRq.getChatId();
        Long dialogId = dialogRs.getId();

        DialogSession dialogSession = userRq.getDialogSession();

        int unreadCount = dialogRs.getUnreadCount().intValue();
        if (unreadCount > 0 && !dialogRs.getLastMessage().getIsSentByMe()) {
            List<MessageRs> messages = httpService.getUnreadMessages(userRq, dialogId)
                    .stream()
                    .sorted(Comparator.comparing(MessageRs::getTime))
                    .toList();
            messages.forEach(message -> telegramService.sendMessage(chatId, message.getMessageText()));
            httpService.readDialog(userRq, dialogId);
        }

        StompSession oldSession = dialogSession.getStompSession();
        if (oldSession != null) {
            oldSession.disconnect();
        }
    }

    public DialogRs getDialog(UserRq userRq) throws IOException {
        Long userId = userRq.getUserSession().getId();
        DialogSession dialogSession = userRq.getDialogSession();

        Update update = userRq.getUpdate();
        String callbackData = update.getCallbackQuery().getData();

        DialogRs dialog;
        if (callbackData.startsWith(Person.MESSAGE.getCommand())) {
            Long companionId = messageService.getIdFromCallback(userRq, Person.MESSAGE.getCommand());

            DialogUserShortListRq dialogRq = new DialogUserShortListRq();
            dialogRq.setUserId(userId);
            dialogRq.setUserIds(List.of(companionId));

            httpService.createDialog(userRq, dialogRq);

            dialog = httpService.getDialogs(userRq)
                    .stream()
                    .filter(dialog1 -> dialog1.getAuthorId().equals(companionId) ||
                            dialog1.getRecipientId().equals(companionId))
                    .findAny()
                    .orElse(null);
        } else {
            Long dialogId = messageService.getIdFromCallback(userRq, MESSAGE.getCommand());
            dialog = Optional.ofNullable(dialogSession.getDialogs())
                    .orElse(httpService.getDialogs(userRq))
                    .stream()
                    .filter(dialog1 -> dialog1.getId().equals(dialogId))
                    .findAny()
                    .orElse(null);
        }

        return dialog;
    }

    public void sendDialogDetailsNavigate(Long chatId, DialogRs dialogRs, PersonRs companion) {
        InlineKeyboardMarkup markupInLine = keyboardService.buildDialogMenuNavigate(dialogRs);
        telegramService.sendMessage(
                chatId,
                formatService.formatDialog(dialogRs, companion),
                markupInLine);
    }

    private Comparator<DialogRs> dialogComparator() {
        return (d1, d2) -> {
            boolean sentByMe1 = d1.getLastMessage().getIsSentByMe();
            boolean sentByMe2 = d2.getLastMessage().getIsSentByMe();
            if (!sentByMe1 && !sentByMe2) {
                return Long.compare(d2.getUnreadCount(), d1.getUnreadCount());
            } else if (sentByMe1) {
                return 1;
            } else {
                return -1;
            }
        };
    }
}
