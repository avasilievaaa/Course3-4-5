package com.example.course345.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.course345.listener.TelegramBotUpdatesListener;
import com.example.course345.model.NotificationTask;
import com.example.course345.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskScheduler {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    @Autowired
    public NotificationTaskScheduler(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    // Реализуется ежеминутная проверка подходящих уведомлений на конкретное время

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {

        LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> notificationTasks = repository.findAllByDateTime(nowDateTime);

        notificationTasks.forEach(this::sendNotification);
    }

    // Реализуется отправка уведомления в чат с последующем удалении его из БД

    private void sendNotification(NotificationTask task) {

        SendResponse response = telegramBot.execute(new SendMessage(task.getChatId(), task.getMessage()));
        if (response.isOk()) {
            repository.delete(task);
        }
    }
}