package net.flolive.slider.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.flolive.slider.entity.SliderEntity;
import net.flolive.slider.respository.SliderRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.*;
import reactor.core.publisher.Mono;

@Component("SliderWebSocketHandler")
public class SliderWebSocketHandler implements WebSocketHandler {

    public static final SliderEntity SLIDER_ENTITY = new SliderEntity(0);

    ObjectMapper mapper = new ObjectMapper();

    private final SliderRepository repository;

    public SliderWebSocketHandler(SliderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(webSocketSession.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .map(this::process)
            .map(webSocketSession::textMessage));
    }

    private String process(String s) {
        try {
            Value value = mapper.readValue(s, Value.class);

            SliderEntity entity = repository.findById(0).orElse(SLIDER_ENTITY);

            if (isInvalid(value.getRed()) || isInvalid(value.getBlue()) || isInvalid(value.getGreen())) {
                return "invalid value";
            }

            entity.setR(value.getRed());
            entity.setG(value.getGreen());
            entity.setB(value.getBlue());

            repository.save(entity);

            return value.toRGB();
        } catch (Exception e) {
            return "error";
        }
    }

    private boolean isInvalid(int value) {
        return value < 0 || value > 255;
    }
}
