package ru.skillbox.socialnet.zeronebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import ru.skillbox.socialnet.zeronebot.exception.IdException;
import ru.skillbox.socialnet.zeronebot.exception.TokenException;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final Jedis jedis;

    public void saveToken(Long id, String token) throws IOException {
        if (id == null) {
            throw new IdException();
        }

        String key = id.toString();
        long expired = 12L * 60 * 60;

        jedis.set(key, token);
        jedis.expire(key, expired);
    }

    public String getToken(Long id) throws IOException {
        if (id == null) {
            throw new IdException();
        }

        return Optional.of(jedis.get(id.toString()))
                .orElseThrow(() -> new TokenException(id));
    }

    public void deleteToken(Long id) throws IOException {
        if (id == null) {
            throw new IdException();
        }

        jedis.del(id.toString());
    }
}
