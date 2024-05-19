package com.practice.devy.global;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String name = this.name();
            log.info("global filter baseMessage: {}", config.baseMessage());

            if(config.preLogger()) {
                log.info("[{}] request ID --> {}", name, request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.postLogger()) {
                    log.info("[{}] response status code --> {}", name, response.getStatusCode());
                }
            }));
        });
    }

    public record Config(String baseMessage, boolean preLogger, boolean postLogger) {}
}
