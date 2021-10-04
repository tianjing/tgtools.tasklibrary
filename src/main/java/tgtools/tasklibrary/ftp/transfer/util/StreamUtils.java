package tgtools.tasklibrary.ftp.transfer.util;

import tgtools.exceptions.APPErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 田径
 * @date 2020-03-17 9:27
 * @desc
 **/
public class StreamUtils {

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
}
