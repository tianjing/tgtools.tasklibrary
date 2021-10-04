package tgtools.tasklibrary.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import tgtools.exceptions.APPErrorException;

import java.io.File;
import java.util.Vector;

public class SFTPClientTest {

    @org.junit.Test
    public void main() throws SftpException {
        SFTPClient client = new SFTPClient();
        //client.ftpLogin("172.17.3.32",22,"fangtian","fangtian!123");
        //client.ftpLogin("114.212.184.2",22,"root","dell~!@#123");
        client.ftpLogin("192.168.1.135", 22, null, "tianjing", "tianjing");

        String path = "/home/tianjing/";

        Vector<ChannelSftp.LsEntry> list = client.getClient().ls(path);
        FtpFileInfo vFileInfo = new FtpFileInfo();
        vFileInfo.setName(list.get(0).getFilename());
        vFileInfo.setPath(path);
        vFileInfo.setGroup(String.valueOf(list.get(0).getAttrs().getGId()));
        vFileInfo.setOwner(String.valueOf(list.get(0).getAttrs().getUId()));
        vFileInfo.setPermissions(String.valueOf(list.get(0).getAttrs().getPermissionsString()));
        vFileInfo.setSize(list.get(0).getAttrs().getSize());
        vFileInfo.setIsFile(list.get(0).getAttrs().getPermissionsString().startsWith("-"));

        String[] files = client.listFiles(path, new String[]{"DT"});
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                try {
                    String remotefile = path + File.separator + files[i];
                    byte[] data = client.get(remotefile);
                    String localfile = "D:\\tianjing\\Desktop\\222\\" + files[i];
                    client.createFileToLocal(localfile, data);
                    client.delete(remotefile);
                } catch (APPErrorException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}