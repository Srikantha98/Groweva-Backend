package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.NotificationResponse;
import org.evabirds.evabirds.entity.Notification;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.NotificationRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return notificationRepository.countUnreadByUserId(user.getId());
    }

    @Transactional
    public void markAsRead(Long notifId, String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        notificationRepository.markAsRead(notifId, user.getId());
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, String type, String link) {
        if (user == null) {
            return null;
        }
        Notification notification = new Notification(user, title, message, type, link);
        return notificationRepository.save(notification);
    }
}
