package ru.skillbox.socialnet.zeronebot.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.zeronebot.config.HttpProperties;
import ru.skillbox.socialnet.zeronebot.dto.request.*;
import ru.skillbox.socialnet.zeronebot.dto.response.*;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
import ru.skillbox.socialnet.zeronebot.exception.BadRequestException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class HttpService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private final HttpProperties properties;
    private final TokenService tokenService;

    public PersonRs login(LoginRq loginRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/auth/login");

        String response = post(url, null, loginRq);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        PersonRs personRs = OBJECT_MAPPER.treeToValue(node, PersonRs.class);

        tokenService.saveToken(personRs.getId(), personRs.getToken());

        return personRs;
    }

    public void logout(Long id) throws IOException {
        URL url = new URL(properties.getUrl() + "/auth/logout");
        String token = tokenService.getToken(id);

        post(url, token, null);

        tokenService.deleteToken(id);
    }

    public void register(RegisterRq registerRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/account/register");
        post(url, registerRq);
    }

    public CaptchaRs captcha() throws IOException {
        URL url = new URL(properties.getUrl() + "/auth/captcha");
        String response = get(url);
        return OBJECT_MAPPER.readValue(response, CaptchaRs.class);
    }

    public PersonRs profile(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/users/me");
        return getPerson(sessionRq, url);
    }

    public void editProfile(SessionRq sessionRq, EditRq editRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/users/me");

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, editRq);
    }

    public PersonRs getPersonById(SessionRq sessionRq, Long personId) throws IOException {
        URL url = new URL(properties.getUrl() + "/users/" + personId);
        return getPerson(sessionRq, url);
    }

    public List<PersonRs> recommendations(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/recommendations");
        return getPersons(sessionRq, url);
    }

    public List<PersonRs> incoming(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/request");
        return getPersons(sessionRq, url);
    }

    public List<PersonRs> outgoing(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/outgoing_requests");
        return getPersons(sessionRq, url);
    }

    public List<PersonRs> friends(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends");

        List<PersonRs> friends = getPersons(sessionRq, url);

        return friends.stream()
                .filter(friend -> {
                    Boolean isBlocked = friend.getIsBlocked();
                    Boolean isDeleted = friend.getUserDeleted();
                    return (isBlocked == null || !isBlocked) &&
                            (isDeleted == null || !isDeleted);
                })
                .sorted((fr1, fr2) -> Boolean.compare(fr2.getOnline(), fr1.getOnline()))
                .toList();
    }

    public List<PersonRs> search(SessionRq sessionRq) throws IOException {
        FilterSession filterSession = sessionRq.getFilterSession();

        Map<String, Object> filterMap =
                OBJECT_MAPPER.convertValue(filterSession, new TypeReference<>() {});

        filterMap.remove("chat_id");
        filterMap.remove("filter_state");

        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            if (entry.getValue() != null) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                joiner.add(key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }

        URL url = new URL(properties.getUrl() + "/users/search?" + joiner);
        return getPersons(sessionRq, url);
    }

    public void sendFriendship(SessionRq sessionRq, Long friendId) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/" + friendId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        post(url, token, null);
    }

    public void addFriend(SessionRq sessionRq, Long friendId) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/request/" + friendId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        post(url, token, null);
    }

    public void declineFriendship(SessionRq sessionRq, Long friendId) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/request/" + friendId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        delete(url, token);
    }

    public void deleteFriend(SessionRq sessionRq, Long friendId) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/" + friendId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        delete(url, token);
    }

    public void blockUser(SessionRq sessionRq, Long userId) throws IOException {
        URL url = new URL(properties.getUrl() + "/friends/block_unblock/" + userId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        post(url, token, null);
    }

    public List<PostRs> feeds(SessionRq sessionRq, Integer offset, Integer perPage) throws IOException {
        StringJoiner joiner = new StringJoiner("&");
        if (offset != null) {
            joiner.add("offset=" + offset);
        }
        if (perPage != null) {
            joiner.add("perPage=" + perPage);
        }

        URL url = new URL(properties.getUrl() + "/feeds?" + joiner);
        return getPosts(sessionRq, url);
    }

    public List<PostRs> myWall(SessionRq sessionRq, Integer offset, Integer perPage) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        return wall(sessionRq, id, offset, perPage);
    }

    public List<PostRs> wall(SessionRq sessionRq, Long id, Integer offset, Integer perPage) throws IOException {
        StringJoiner joiner = new StringJoiner("&");
        if (offset != null) {
            joiner.add("offset=" + offset);
        }
        if (perPage != null) {
            joiner.add("perPage=" + perPage);
        }

        URL url = new URL(String.format(
                "%s/users/%d/wall?%s",
                properties.getUrl(),
                id,
                joiner));

        return getPosts(sessionRq, url);
    }

    public void like(SessionRq sessionRq, LikeRq likeRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/likes");

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, likeRq);
    }

    public void unlike(SessionRq sessionRq, LikeRq likeRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/likes?" +
                "item_id=" + likeRq.getItemId() +
                "&type=" + likeRq.getType());

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        delete(url, token);
    }

    public void addComment(SessionRq sessionRq, Long postId, CommentRq commentRq) throws IOException {
        URL url = new URL(String.format(
                "%s/post/%d/comments",
                properties.getUrl(),
                postId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        post(url, token, commentRq);
    }

    public void editComment(SessionRq sessionRq,
                            Long postId,
                            Long commentId,
                            CommentRq commentRq) throws IOException {

        URL url = new URL(String.format(
                "%s/post/%d/comments/%d",
                properties.getUrl(),
                postId,
                commentId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, commentRq);
    }

    public void deleteComment(SessionRq sessionRq, Long postId, Long commentId) throws IOException {
        URL url = new URL(String.format(
                "%s/post/%d/comments/%d",
                properties.getUrl(),
                postId,
                commentId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        delete(url, token);
    }

    public void recoverComment(SessionRq sessionRq, Long postId, Long commentId) throws IOException {
        URL url = new URL(String.format(
                "%s/post/%d/comments/%d/recover",
                properties.getUrl(),
                postId,
                commentId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, null);
    }

    public void createPost(SessionRq sessionRq, PostRq postRq) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        URL url = new URL(String.format(
                "%s/users/%d/wall",
                properties.getUrl(),
                id));

        post(url, token, postRq);
    }

    public PostRs getPostById(SessionRq sessionRq, Long postId) throws IOException {
        URL url = new URL(properties.getUrl() + "/post/" + postId);
        return getPost(sessionRq, url);
    }

    public void deletePost(SessionRq sessionRq, Long postId) throws IOException {
        URL url = new URL(properties.getUrl() + "/post/" + postId);

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        delete(url, token);
    }

    public void recoverPost(SessionRq sessionRq, Long postId) throws IOException {
        URL url = new URL(String.format(
                "%s/post/%d/recover",
                properties.getUrl(),
                postId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, null);
    }

    public void readDialog(SessionRq sessionRq, Long dialogId) throws IOException {
        URL url = new URL(String.format(
                "%s/dialogs/%d/read",
                properties.getUrl(),
                dialogId));

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        put(url, token, null);
    }

    public List<DialogRs> getDialogs(SessionRq sessionRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/dialogs");
        return getDialogs(sessionRq, url);
    }

    public List<MessageRs> getUnreadMessages(SessionRq sessionRq, Long dialogId) throws IOException {
        URL url = new URL(String.format(
                "%s/dialogs/%d/unread",
                properties.getUrl(),
                dialogId));
        return getMessages(sessionRq, url);
    }

    public void createDialog(SessionRq sessionRq, DialogUserShortListRq dialogRq) throws IOException {
        URL url = new URL(properties.getUrl() + "/dialogs");

        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        post(url, token, dialogRq);
    }

    private PersonRs getPerson(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.treeToValue(node, PersonRs.class);
    }

    private List<PersonRs> getPersons(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.readValue(
                OBJECT_MAPPER.treeAsTokens(node),
                TypeFactory.defaultInstance().constructCollectionType(List.class, PersonRs.class)
        );
    }

    private PostRs getPost(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.treeToValue(node, PostRs.class);
    }

    private List<PostRs> getPosts(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.readValue(
                OBJECT_MAPPER.treeAsTokens(node),
                TypeFactory.defaultInstance().constructCollectionType(List.class, PostRs.class)
        );
    }

    private List<DialogRs> getDialogs(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.readValue(
                OBJECT_MAPPER.treeAsTokens(node),
                TypeFactory.defaultInstance().constructCollectionType(List.class, DialogRs.class)
        );
    }

    private List<MessageRs> getMessages(SessionRq sessionRq, URL url) throws IOException {
        Long id = sessionRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.readValue(
                OBJECT_MAPPER.treeAsTokens(node),
                TypeFactory.defaultInstance().constructCollectionType(List.class, MessageRs.class)
        );
    }

    private String get(URL url) throws IOException {
        return get(url, null);
    }

    private String post(URL url, Object object) throws IOException {
        return post(url, null, object);
    }

    private String get(URL url, String token) throws IOException {
        HttpURLConnection connection = connectionConfig(url, token, "GET");
        return connect(connection);
    }

    private String delete(URL url, String token) throws IOException {
        HttpURLConnection connection = connectionConfig(url, token, "DELETE");
        return connect(connection);
    }

    private String put(URL url, String token, Object body) throws IOException {
        HttpURLConnection connection = connectionConfig(url, token, "PUT");
        connection.setDoOutput(true);

        OutputStream out = connection.getOutputStream();
        out.write(serialize(body).getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        return connect(connection);
    }

    private String post(URL url, String token, Object body) throws IOException {
        HttpURLConnection connection = connectionConfig(url, token, "POST");
        connection.setDoOutput(true);

        OutputStream out = connection.getOutputStream();
        out.write(serialize(body).getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        return connect(connection);
    }

    private String connect(HttpURLConnection connection) throws IOException {
        try {
            connection.connect();
            return response(connection.getInputStream());
        } catch (IOException ex) {
            String error = response(connection.getErrorStream());
            JsonNode node = OBJECT_MAPPER.readTree(error);
            ErrorRs errorRs = OBJECT_MAPPER.treeToValue(node, ErrorRs.class);
            throw new BadRequestException(errorRs);
        }
    }

    private HttpURLConnection connectionConfig(URL url, String token, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("User-Agent", properties.getAgent());
        connection.setRequestProperty("Authorization", token);

        return connection;
    }

    private String response(InputStream inputStream) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream,
                        StandardCharsets.UTF_8));

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private String serialize(Object object) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }
}
