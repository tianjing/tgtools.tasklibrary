package tgtools.tasklibrary.ftp;

import com.jcraft.jsch.*;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by tian_ on 2016-07-18.
 */
public class SFTPClient implements IFTPClient {

    private ChannelSftp m_sftp;
    private String m_ftp_ip;
    private int m_ftp_port;
    private String m_ftp_username;
    private String m_ftp_password;
    private String m_ftp_model = "PORT";
    private String encoding = "GBK";

    private static byte[] toByte(InputStream p_Input) throws APPErrorException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {

            //buff用于存放循环读取的临时数据
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = p_Input.read(buff, 0, 100)) > 0) {
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

    public static void main(String[] args) throws Exception {

        SFTPClient client = new SFTPClient();
        //client.ftpLogin("172.17.3.32",22,"fangtian","fangtian!123");
        //client.ftpLogin("114.212.184.2",22,"root","dell~!@#123");
        client.ftpLogin("192.168.1.135", 22, null, "tianjing", "tianjing");

        String path = "/home/tianjing/";

        Vector<ChannelSftp.LsEntry> list = client.m_sftp.ls(path);
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

    public String getIp() {
        return m_ftp_ip;
    }

    public void setIp(String pIp) {
        m_ftp_ip = pIp;
    }

    public int getPort() {
        return m_ftp_port;
    }

    public void setPort(int pPort) {
        m_ftp_port = pPort;
    }

    public String getUserName() {
        return m_ftp_username;
    }

    public void setUserName(String pUsername) {
        m_ftp_username = pUsername;
    }

    public String getPassword() {
        return m_ftp_password;
    }

    public void setPassword(String pPassword) {
        m_ftp_password = pPassword;
    }

    public String getModel() {
        return m_ftp_model;
    }

    public void setModel(String pModel) {
        m_ftp_model = pModel;
    }

    public ChannelSftp getClient() {
        return m_sftp;
    }

    @Override
    public String[] listFiles(String p_dirName, String[] p_extName) {
        List<String> files = new ArrayList<String>();
        try {
            Vector<ChannelSftp.LsEntry> list = m_sftp.ls(p_dirName);
            for (int i = 0; i < list.size(); i++) {
                ChannelSftp.LsEntry file = list.get(i);
                if (null == p_extName || p_extName.length < 1) {
                    files.add(file.getFilename());
                    continue;
                }
                for (String extName : p_extName) {
                    if (!isDirectory(file.getLongname()) && file.getFilename().toLowerCase().endsWith(extName.toLowerCase())) {
                        files.add(file.getFilename());
                        break;
                    }
                }
            }


        } catch (SftpException e) {
            e.printStackTrace();
        }
        return (String[]) files.toArray(new String[files.size()]);
    }

    private boolean isDirectory(String longname) {
        return !StringUtil.isNullOrEmpty(longname) && longname.startsWith("d");
    }

    @Override
    public void ftpLogin(String ftp_ip, int ftp_port, String ftp_model, String ftp_username, String ftp_password) {
        Logger logger = new SettleLogger();
        JSch.setLogger(logger);
        try {
            JSch jsch = new JSch();
            jsch.getSession(ftp_username, ftp_ip, ftp_port);

            Session sshSession = jsch.getSession(ftp_username, ftp_ip, ftp_port);
            System.out.println("Session created.");
            sshSession.setPassword(ftp_password);

            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");

            sshSession.setConfig(sshConfig);
            sshSession.setTimeout(30000);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            ChannelSftp channel = (ChannelSftp) sshSession.openChannel("sftp");

            channel.connect();

            Field f = ChannelSftp.class.getDeclaredField("server_version");
            f.setAccessible(true);
            f.set(channel, 2);
            channel.setFilenameEncoding(getEncoding());
            m_sftp = channel;
            // System.out.println("Connected to " + ftp_ip + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ftpLogin() throws APPErrorException {
        ftpLogin(getIp(), getPort(), getModel(), getUserName(), getPassword());
    }

    @Override
    public void closeFtp() {
        try {
            m_sftp.getSession().disconnect();

            m_sftp.exit();
            m_sftp.disconnect();

            m_sftp = null;
        } catch (JSchException e) {
            LogHelper.error("", "SFTP关闭出错", "SFTPClient.closeFtp", e);
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
        List<FtpFileInfo> vResult = new ArrayList<FtpFileInfo>();
        try {
            Vector<ChannelSftp.LsEntry> vFiles = getClient().ls(m_path);
            for (ChannelSftp.LsEntry vFile : vFiles) {
                if (".".equals(vFile.getFilename()) || "..".equals(vFile.getFilename())) {
                    continue;
                }

                FtpFileInfo vFileInfo = new FtpFileInfo();
                vFileInfo.setName(vFile.getFilename());
                vFileInfo.setPath(m_path);
                vFileInfo.setGroup(String.valueOf(vFile.getAttrs().getGId()));
                vFileInfo.setOwner(String.valueOf(vFile.getAttrs().getUId()));
                vFileInfo.setPermissions(String.valueOf(vFile.getAttrs().getPermissionsString()));
                vFileInfo.setSize(vFile.getAttrs().getSize());
                vFileInfo.setIsFile(vFile.getAttrs().getPermissionsString().startsWith("-"));
                if (null != vFile.getAttrs() && vFile.getAttrs().getMTime() > 0) {
                    vFileInfo.setLastModified(new Date(vFile.getAttrs().getMTime()*1000L));
                }

                vResult.add(vFileInfo);
            }
        } catch (Exception e) {
            throw new APPErrorException("sftp ls 出错！path:" + m_path, e);
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
        InputStream input = null;
        try {
            input = m_sftp.get(file);
            return toByte(input);


            // m_sftp.cd(directory);
            // File file=new File(saveFile);
            // sftp.get(downloadFile, new FileOutputStream(file));
        } catch (SftpException e) {
            throw new APPErrorException("下载文件出错", e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void get(String remoteFile, String localFile) throws APPErrorException {
        try {
            m_sftp.get(remoteFile, localFile);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }

    @Override
    public void upload(String sourcefile, String targefile) throws APPErrorException {
        try {
            m_sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            throw new APPErrorException("上传文件出错", e);
        }
    }

    @Override
    public void upload(InputStream sourcefile, String targefile) throws APPErrorException {
        try {
            m_sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            throw new APPErrorException("上传文件出错", e);
        }
    }

    @Override
    public void delete(String file) throws APPErrorException {
        try {
            //m_sftp.cd(directory);
            m_sftp.rm(file);
        } catch (SftpException e) {
            throw new APPErrorException("删除SFTP文件出错：" + file, e);
        }
    }

    @Override
    public void setFtpModel(String pFtpModel) {
        m_ftp_model = pFtpModel;
    }

    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     * @param sftp
     */
    public void upload(String directory, String uploadFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Dispose() {
        try {
            closeFtp();
        } catch (Exception e) {
        }
    }


    public class SettleLogger implements Logger {
        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String msg) {
            System.out.println(msg);
        }
    }
}
