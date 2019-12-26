package tgtools.tasklibrary.ftp;

import com.jcraft.jsch.*;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by tian_ on 2016-07-18.
 */
public class SFTPClient implements IFTPClient {

    private ChannelSftp m_sftp;
    private String m_ftp_ip;
    private String m_ftp_port;
    private String m_ftp_username;
    private String m_ftp_password;
    private String m_ftp_model = "PORT";
    private String encoding ="GBK";

    private static byte[] toByte(InputStream p_Input) throws APPErrorException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {

            byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
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

    public static void main(String[] args) {

        SFTPClient client = new SFTPClient();
        //client.ftpLogin("172.17.3.32",22,"fangtian","fangtian!123");
        //client.ftpLogin("114.212.184.2",22,"root","dell~!@#123");
        //client.ftpLogin("192.168.88.32", 22, "tianjing", "tianjing");

        String path = "/home/tianjing/22/";

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
    public void upload(String sourcefile, String targefile) {
        try {
            m_sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            new APPErrorException("上传文件出错", e);
        }
    }

    @Override
    public void upload(InputStream sourcefile, String targefile) throws APPErrorException {
        try {
            m_sftp.put(sourcefile, targefile);
        } catch (SftpException e) {
            new APPErrorException("上传文件出错", e);
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
