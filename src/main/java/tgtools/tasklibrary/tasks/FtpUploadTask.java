package tgtools.tasklibrary.tasks;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.ConfigInfo;
import tgtools.tasklibrary.core.Constants;
import tgtools.tasklibrary.ftp.FTPFactory;
import tgtools.tasklibrary.ftp.IFTPClient;
import tgtools.tasklibrary.listeners.IFtpDownloadListeners;
import tgtools.tasklibrary.listeners.entity.FtpDownloadEvent;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.FileUtil;
import tgtools.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * FTP 上传任务 支持 ftp 和 sftp
 * @author tianjing
 */
public class FtpUploadTask extends Task {
    protected IFtpDownloadListeners m_DownloadListeners;
    private String ftpType;
    private String ftpModel = "PORT";
    private ConfigInfo config;
    private String ftpIP;
    private int port;
    private String username;
    private String password;
    private String ftpPath;
    private String localPath;

    public FtpUploadTask() {
    }

    public FtpUploadTask(String pFtpIP, int pPort, String pUsername, String pPassword, String pFtpPath, String pLocalPath) {
        ftpIP = pFtpIP;
        port = pPort;
        ftpPath = pFtpPath;
        localPath = pLocalPath;
        username = pUsername;
        password = pPassword;
        ftpType = null;
    }

    public FtpUploadTask(String pFtpType, String pFtpIP, int pPort, String pUsername, String pPassword,
                         String pFtpPath, String pLocalPath, String pFtpModel) {
        ftpIP = pFtpIP;
        port = pPort;
        ftpPath = pFtpPath;
        localPath = pLocalPath;
        username = pUsername;
        password = pPassword;
        ftpType = pFtpType;
        ftpModel = pFtpModel;
    }

    public IFtpDownloadListeners getDownloadListeners() {
        return m_DownloadListeners;
    }

    public void setDownloadListeners(IFtpDownloadListeners pDownloadListeners) {
        m_DownloadListeners = pDownloadListeners;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        if (null != taskContext && taskContext.containsKey("config")) {
            Object obj = taskContext.get("config");
            if (null != obj && obj instanceof ConfigInfo) {
                config = (ConfigInfo) obj;
                ftpIP = config.getFtpIp();
                port = config.getFtpPort();
                username = config.getFtpUsername();
                password = config.getFtpPassword();
                ftpPath = config.getFtpPath();
                localPath = Constants.FTP_BACK_PATH;
                ftpType = config.getFtpType();
            }
        }
        if (StringUtil.isNullOrEmpty(ftpIP) || StringUtil.isNullOrEmpty(ftpPath) || StringUtil.isNullOrEmpty(localPath)
                || StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)) {
            return;
        }
        String[] files = FileUtil.listFiles(localPath, null);
        if (null == files || files.length < 1) {
            return;
        }
        IFTPClient ftpClient = FTPFactory.createClientByPort(ftpType, port);
        if (null == ftpClient) {
            LogHelper.info("无法创建 FTP Client FtpType:" + ftpType + ";;Port:" + port);
            return;
        }
        try {
            ftpClient.ftpLogin(ftpIP, port, ftpModel, username, password);

            for (int i = 0; i < files.length; i++) {
                File file = new File(files[i]);
                if (null != m_DownloadListeners) {
                    FtpDownloadEvent e = new FtpDownloadEvent();
                    e.setFileName(file.getName());
                    e.setFileExt(tgtools.tasklibrary.util.FileUtil.getFileExt(files[i]));
                    e.setCancel(false);
                    m_DownloadListeners.beforeDownload(e);
                    if (e.getCancel()) {
                        continue;
                    }
                }
                try {
                    if (!"/".equals(ftpPath.substring(ftpPath.length() - 1)) && !"\\".equals(ftpPath.substring(ftpPath.length() - 1))) {
                        ftpPath = ftpPath + File.separator;
                    }
                    LogHelper.info("准备上传文件：" + files[i]);
                    ftpClient.upload(files[i], ftpPath + file.getName());
                    LogHelper.info("已上传文件：" + files[i]);


                    if (null != m_DownloadListeners) {
                        FtpDownloadEvent e = new FtpDownloadEvent();
                        String ext = tgtools.tasklibrary.util.FileUtil.getFileExt(file.getName());
                        e.setFileName(file.getName());
                        e.setFileExt(ext);
                        e.setCancel(false);
                        m_DownloadListeners.deleteFile(e);
                        if (!e.getCancel()) {
                            deleteFile(files[i]);
                        }
                    }

                } catch (APPErrorException e) {
                    LogHelper.error("上传错误文件：" + files[i] + "ftp：" + ftpPath, e);
                }
            }
        } catch (APPErrorException e) {
            LogHelper.error("upload error", e);
        } finally {
            if (null != ftpClient) {
                try {
                    ftpClient.close();
                } catch (IOException e) {

                }
            }
            ftpClient = null;
        }
    }

    private void deleteFile(String pFile) {
        for (int i = 0; i < 3; i++) {
            if (new File(pFile).delete()) {
                return;
            }
        }
        LogHelper.error("删除文件失败", new APPErrorException(pFile));
    }
}
