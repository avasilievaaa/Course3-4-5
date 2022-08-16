package com.example.course345.listener;

import com.example.course345.model.NotificationTask;
import com.example.course345.repository.NotificationTaskRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final String START_COMMAND = "/start";
    private static final String HELLO_TEXT = "Hello!";

    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    @Autowired
    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            // Отправляем ответ на запрос

            Message message = update.message();
            if (message.text().equals(START_COMMAND)) {
                logger.info(START_COMMAND + "received");
                sendMessage(message.chat().id(), HELLO_TEXT);
            }

            // Выделяем из сообщения на дату и текст

            String text = update.message().text();
            Long chatId = update.message().chat().id();
            Matcher matcher = Pattern.compile("([\\d\\.\\:\\s]{16})(\\s)([\\W+]+)").matcher(text);
            if (!matcher.matches()) {
                sendMessage(chatId, "Введите запрос корректно.");
                return;
            }
            String notificationDataTimeStr = matcher.group(1);
            String notificationText = matcher.group(3);

            // Конвертируем строки в местное время

            LocalDateTime notificationDataTime = LocalDateTime
                    .parse(notificationDataTimeStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            // Создаем сообщение

            NotificationTask notificationTask = new NotificationTask(chatId, notificationText, notificationDataTime);

            // Добавляем уведомление в БД

            notificationTask = notificationTaskRepository.save(notificationTask);

            // Отправляем уведомление

            sendMessage(chatId, "Напоминаем! " + notificationTask.getMessage()
                    + " Вам уведомление в " + notificationTask.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        telegramBot.execute(sendMessage);
    }
}