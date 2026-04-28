package org.example.rawabet.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String email = jwtService.extractEmail(token);

                    if (email != null) {
                        User user = userRepository.findByEmail(email).orElse(null);

                        if (user != null) {
                            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                                    .flatMap(role -> Stream.concat(
                                            Stream.of(new SimpleGrantedAuthority(role.getName())),
                                            role.getPermissions().stream()
                                                    .map(perm -> new SimpleGrantedAuthority(perm.getName()))
                                    ))
                                    .toList();

                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null, authorities);

                            accessor.setUser(auth);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[WebSocket] Token JWT invalide : " + e.getMessage());
                }
            }
        }

        return message;
    }
}
