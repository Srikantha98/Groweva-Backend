package org.evabirds.evabirds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.evabirds.evabirds.entity.Notification;

import java.time.Duration;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String type;

    private boolean read;

    private String link;
    private LocalDateTime createdAt;
    private String time;

    public NotificationResponse() {
    }

    public static NotificationResponse fromEntity(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setTitle(n.getTitle());
        res.setMessage(n.getMessage());
        res.setType(n.getType() != null ? n.getType().toLowerCase() : "alert");
        res.setRead(n.isRead());
        res.setLink(n.getLink());
        res.setCreatedAt(n.getCreatedAt());
        res.setTime(formatTimeAgo(n.getCreatedAt()));
        return res;
    }

    public static String formatTimeAgo(LocalDateTime dt) {
        if (dt == null) return "just now";
        Duration d = Duration.between(dt, LocalDateTime.now());
        long mins = d.toMinutes();
        if (mins < 1) return "just now";
        if (mins < 60) return mins + "m ago";
        long hours = d.toHours();
        if (hours < 24) return hours + "h ago";
        long days = d.toDays();
        if (days < 7) return days + "d ago";
        return (days / 7) + "w ago";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
