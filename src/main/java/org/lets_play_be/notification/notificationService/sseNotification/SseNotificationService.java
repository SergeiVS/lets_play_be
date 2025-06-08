package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.NotificationFactory;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.NotificationSubject;
import org.lets_play_be.notification.dto.LobbyCreatedNotificationData;
import org.lets_play_be.notification.dto.Notification;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseService sseService;

    private final SseLiveRecipientPool recipientPool;

    private final LobbySubjectPool subjectPool;

    private final AppUserService userService;

    private final InviteService inviteService;


    public SseEmitter subscribeForSse(Authentication auth) {

        var recipientId = userService.getUserIdByEmailOrThrow(auth.getName());

        final SseEmitter emitter = sseService.createSseConnection();

        isRecipientNotSubscribed(recipientId);

        createSseObserver(emitter, recipientId);

        return emitter;
    }


    public void subscribeSseObserverForActiveLobby(long recipientId, long lobbyId) {

        if (recipientPool.isInPool(recipientId)) {

            NotificationObserver observer = recipientPool.getObserver(recipientId);

            NotificationSubject subject = subjectPool.getSubject(lobbyId);

            subject.subscribe(observer);
        }
    }

    public void notifyLobbyMembers(long lobbyId, Notification notification) {

        NotificationSubject subject = subjectPool.getSubject(lobbyId);

        subject.notifyObservers(notification);
    }

    private void isRecipientNotSubscribed(Long recipientId) {
        if (recipientPool.isInPool(recipientId)) {
            throw new RestException("Recipient with id: " + recipientId + " is already subscribed", HttpStatus.BAD_REQUEST);
        }
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {

        recipientPool.addObserver(recipientId, emitter);
    }


    public void sendMissedNotifications(Authentication authentication) {

        var recipientId = userService.getUserIdByEmailOrThrow(authentication.getName());

        try {
            List<Invite> invites = inviteService.getNotDeliveredInvitesByUserIdl(recipientId);

            NotificationObserver observer = recipientPool.getObserver(recipientId);

            for (Invite invite : invites) {

                var lobby = invite.getLobby();
                var data = new LobbyCreatedNotificationData(lobby);
                var notification = NotificationFactory.createNotification(data);
                observer.update(notification);
                inviteService.changeIsDeliveredState(true, invite);
            }
        } catch (IOException e) {

            throw new RestException("Failed to send missed notifications", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
