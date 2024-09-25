package com.amplifiers.pathfinder.config;

import static com.amplifiers.pathfinder.utility.Variables.ClientSettings.CLIENT_BASE_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Integer tokenBeginsAtIndex = 7;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:5173", CLIENT_BASE_URL, "https://pathphindr.netlify.app", "https://www.pathphindr.com")
            .withSockJS();
        registry.addEndpoint("/ws");
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    //    @Override
    //    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    //        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    //        resolver.setDefaultMimeType(APPLICATION_JSON);
    //        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    //        converter.setObjectMapper(new ObjectMapper());
    //        converter.setContentTypeResolver(resolver);
    //        messageConverters.add(converter);
    //
    //        return false;
    //    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            new ChannelInterceptor() {
                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {
                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                    System.out.println("hello there");
                    assert accessor != null;
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                        assert authorizationHeader != null;
                        String token = authorizationHeader.substring(tokenBeginsAtIndex);

                        System.out.println("token " + token);
                        String username = jwtService.extractUsername(token);
                        System.out.println("username " + username);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                        accessor.setUser(usernamePasswordAuthenticationToken);
                    }

                    return message;
                }
            }
        );
    }
}
