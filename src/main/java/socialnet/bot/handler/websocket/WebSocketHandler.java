package socialnet.bot.handler.websocket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import socialnet.bot.dto.websocket.MessageWs;
import socialnet.bot.service.TelegramService;

import java.lang.reflect.Type;

public class WebSocketHandler implements StompSessionHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private final Long chatId;
    private final Long userId;
    private final Long dialogId;
    private final TelegramService telegramService;

    public WebSocketHandler(
            Long chatId,
            Long userId,
            Long dialogId,
            TelegramService telegramService) {

        this.chatId = chatId;
        this.userId = userId;
        this.dialogId = dialogId;
        this.telegramService = telegramService;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        String subscribe = String.format("/user/%d/queue/messages", dialogId);
        session.subscribe(subscribe, this);
    }

    @Override
    public void handleException(
            StompSession session,
            StompCommand command,
            StompHeaders headers,
            byte[] payload,
            Throwable exception) {}

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {}

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree((byte[]) payload);
            if (node.has("time")) {
                MessageWs messageWs = OBJECT_MAPPER.treeToValue(node, MessageWs.class);
                if (!messageWs.getAuthorId().equals(userId)) {
                    telegramService.sendMessage(chatId, messageWs.getMessageText());
                }
            } else if (!node.has("message_text") && !node.has("typing")) {
                throw new IllegalArgumentException("Unknown message type");
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
