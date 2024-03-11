package ru.fafurin.utils;

import ru.fafurin.commands.BotCommand;
import ru.fafurin.functions.ImageFilter;
import ru.fafurin.functions.ImageOperation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ImageUtils {

    public static BufferedImage getImage(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    public static void saveImage(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "jpg", new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Integer> rgbPixelToArr(int pixel) {
        Color color = new Color(pixel, true);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        HashMap<String, Integer> colorComponents = new HashMap<>();
        colorComponents.put("red", red);
        colorComponents.put("green", green);
        colorComponents.put("blue", blue);
        return colorComponents;
    }

    public static int arrToRgbPixel(HashMap<String, Integer> map) {
        Color color;
        color = new Color(map.get("red"), map.get("green"), map.get("blue"));
        return color.getRGB();
    }

    public static ImageOperation getOperation(String operationName) {
        ImageFilter filter = new ImageFilter();
        Method[] classMethods = filter.getClass().getDeclaredMethods();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(BotCommand.class)) {
                BotCommand command = method.getAnnotation(BotCommand.class);
                if (command.name().equals(operationName)) {
                    return (f) -> {
                        try {
                            return (HashMap<String, Integer>) method.invoke(filter, f);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    };
                }
            }
        }
        return null;
    }
}
