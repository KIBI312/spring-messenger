package com.seitov.messenger.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class ImageUtils {
    
    public static byte[] toByteArray(BufferedImage image) {
        try (ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();) {
            ImageIO.write(image, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error occured during converting to byte array!");
        } 
    }

    public static BufferedImage toBufferedImage(byte[] image) {
        if(image==null) {
            throw new RuntimeException("Image cannot be null");
        }
        try (InputStream inputStream = new ByteArrayInputStream(image);) {    
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return bufferedImage;   
        } catch (IOException e) {
            throw new RuntimeException("Error occured during converting to buffered image");
        }
    }

    public static byte[] resize(byte[] original, int width) {
        BufferedImage bufferedImage = toBufferedImage(original);
        BufferedImage resized = Scalr.resize(bufferedImage, Method.BALANCED, width);
        return toByteArray(resized);
    }

}
