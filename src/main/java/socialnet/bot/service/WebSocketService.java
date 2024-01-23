package socialnet.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import socialnet.bot.dto.websocket.MessageWs;
import socialnet.bot.handler.websocket.WebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final TokenService tokenService;
    private final TelegramService telegramService;

    @Value("${websocket.url}")
    private String url;

    public StompSession connect(Long userId, Long chatId, Long dialogId) throws IOException {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("token", tokenService.getToken(userId));

        SockJsClient sockJsClient = new SockJsClient(transports);
        StompSessionHandler stompHandler =
                new WebSocketHandler(chatId, userId, dialogId, telegramService);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stompClient.connect(url, handshakeHeaders, connectHeaders, stompHandler).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendMessage(StompSession session, Long userId, Long dialogId, String message) throws IOException {
        MessageWs messageWs = new MessageWs();
        messageWs.setAuthorId(userId);
        messageWs.setMessageText(message);
        messageWs.setToken(tokenService.getToken(userId));

        StompHeaders headers = new StompHeaders();
        headers.add("destination", "/api/v1/dialogs/send_message");
        headers.add("dialog_id", dialogId.toString());

        session.send(headers, messageWs);
    }
}
