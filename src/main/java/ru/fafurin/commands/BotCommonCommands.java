package ru.fafurin.commands;

import ru.fafurin.functions.ImageFilter;

import java.lang.reflect.Method;

public class BotCommonCommands {
    @BotCommand(name = "/help", description = "help", showInKeyboard = true)
    String help() {
        return getCommandsString();
    }

    private String getCommandsString() {
        Method[] classMethods = ImageFilter.class.getDeclaredMethods();
        StringBuilder builder = new StringBuilder();
        for (Method classMethod : classMethods) {
            BotCommand command = classMethod.getAnnotation(BotCommand.class);
            builder.append(command.name()).append(" - ").append(command.description()).append("\n");
        }
        return builder.toString();
    }
}
