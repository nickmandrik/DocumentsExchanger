package fortegroup.internship.mandrik.exchanger.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class Encoder {
    public static byte[] encodeBytesToBase64(byte[] bytes) {
        return Base64.encodeBase64(bytes);
    }

    public static byte[] encodeFileToBase64(File file) throws IOException {
        return Base64.encodeBase64(FileUtils.readFileToByteArray(file));
    }
}
