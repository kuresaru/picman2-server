package top.scraft.picmanserver.utils;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static final String[] HEX_CODE = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static boolean isPidInvalid(String pid) {
        return !pid.toLowerCase().matches("^[a-f0-9]{32}\\.(jpg|jpeg|png|gif)$");
    }

    public static MediaType mediaType(String pid) {
        MediaType mediaType;
        String pidLower = pid.toLowerCase();
        if (pidLower.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else if (pidLower.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.IMAGE_JPEG;
        }
        return mediaType;
    }

    @SneakyThrows(NoSuchAlgorithmException.class)
    public static byte[] md5(byte[] data) {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(data);
        return messageDigest.digest();
    }

    public static String Bytes2HStrNoSpace(byte[] bytes) {
        String string = "";
        if (bytes != null) {
            for (byte b : bytes) {
                string += HEX_CODE[(b & 0xFF) / 0x10];
                string += HEX_CODE[(b & 0xFF) & 0x0F];
            }
        }
        return string;
    }

}
