package tgtools.tasklibrary.ftp;

import java.io.FileInputStream;
import java.util.List;

public class SFTPClient2Test {

    @org.junit.Test
    public void main() throws Exception {
        IFTPClient client = new SFTPClient2();
        client.setEncoding("UTF-8");
        //client.ftpLogin("172.17.3.32",22,"fangtian","fangtian!123");
        //client.ftpLogin("114.212.184.2",22,"root","dell~!@#123");
        client.ftpLogin("192.168.1.245", 22, null, "tianjing", "tianjing");

        String path = "/home/tianjing/wcpt-cloud";
        String[] vFiles = client.listFiles(path, new String[]{"sh"});

        List<FtpFileInfo> vDirs = client.lsDetails(path);
       // byte[] data = client.get(path+"/server_root1.sh");


        //client.upload(new FileInputStream("C:\\Users\\tian_\\Desktop\\333333.sql"),"/home/tianjing/wcpt-cloud/333333.sql");


//        client.delete("/home/tianjing/wcpt-cloud/fda.xlsx");

        System.out.println(vDirs);


//        String[] files = client.listFiles(path, new String[]{"DT"});
//        if (null != files) {
//            for (int i = 0; i < files.length; i++) {
//                try {
//                    String remotefile = path + File.separator + files[i];
//                    byte[] data = client.get(remotefile);
//                    String localfile = "D:\\tianjing\\Desktop\\222\\" + files[i];
//                    client.createFileToLocal(localfile, data);
//                    client.delete(remotefile);
//                } catch (APPErrorException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}