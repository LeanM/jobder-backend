package com.jobder.app.chat.chat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PushNotificationService {

    private final ChatNotificationRepository notificationRepository;

    private List<ChatNotification> getNotifs(String userId) {
        var notifs = notificationRepository.findByToUserIdAndDeliveredFalse(userId);
        notifs.forEach(x -> x.setDelivered(true));
        notificationRepository.deleteAll(notifs);
        return notifs;
    }

    public Flux<ServerSentEvent<List<ChatNotification>>> getNotificationsByRecipientID(String userID) {
        if (userID != null && !userID.isBlank()) {
            return Flux.interval(Duration.ofSeconds(5))
                    .publishOn(Schedulers.boundedElastic())
                    .map(sequence -> ServerSentEvent.<List<ChatNotification>>builder().id(String.valueOf(sequence))
                            .event("user-list-event").data(getNotifs(userID))
                            .build());
        }

        return Flux.interval(Duration.ofSeconds(60)).map(sequence -> ServerSentEvent.<List<ChatNotification>>builder()
                .id(String.valueOf(sequence)).event("user-list-event").data(new ArrayList<>()).build());
    }
}
