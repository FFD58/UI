package ru.fafurin;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bot extends TelegramLongPollingBot {

    final String FILENAME = ".token";

    @Override
    public String getBotUsername() {
        return "VooDoo58Bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        System.out.println(message.getText());
    }

    public String getBotToken() {
        return getTokenFromFile();
    }

    private String getTokenFromFile() {
        Path path = Paths.get(FILENAME);
        String token = null;
        try {
            token = Files.readString(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return token;
    }
}
