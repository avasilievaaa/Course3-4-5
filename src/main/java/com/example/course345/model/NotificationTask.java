package com.example.course345.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

// Класс для создания уведомления как Объекта
@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private LocalDateTime dateTime;
    private String message;

    public NotificationTask(Long chatId, String message, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.dateTime = dateTime;
        this.message = message;
    }

    public NotificationTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id.equals(that.id) && chatId.equals(that.chatId) && dateTime.equals(that.dateTime) && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, dateTime, message);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", dateTime=" + dateTime +
                ", message='" + message + '\'' +
                '}';
    }
}