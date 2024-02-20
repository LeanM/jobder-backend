package com.jobder.app.chat.config;

import com.jobder.app.authentication.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = extractTokenFromURI(servletRequest);

            if (token != null && jwtService.isTokenValid(token)) {
                String email = jwtService.getUsernameFromToken(token);
                attributes.put("username", email);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private String extractTokenFromURI(ServletServerHttpRequest request) {
        // Implement logic to extract JWT token from URI
        String access_token = request.getURI().getQuery();
        return access_token.substring(13);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Do nothing
    }
}
