package ru.skillbox.socialnet.zeronebot.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.zeronebot.config.HttpProperties;
import ru.skillbox.socialnet.zeronebot.dto.request.LoginRq;
import ru.skillbox.socialnet.zeronebot.dto.request.RegisterRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.CaptchaRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HttpService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
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

    public PersonRs profile(UserRq userRq) throws IOException {
        Long id = userRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        URL url = new URL(properties.getUrl() + "/users/me");

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.treeToValue(node, PersonRs.class);
    }

    public PersonRs getPerson(UserRq userRq, Long personId) throws IOException {
        Long id = userRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        URL url = new URL(properties.getUrl() + "/users/" + personId);

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.treeToValue(node, PersonRs.class);
    }

    public List<PersonRs> friends(UserRq userRq) throws IOException {
        Long id = userRq.getUserSession().getId();
        String token = tokenService.getToken(id);

        URL url = new URL(properties.getUrl() + "/friends");

        String response = get(url, token);

        JsonNode node = OBJECT_MAPPER.readTree(response).get("data");
        return OBJECT_MAPPER.readValue(
                OBJECT_MAPPER.treeAsTokens(node),
                TypeFactory.defaultInstance().constructCollectionType(List.class, PersonRs.class)
        );
    }

    private String get(URL url) throws IOException {
        return get(url, null);
    }

    private String post(URL url, Object object) throws IOException {
        return post(url, null, object);
    }

    private String get(URL url, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("User-Agent", properties.getAgent());
        connection.setRequestProperty("Authorization", token);

        connection.connect();

        return response(connection);
    }

    private String post(URL url, String token, Object body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("User-Agent", properties.getAgent());
        connection.setRequestProperty("Authorization", token);
        connection.setDoOutput(true);

        OutputStream out = connection.getOutputStream();
        out.write(serialize(body).getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        connection.connect();

        return response(connection);
    }

    private String response(HttpURLConnection connection) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(),
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
