package ru.fafurin;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bot extends TelegramLongPollingBot {
    private final String FILENAME = ".token";

    private final String TELEGRAM_BOT_URI = "https://api.telegram.org/file/bot";

    @Override
    public String getBotUsername() {
        return "VooDoo58Bot";
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

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        System.out.println(message.getText());

        // receive image
    //    receiveImage(message, "new-file.jpg");

        // send image
        sendImage(message, "new-file.jpg", "new-file.jpg");

        // messages
        sendMessage(message, "Hello!");
    }

    private void sendMessage(Message message, String messageText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(messageText);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendImage(Message message, String filename, String caption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        InputFile file = new InputFile().setMedia(filename);
        sendPhoto.setPhoto(file);
        sendPhoto.setCaption(caption);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveImage(Message message, String filename) {
        PhotoSize photoSize = message.getPhoto().get(0);

        String fileId = photoSize.getFileId();

        try {
            org.telegram.telegrambots.meta.api.objects.File file = sendApiMethod(new GetFile(fileId));
            String fileUrl = TELEGRAM_BOT_URI + getTokenFromFile() + "/" + file.getFilePath();
            saveImage(fileUrl, filename);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImage(String url, String filename) throws IOException {
        URL urlModel = new URL(url);
        InputStream inputStream = urlModel.openStream();
        OutputStream outputStream = new FileOutputStream(filename);
        byte[] b = new byte[2048];
        int length;
        while ((length = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
}
