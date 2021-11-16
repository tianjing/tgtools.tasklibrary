package tgtools.tasklibrary.ftp;


import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.transfer.util.StreamUtils;
import tgtools.util.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tian_ on 2016-07-18.
 *
 * @author tianjing
 */
public class SFTPClient2 implements IFTPClient {

    private SftpClient sftp;
    private String ftpIp;
    private int ftpPort;
    private String ftpUsername;
    private String ftpPassword;
    private String ftpModel = "PORT";
    private String encoding = "GBK";

    private static byte[] toByte(InputStream pInput) throws APPErrorException {
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


    public String getIp() {
        return ftpIp;
    }

    public void setIp(String pIp) {
        ftpIp = pIp;
    }

    public int getPort() {
        return ftpPort;
    }

    public void setPort(int pPort) {
        ftpPort = pPort;
    }

    public String getUserName() {
        return ftpUsername;
    }

    public void setUserName(String pUsername) {
        ftpUsername = pUsername;
    }

    public String getPassword() {
        return ftpPassword;
    }

    public void setPassword(String pPassword) {
        ftpPassword = pPassword;
    }

    public String getModel() {
        return ftpModel;
    }

    public void setModel(String pModel) {
        ftpModel = pModel;
    }

    public SftpClient getClient() {
        return sftp;
    }

    @Override
    public String[] listFiles(String p_dirName, String[] p_extName) {
        List<String> vFiles = new ArrayList<String>();
        try {
            Iterable<SftpClient.DirEntry> vDirList = sftp.readDir(p_dirName);
            vDirList.forEach((item) -> {
                if (".".equals(item.getFilename()) || "..".equals(item.getFilename())) {
                    return;
                }
                for (String extName : p_extName) {
                    if (!isDirectory(item.getLongFilename()) && item.getFilename().toLowerCase().endsWith(extName.toLowerCase())) {
                        vFiles.add(item.getFilename());
                        break;
                    }
                }
            });


        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vFiles.toArray(new String[vFiles.size()]);

    }

    private boolean isDirectory(String longname) {
        return !StringUtil.isNullOrEmpty(longname) && longname.startsWith("d");
    }

    @Override
    public void ftpLogin(String ftp_ip, int ftp_port, String ftp_model, String ftp_username, String ftp_password) throws APPErrorException {
        SshClient vClient = SshClient.setUpDefaultClient();
        vClient.start();
        try {
            ClientSession vClientSession = vClient.connect(ftp_username, ftp_ip, ftp_port).verify().getSession();
            vClientSession.addPasswordIdentity(ftp_password);

            AuthFuture vAuthFuture = vClientSession.auth().verify();
            if (!vAuthFuture.isSuccess()) {
                throw new APPErrorException("ssh client2 认证失败！");
            }
            sftp = SftpClientFactory.instance().createSftpClient(vClientSession);
            sftp.setNameDecodingCharset(Charset.forName(getEncoding()));
        } catch (Throwable e) {
            if (e instanceof APPErrorException) {
                throw (APPErrorException) e;
            }
            throw new APPErrorException("ssh client2 连接失败！原因：" + e, e);
        }
    }

    @Override
    public void ftpLogin() throws APPErrorException {
        ftpLogin(getIp(), getPort(), getModel(), getUserName(), getPassword());
    }

    @Override
    public void closeFtp() {
        try {
            if (null == sftp) {
                return;
            }
            sftp.close();
            sftp = null;
        } catch (IOException e) {

        }
    }

    @Override
    public boolean createFileToLocal(String filepath, byte[] byteArr) {
        boolean flag = true;
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            flag = false;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(byteArr, 0, byteArr.length);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                flag = false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    @Override
    public String[] dirDetails(String m_path) throws APPErrorException {
        return listFiles(m_path, new String[0]);
    }

    @Override
    public List<FtpFileInfo> lsDetails(String m_path) throws APPErrorException {
        List<FtpFileInfo> vList = new ArrayList<>();
        try {
            Iterable<SftpClient.DirEntry> vDirList = sftp.readDir(m_path);
            vDirList.forEach((item) -> {
                if (".".equals(item.getFilename()) || "..".equals(item.getFilename())) {
                    return;
                }
                FtpFileInfo vFileInfo = new FtpFileInfo();
                vFileInfo.setGroup(item.getAttributes().getGroup());
                vFileInfo.setIsFile(!item.getAttributes().isDirectory());
                vFileInfo.setLastModified(new Date(item.getAttributes().getModifyTime().toMillis()));
                vFileInfo.setName(item.getFilename());
                vFileInfo.setOwner(item.getAttributes().getOwner());
                vFileInfo.setPath(m_path);
                vFileInfo.setPermissions(String.valueOf(item.getAttributes().getPermissions()));
                vFileInfo.setSize(item.getAttributes().getSize());
                vList.add(vFileInfo);
            });

            return vList;
        } catch (Throwable e) {
            throw new APPErrorException("lsDetails 出错！原因：" + e, e);
        }
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String pEncoding) {
        encoding = pEncoding;
    }

    @Override
    public byte[] get(String file) throws APPErrorException {
        try {
            InputStream vInputStream = sftp.read(file);
            return StreamUtils.toByte(vInputStream);
        } catch (Throwable e) {
            throw new APPErrorException("获取文件内容失败！" + file + ";原因：" + e, e);
        }
    }

    @Override
    public void get(String remoteFile, String localFile) throws APPErrorException {
        try {
            InputStream vInputStream = sftp.read(remoteFile);

            File vFile = new File(localFile);
            if (!vFile.exists()) {
                vFile.mkdirs();
            }

            FileOutputStream vFileStream = new FileOutputStream(vFile);
            StreamUtils.copyThenClose(vInputStream, vFileStream);
        } catch (Throwable e) {
            throw new APPErrorException("获取文件内容失败！" + remoteFile + ";原因：" + e, e);
        }
    }

    @Override
    public void get(String remoteFile, OutputStream outputStream) throws APPErrorException {
        try {
            InputStream vInputStream = sftp.read(remoteFile);
            StreamUtils.copy(vInputStream, outputStream);
            vInputStream.close();
        } catch (Throwable e) {
            throw new APPErrorException("获取文件内容失败！" + remoteFile + ";原因：" + e, e);
        }
    }


    @Override
    public void upload(String sourcefile, String targefile) throws APPErrorException {
        try {

            File vFile = new File(sourcefile);
            if (!vFile.exists()) {
                throw new APPErrorException("本地文件不存在！" + sourcefile);
            }
            FileInputStream vFileStream = new FileInputStream(vFile);
            OutputStream vOutputStream = sftp.write(targefile);
            StreamUtils.copyThenClose(vFileStream, vOutputStream);
        } catch (Throwable e) {
            throw new APPErrorException("上传文件内容失败！" + targefile + ";原因：" + e, e);
        }
    }

    @Override
    public void upload(InputStream sourcefile, String targefile) throws APPErrorException {
        try {
            OutputStream vOutputStream = sftp.write(targefile);
            StreamUtils.copyThenClose(sourcefile, vOutputStream);
        } catch (Throwable e) {
            throw new APPErrorException("上传文件内容失败！" + targefile + ";原因：" + e, e);
        }
    }


    @Override
    public void delete(String file) throws APPErrorException {
        try {
            sftp.remove(file);
        } catch (IOException e) {
            throw new APPErrorException("删除远程文件失败！" + file + ";原因：" + e, e);
        }
    }

    @Override
    public void setFtpModel(String pFtpModel) {
        ftpModel = pFtpModel;
    }


    @Override
    public void close() {
        try {
            closeFtp();
        } catch (Exception e) {
        }
    }


}
