package com.jobder.app.chat.config;

import com.jobder.app.authentication.config.JwtService;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.exceptions.WebSocketException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.Optional;

@AllArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private UserRepository userRepository;

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            assert authorizationHeader != null;
            String token = authorizationHeader.substring(7);

            String username = jwtService.getUsernameFromToken(token);

            if(username == null)
                throw new WebSocketException("Need authentication!");

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            accessor.setUser(usernamePasswordAuthenticationToken);
        } else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            Principal user = accessor.getUser();
            UserDetails actualUser = userDetailsService.loadUserByUsername(user.getName());

            User completeUser = userRepository.findByEmail(actualUser.getUsername()).orElseThrow(()->new WebSocketException("No exists user!"));

            if(!completeUser.getId().equals(accessor.getDestination().split("/")[2]))
                throw new WebSocketException("You are not allowed to join this channel");
        }

        return message;
    }
}
