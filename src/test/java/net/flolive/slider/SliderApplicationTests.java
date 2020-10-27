package net.flolive.slider;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.flolive.slider.respository.SliderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SliderApplicationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private SliderRepository repository;

    private WebSocketClient webSocketClient;

    @BeforeEach
    public void setup() {
        webSocketClient = new ReactorNettyWebSocketClient();
    }

    @AfterEach
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    void testCorrectValues() {
        Map<String, String> testMessages = new HashMap<>() {

            {
                put("#000000", "{\"red\":0,\"green\":0,\"blue\":0}");
                put("#FFFFFF", "{\"red\":255,\"green\":255,\"blue\":255}");
                put("#808080", "{\"red\":128,\"green\":128,\"blue\":128}");
            }
        };

        webSocketClient.execute(URI.create(String.format("ws://localhost:%d/value", port)), session -> session
            .send(Flux.fromIterable(testMessages.values()).map(session::textMessage))
            .and(session.receive().map(WebSocketMessage::getPayloadAsText).map(response -> {
                assertTrue(testMessages.containsKey(response));
                return true;
            }).take(testMessages.size()))
            .then()).block(Duration.ofSeconds(10));

        assertEquals(1, repository.count());
    }

    @Test
    void testIncorrectValues() {
        webSocketClient.execute(URI.create(String.format("ws://localhost:%d/value", port)),
            session -> session.send(
                Mono.just("{\"red\":256,\"green\":0,\"blue\":0}").map(session::textMessage))
                .and(session.receive().map(WebSocketMessage::getPayloadAsText).map(response -> {
                    assertEquals("invalid value", response);
                    return true;
                }).take(1))
                .then()).block(Duration.ofSeconds(10));
        assertEquals(0, repository.count());
    }

    @Test
    void testInvalidJson() {
        webSocketClient.execute(URI.create(String.format("ws://localhost:%d/value", port)), session -> session
            .send(Mono.just("{random:0}").map(session::textMessage))
            .and(session.receive().map(WebSocketMessage::getPayloadAsText).map(response -> {
                assertEquals("error", response);
                return true;
            }).take(1))
            .then()).block(Duration.ofSeconds(10));

        assertEquals(0, repository.count());
    }
}
