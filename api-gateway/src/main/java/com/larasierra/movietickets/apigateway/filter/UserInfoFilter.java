package com.larasierra.movietickets.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserInfoFilter implements GlobalFilter {

    private static final String ID_TOKEN_HEADER = "IdToken";

    /**
     * Add IdToken header to requests
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getPath().value().equals("/stripe/webhook")
            && exchange.getRequest().getMethod().equals(HttpMethod.POST)
        ) {
            return chain.filter(exchange);
        }

        if (exchange.getRequest().getPath().value().startsWith("/v3/api-docs/")) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(DefaultOidcUser.class)
                .map(user -> user.getIdToken().getTokenValue())
                .map(idToken ->
                    exchange.mutate().request(
                        exchange.getRequest()
                                .mutate()
                                .header(ID_TOKEN_HEADER, idToken)
                                .build()
                    ).build()
                )
                .onErrorReturn(exchange)
                .flatMap(chain::filter);
    }
}
