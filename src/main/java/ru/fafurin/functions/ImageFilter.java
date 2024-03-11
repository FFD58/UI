package ru.fafurin.functions;

import ru.fafurin.commands.BotCommand;

import java.util.HashMap;
import java.util.Random;

public class ImageFilter {

    @BotCommand(name = "grayscale", description = "Convert the image to grayscale", showInKeyboard = true)
    public static HashMap<String, Integer> grayScale(HashMap<String, Integer> rgb) {
        int red = rgb.get("red");
        int green = rgb.get("green");
        int blue = rgb.get("blue");

        int average = (red + green + blue) / 3;
        rgb.put("red", average);
        rgb.put("green", average);
        rgb.put("blue", average);
        return rgb;
    }

    @BotCommand(name = "noise", description = "Add to the image some noise", showInKeyboard = true)
    public static HashMap<String, Integer> noise(HashMap<String, Integer> rgb) {
        Random random = new Random();
        float randomValue = random.nextFloat(40);
        int red = rgb.get("red");
        int green = rgb.get("green");
        int blue = rgb.get("blue");

        red += randomValue;
        green += randomValue;
        blue += randomValue;
        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;

        rgb.put("red", red);
        rgb.put("green", green);
        rgb.put("blue", blue);
        return rgb;
    }

    @BotCommand(name = "sepia", description = "Convert the image to sepia", showInKeyboard = true)
    public static HashMap<String, Integer> sepia(HashMap<String, Integer> rgb) {
        int red = rgb.get("red");
        int green = rgb.get("green");
        int blue = rgb.get("blue");

        int average = (red + green + blue) / 3;
        int depth = 20;
        int intensity = 30;

        red = average + (depth * 2);
        green = average + depth;
        blue = average - intensity;

        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;
        if (blue < 0) blue = 0;

        rgb.put("red", red);
        rgb.put("green", green);
        rgb.put("blue", blue);
        return rgb;
    }
}
