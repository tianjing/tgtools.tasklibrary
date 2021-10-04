package tgtools.tasklibrary.ftp;

import java.util.List;

import static org.junit.Assert.*;

public class FTPClientTest {

    @org.junit.Test
    public static void main(String[] args) throws Exception {

        FTPClient vFTPClient = new FTPClient();
        vFTPClient.setIp("192.168.1.238");
        vFTPClient.setPort(21);
        vFTPClient.setUserName("file");
        vFTPClient.setPassword("binfo-tech@123!");
        vFTPClient.setEncoding("UTF-8");
        vFTPClient.ftpLogin();
        List<FtpFileInfo> vRes = vFTPClient.lsDetails("/");
        System.out.println(vRes);
    }
}