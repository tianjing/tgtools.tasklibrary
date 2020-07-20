package tgtools.tasklibrary.ftp;

import com.enterprisedt.net.ftp.*;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.util.LogHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian_ on 2016-07-18.
 */
public class FTPClient implements IFTPClient {
    public static final String FTP_MODE_ACTIVE = "ACTIVE";
    public static final String FTP_MODE_PASSIVE = "PASV";
    private com.enterprisedt.net.ftp.FTPClient m_Client;
    private String m_FtpModel;
    private String encoding = "GBK";
    private String ip = "";
    private int port = 21;
    private String userName = "";
    private String password = "";

    /**
     * 将文件在本地生成一份
     *
     * @param filepath
     * @param byteArr
     */
    public static boolean createLocalFile(String filepath, byte[] byteArr) {
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

    public static String[] list(String p_dirName, String[] p_extName) {
        ArrayList<String> fileNames = new ArrayList<String>();
        File dir = new File(p_dirName);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isFile())
                    continue;
                try {
                    String[] s = file.getCanonicalPath().split("\\\\");
                    if (s.length > 0) {
                        String fileExtName = s[(s.length - 1)];
                        for (String extName : p_extName) {
                            if (fileExtName.equalsIgnoreCase(extName)) {
                                fileNames.add(file.getCanonicalPath());
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }

    // 登录远程FTP服务器
    public static com.enterprisedt.net.ftp.FTPClient login(String ftp_ip, int ftp_port, String ftp_model,
                                                           String ftp_username, String ftp_password, String ftp_encoding) throws APPErrorException {
        com.enterprisedt.net.ftp.FTPClient client = new com.enterprisedt.net.ftp.FTPClient();
        try {
            client.setRemoteHost(ftp_ip); // 指定服务器地址
            client.setRemotePort(ftp_port); // 端口号
            client.setControlEncoding(ftp_encoding); // 读取文件编码格式
            FTPMessageCollector listener = new FTPMessageCollector(); // 服务器端监听
            client.setMessageListener(listener);
            client.connect();
            client.login(ftp_username, ftp_password);
            //client.setConnectMode(FTPConnectMode.ACTIVE);
            LogHelper.info("当前模式：" + ftp_model);
            client.setConnectMode("PASV".equals(ftp_model) ? FTPConnectMode.PASV : FTPConnectMode.ACTIVE);
            client.setType(FTPTransferType.BINARY);
        } catch (Exception e1) {
            throw new APPErrorException("FTP登陆错误", e1);
        }
        return client;
    }

    // 关闭连接
    public static void closeFtp(com.enterprisedt.net.ftp.FTPClient client) {
        try {
            if (client != null) {
                client.quit();
                client = null;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

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

    public com.enterprisedt.net.ftp.FTPClient getClient() {
        return m_Client;
    }

    public void setClient(com.enterprisedt.net.ftp.FTPClient pClient) {
        m_Client = pClient;
    }

    @Override
    public String[] listFiles(String p_dirName, String[] p_extName) {
        ArrayList<String> fileNames = new ArrayList<String>();

        FTPFile[] files = null;
        String newdir = p_dirName.lastIndexOf("/") != (p_dirName.length() - 1) ? p_dirName + "/" : p_dirName;
        try {
            files = m_Client.dirDetails(p_dirName);

            for (FTPFile file : files) {
                if (!file.isFile())
                    continue;
                try {
                    String s = file.getName();
                    if (null == p_extName || p_extName.length < 1) {
                        fileNames.add(file.getName());
                        continue;
                    }
                    if (s.length() > 0) {
                        int extindex = s.lastIndexOf('.');
                        if (extindex < 0) {
                            continue;
                        }
                        String fileExtName = s.substring(extindex + 1);
                        for (String extName : p_extName) {
                            if (fileExtName.equalsIgnoreCase(extName)) {
                                fileNames.add(newdir + file.getName());
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LogHelper.error("获取文件列表出错", e);
        }
        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }

    @Override
    public void ftpLogin(String ftp_ip, int ftp_port, String ftp_model, String ftp_username, String ftp_password) throws APPErrorException {
        m_Client = login(ftp_ip, ftp_port, ftp_model, ftp_username, ftp_password, encoding);
    }


    @Override
    public void ftpLogin() throws APPErrorException {
        ftpLogin(getIp(), getPort(), getFtpModel(), getUserName(), getPassword());
    }

    @Override
    public void closeFtp() {
        closeFtp(m_Client);
    }

    @Override
    public boolean createFileToLocal(String filepath, byte[] byteArr) {
        return createLocalFile(filepath, byteArr);
    }

    @Override
    public String[] dirDetails(String m_path) {
        return listFiles(m_path, new String[0]);
    }

    @Override
    public List<FtpFileInfo> lsDetails(String m_path) throws APPErrorException {
        ArrayList<FtpFileInfo> vResult = new ArrayList<FtpFileInfo>();
        try {
            FTPFile[] files = m_Client.dirDetails(m_path);
            for (FTPFile vFile : files) {
                if (".".equals(vFile.getName()) || "..".equals(vFile.getName())) {
                    continue;
                }

                FtpFileInfo vFileInfo = new FtpFileInfo();
                vFileInfo.setName(vFile.getName());
                vFileInfo.setPath(m_path);
                vFileInfo.setGroup(vFile.getGroup());
                vFileInfo.setOwner(vFile.getOwner());
                vFileInfo.setPermissions(vFile.getPermissions());
                vFileInfo.setSize(vFile.size());
                vFileInfo.setIsFile(vFile.isFile());
                vFileInfo.setLastModified(vFile.lastModified());
                vResult.add(vFileInfo);
            }
        } catch (Exception e) {
            LogHelper.error("获取文件列表出错", e);
        }
        return vResult;
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
            return m_Client.get(file);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }

    @Override
    public void get(String remoteFile, String localFile) throws APPErrorException {
        try {
            m_Client.get(localFile, remoteFile);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }

    @Override
    public void get(String remoteFile, OutputStream outputStream) throws APPErrorException
    {
        try {
            m_Client.get(outputStream, remoteFile);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }

    @Override
    public void upload(String sourcefile, String targefile) throws APPErrorException {
        try {
            m_Client.put(sourcefile, targefile);
        } catch (Exception e) {
            throw new APPErrorException("FTP上传失败");
        }
    }

    @Override
    public void upload(InputStream sourcefile, String targefile) throws APPErrorException {
        try {
            m_Client.put(sourcefile, targefile);
        } catch (Exception e) {
            throw new APPErrorException("FTP上传失败");
        }
    }

    @Override
    public void delete(String file) {
        try {
            m_Client.delete(file);
        } catch (FTPException e) {
            LogHelper.error(file + ";删除文件出错", e);
        } catch (IOException e) {
            LogHelper.error(file + ";删除文件出错", e);
        }
    }

    public String getFtpModel() {
        return m_FtpModel;
    }

    @Override
    public void setFtpModel(String pFtpModel) {
        m_FtpModel = pFtpModel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String pIp) {
        ip = pIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int pPort) {
        port = pPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        password = pPassword;
    }

    @Override
    public void Dispose() {
        closeFtp();
    }


}
