package ru.fafurin.utils;

import ru.fafurin.functions.ImageOperation;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class RgbMaster {
    private BufferedImage image;
    private int width;
    private int height;
    private boolean hasAlphaChannel;
    private int[] pixels;

    public RgbMaster(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasAlphaChannel = image.getAlphaRaster() != null;
        this.pixels = image.getRGB(0, 0, width, height, null, 0, width);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void changeImage(ImageOperation operation) {
        for (int x = 0; x < pixels.length; x++) {
            HashMap<String, Integer> pixel = ImageUtils.rgbPixelToArr(pixels[x]);
            HashMap<String, Integer> newPixel = operation.execute(pixel);
            pixels[x] = ImageUtils.arrToRgbPixel(newPixel);
        }
        image.setRGB(0, 0, width, height, pixels, 0, width);
    }

}
