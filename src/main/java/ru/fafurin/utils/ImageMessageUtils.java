package ru.fafurin.utils;

import org.telegram.telegrambots.meta.api.objects.File;
import ru.fafurin.functions.ImageOperation;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Random;

public class ImageMessageUtils {

    public static String saveImages(File image, String uri, String botToken, String folder) {
        Random random = new Random();
        makeDir(folder);
        final String localFileName = folder + "/" + LocalDate.now() + random.nextLong() + ".jpg";
        String fileUrl = uri + botToken + "/" + image.getFilePath();
        downloadImage(fileUrl, localFileName);
        return localFileName;
    }

    public static void processingImage(String path, ImageOperation filter) {
        BufferedImage image = ImageUtils.getImage(path);
        RgbMaster master = new RgbMaster(image);
        master.changeImage(filter);
        ImageUtils.saveImage(master.getImage(), path);
    }

    private static void downloadImage(String url, String filename) {
        try (InputStream in = new BufferedInputStream(new URI(url).toURL().openStream());
             OutputStream out = new BufferedOutputStream(new FileOutputStream(filename))) {
            for (int i; (i = in.read()) != -1; ) {
                out.write(i);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void makeDir(String title) {
        String path = System.getProperty("user.dir") + "\\" + title;
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
