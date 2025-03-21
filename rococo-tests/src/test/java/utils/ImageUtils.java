package utils;

import java.io.InputStream;
import java.util.Base64;

public class ImageUtils {

    public static String convertImgToBase64(String filePath) {
        byte[] fileContent = convertImageToByteArray(filePath);
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);
    }

    public static String convertImgToBase64(byte[] img) {
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(img);
    }

    public static byte[] convertImageToByteArray(String resourcePath) {
        try (InputStream inputStream = ImageUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + resourcePath);
            }
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error while reading file: " + resourcePath, e);
        }
    }
}
