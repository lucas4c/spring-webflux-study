package com.drago.webflux_api.security;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.net.InetSocketAddress;

@Component
public class RateLimitFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final int MAX_REQUESTS_PER_MINUTE = 5;

    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        
        // Se for chamada de teste onde o RemoteAddress é null, pula o rate limit
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return chain.filter(exchange);
        }

        String ip = remoteAddress.getAddress().getHostAddress();
        String redisKey = "rate_limit:" + ip;

        return redisTemplate.opsForValue().increment(redisKey)
            .flatMap(requestCount -> {
                if (requestCount == 1) {
                    return redisTemplate.expire(redisKey, Duration.ofMinutes(1))
                            .then(chain.filter(exchange));
                }
                if (requestCount > MAX_REQUESTS_PER_MINUTE) {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            });
    }
}