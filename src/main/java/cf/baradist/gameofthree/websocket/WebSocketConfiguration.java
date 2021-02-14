package cf.baradist.gameofthree.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/game-of-three-websocket").withSockJS();
        registry.addEndpoint("/queue")
                //Method .addInterceptors for enabling interceptor
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setHandshakeHandler(new MyHandler())
                .withSockJS();
    }

}

class MyHandler extends DefaultHandshakeHandler {


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

//Get the Username object which you have saved as session objects

        String name = (String) attributes.get("name");

        //Return the User
        return () -> name;
//        return new UsernamePasswordAuthenticationToken(name, null);
    }
}
