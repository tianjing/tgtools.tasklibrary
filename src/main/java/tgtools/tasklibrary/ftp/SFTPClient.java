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
 * @author tianjing
 */
public class SFTPClient implements IFTPClient {

    private ChannelSftp sftp;
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

    public ChannelSftp getClient() {
        return sftp;
    }

    @Override
    public String[] listFiles(String p_dirName, String[] p_extName) {
        List<String> files = new ArrayList<String>();
        try {
            Vector<ChannelSftp.LsEntry> list = sftp.ls(p_dirName);
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
        return files.toArray(new String[files.size()]);
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
            sftp = channel;
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
            sftp.getSession().disconnect();

            sftp.exit();
            sftp.disconnect();

            sftp = null;
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
            input = sftp.get(file);
            return toByte(input);
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
            sftp.get(remoteFile, localFile);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }
    @Override
    public void get(String remoteFile,OutputStream outputStream) throws APPErrorException {
        try {
            sftp.get(remoteFile, outputStream);
        } catch (Exception e) {
            throw new APPErrorException("下载文件出错", e);
        }
    }


    @Override
    public void upload(String sourcefile, String targefile) throws APPErrorException {
        try {
            sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            throw new APPErrorException("上传文件出错", e);
        }
    }

    @Override
    public void upload(InputStream sourcefile, String targefile) throws APPErrorException {
        try {
            sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            throw new APPErrorException("上传文件出错", e);
        }
    }

    @Override
    public void delete(String file) throws APPErrorException {
        try {
            sftp.rm(file);
        } catch (SftpException e) {
            throw new APPErrorException("删除SFTP文件出错：" + file, e);
        }
    }

    @Override
    public void setFtpModel(String pFtpModel) {
        ftpModel = pFtpModel;
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
    public void close() {
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
