package ru.fafurin;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.fafurin.commands.BotCommand;
import ru.fafurin.commands.BotCommonCommands;
import ru.fafurin.functions.ImageFilter;
import ru.fafurin.functions.ImageOperation;
import ru.fafurin.utils.ImageMessageUtils;
import ru.fafurin.utils.ImageUtils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    Map<String, Message> messages = new HashMap<>();
    private final String BOT_TOKEN;
    private final String TELEGRAM_BOT_URI = "https://api.telegram.org/file/bot";

    public Bot() {
        BOT_TOKEN = loadBotToken();
    }

    @Override
    public String getBotUsername() {
        return "VooDoo58Bot";
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        Optional<SendMessage> response = Optional.empty();
        try {
            if (message.hasText()) {
                response = runCommonCommand(message);
            } else if (message.hasPhoto()) {
                response = runPhotoMessage(message);
            }
            if (response.isPresent()) {
                execute(response.get());
                return;
            }
            Optional<SendPhoto> photo = runPhotoFilter(message);
            if (photo.isPresent()) {
                execute(photo.get());
            }
        } catch (TelegramApiException | InvocationTargetException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private Optional<SendMessage> runPhotoMessage(Message message) {
        if (!message.hasPhoto()) {
            return Optional.empty();
        }
        messages.put(message.getChatId().toString(), message);
        return Optional.of(sendMessage(message, "Choose filter...", getKeyboard(ImageFilter.class)));
    }

    private Optional<SendMessage> runCommonCommand(Message message) throws InvocationTargetException, IllegalAccessException {
        String text = message.getText();
        BotCommonCommands commands = new BotCommonCommands();
        Method[] classMethods = BotCommonCommands.class.getDeclaredMethods();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(BotCommand.class)) {
                BotCommand command = method.getAnnotation(BotCommand.class);
                if (command.name().equals(text)) {
                    method.setAccessible(true);
                    String response = (String) method.invoke(commands);
                    if (response != null) {
                        return Optional.of(sendMessage(message, response, getKeyboard(BotCommonCommands.class)));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private SendMessage sendMessage(Message message, String messageText, ReplyKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setText(messageText);
        return sendMessage;
    }

    private Optional<SendPhoto> runPhotoFilter(Message message) {
        ImageOperation operation = ImageUtils.getOperation(message.getText());
        String chatId = message.getChatId().toString();
        if (messages.containsKey(chatId)) {
            Message photoMessage = messages.get(chatId);
            Optional<File> image = getFileByMessage(photoMessage);
            if (image.isPresent()) {
                String path = ImageMessageUtils.saveImages(image.get(), TELEGRAM_BOT_URI, BOT_TOKEN, "images");
                ImageMessageUtils.processingImage(path, operation);
                return Optional.of(preparePhotoMessage(photoMessage, path));
            }
        }
        return Optional.empty();
    }

    private SendPhoto preparePhotoMessage(Message message, String localPath) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(message.getChatId().toString());
        InputFile newFile = new InputFile();
        newFile.setMedia(new java.io.File(localPath));
        photo.setPhoto(newFile);
        return photo;
    }

    private Optional<File> getFileByMessage(Message message) {
        List<PhotoSize> images = message.getPhoto();
        Optional<PhotoSize> image = images.stream().max(Comparator.comparingLong(PhotoSize::getFileSize));
        if (image.isPresent()) {
            String fileId = image.get().getFileId();
            try {
                File file = sendApiMethod(new GetFile(fileId));
                return Optional.of(file);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    private ReplyKeyboardMarkup getKeyboard(Class className) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> allKeyboardRows = new ArrayList<>(getKeyboardRows(className));
        replyKeyboardMarkup.setKeyboard(allKeyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> getKeyboardRows(Class className) {
        Method[] classMethods = className.getDeclaredMethods();
        List<BotCommand> commands = new ArrayList<>();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(BotCommand.class)) {
                commands.add(method.getAnnotation(BotCommand.class));
            }
        }
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int columnCount = 3;
        int rowsCount = commands.size() / columnCount + commands.size() % columnCount == 0 ? 0 : 1;
        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                int index = rowIndex * columnCount + colIndex;
                if (index >= commands.size()) continue;
                BotCommand command = commands.get(index);
                KeyboardButton button = new KeyboardButton(command.name());
                row.add(button);
            }
            keyboardRows.add(row);
        }
        return keyboardRows;
    }

    private String loadBotToken() {
        try (InputStream input = Bot.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }
            prop.load(input);
            return prop.getProperty("telegram.bot.token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}