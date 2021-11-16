package tgtools.tasklibrary.ftp.transfer.util;

import tgtools.exceptions.APPErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author 田径
 * @date 2020-03-17 9:27
 * @desc
 **/
public class StreamUtils {
    public static final int DEFAULT_BUFFER_SIZE = 2048;

    public static byte[] toByte(InputStream pInput) throws APPErrorException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {

            //buff用于存放循环读取的临时数据
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = pInput.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            return swapStream.toByteArray();
        } catch (Exception e) {
            throw new APPErrorException("输入流转换出错", e);
        } finally {
            try {
                swapStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        if (null == input || null == output) {
            return;
        }

        byte[] buf = new byte[bufferSize];
        int bytesRead = input.read(buf);
        while (bytesRead != -1) {
            output.write(buf, 0, bytesRead);
            bytesRead = input.read(buf);
        }
        output.flush();
    }

    public static void copyThenClose(InputStream input, OutputStream output)
            throws IOException {
        copy(input, output);
        input.close();
        output.close();
    }

}
