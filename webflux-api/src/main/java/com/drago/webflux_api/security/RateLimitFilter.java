package com.drago.webflux_api.security;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RateLimitFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final int MAX_REQUESTS_PER_MINUTE = 5;

    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Usa o IP como chave no Redis
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String redisKey = "rate_limit:" + ip;

        return redisTemplate.opsForValue().increment(redisKey)
            .flatMap(requestCount -> {
                if (requestCount == 1) {
                    // Se for a primeira request, expira a chave em 60 segundos
                    return redisTemplate.expire(redisKey, Duration.ofMinutes(1))
                            .then(chain.filter(exchange));
                }
                if (requestCount > MAX_REQUESTS_PER_MINUTE) {
                    // Se passar do limite, bloqueia com HTTP 429
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            });
    }
}