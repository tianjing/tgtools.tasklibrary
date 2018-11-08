package tgtools.tasklibrary.tasks;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.ConfigInfo;
import tgtools.tasklibrary.config.Constants;
import tgtools.tasklibrary.ftp.FTPFactory;
import tgtools.tasklibrary.ftp.IFTPClient;
import tgtools.tasklibrary.listeners.IFtpDownloadListeners;
import tgtools.tasklibrary.listeners.entity.FtpDownloadEvent;
import tgtools.tasklibrary.util.FileUtil;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP 下载任务 支持 ftp 和 sftp
 */
public class FtpDownloadTask extends Task {
    public  static final String FTP_MODEL_PORT="PORT";
    public  static final String FTP_MODEL_PASV="PASV";
    public  static final String FTP_TYPE_FTP="ftp";
    public  static final String FTP_TYPE_SFTP="sftp";


    protected ConfigInfo m_Config;
    protected String m_FtpIP;
    protected int m_Port;
    protected String m_Username;
    protected String m_Password;
    protected String m_FtpType;
    protected String m_FtpPath;
    protected String m_LocalPath;
    protected String m_FtpModel = "PORT";
    protected IFtpDownloadListeners m_DownloadListeners;


    public FtpDownloadTask() {
    }

    public FtpDownloadTask(String p_FtpIP, int p_Port, String p_Username, String p_Password, String p_FtpPath, String p_LocalPath) {
        m_FtpIP = p_FtpIP;
        m_Port = p_Port;
        m_FtpPath = p_FtpPath;
        m_LocalPath = p_LocalPath;
        m_Username = p_Username;
        m_Password = p_Password;
        m_FtpType = null;
    }

    public FtpDownloadTask(String pFtpType, String p_FtpIP, int p_Port, String p_Username, String p_Password, String p_FtpPath, String p_LocalPath, String pFtpModel) {
        m_FtpIP = p_FtpIP;
        m_Port = p_Port;
        m_FtpPath = p_FtpPath;
        m_LocalPath = p_LocalPath;
        m_Username = p_Username;
        m_Password = p_Password;
        m_FtpType = pFtpType;
        m_FtpModel = pFtpModel;
    }

    public IFtpDownloadListeners getDownloadListeners() {
        return m_DownloadListeners;
    }

    public void setDownloadListeners(IFtpDownloadListeners p_DownloadListeners) {
        m_DownloadListeners = p_DownloadListeners;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext p_Param) {
        if (null != p_Param && p_Param.containsKey("config")) {
            Object obj = p_Param.get("config");
            if (null != obj && obj instanceof ConfigInfo) {
                m_Config = (ConfigInfo) obj;
                m_FtpIP = m_Config.getFtpIp();
                m_Port = m_Config.getFtpPort();
                m_FtpType = m_Config.getFtpType();
                m_Username = m_Config.getFtpUsername();
                m_Password = m_Config.getFtpPassword();
                m_FtpPath = StringUtil.isNullOrEmpty(m_Config.getFtpPath()) ? "" : m_Config.getFtpPath();
                m_LocalPath = Constants.ftp_backpath;
                m_FtpModel = m_Config.getFtpModel();
            }
        }
        if (p_Param.containsKey(tgtools.tasklibrary.core.Constants.TaskConstans_Ftp_Listeners)) {
            if (null != p_Param.get(tgtools.tasklibrary.core.Constants.TaskConstans_Ftp_Listeners) && p_Param.get(tgtools.tasklibrary.core.Constants.TaskConstans_Ftp_Listeners) instanceof IFtpDownloadListeners) {
                setDownloadListeners((IFtpDownloadListeners) p_Param.get(tgtools.tasklibrary.core.Constants.TaskConstans_Ftp_Listeners));
            }
        }


        if (StringUtil.isNullOrEmpty(m_FtpIP) || StringUtil.isNullOrEmpty(m_LocalPath)
                || StringUtil.isNullOrEmpty(m_Username) || StringUtil.isNullOrEmpty(m_Password)) {
            return;
        }
        // 记录FTP中存在的文件，用来下载及解析
        List<String> readFiles = new ArrayList<String>();

        try {

            IFTPClient ftpClient = FTPFactory.createClientByPort(m_FtpType, m_Port);
            ftpClient.ftpLogin(m_FtpIP,
                    m_Port, m_FtpModel, m_Username, m_Password);
            if (ftpClient != null) {

                downloadFile(ftpClient);
                // 下载完成，关闭FTP连接
                ftpClient.Dispose();
            }
        } catch (Exception e) {
            tgtools.util.LogHelper.error("", "下载文件出错", "FileToDBService.FtpDownloadTask", e);
        }

    }

    protected void downloadFile(IFTPClient P_Client) throws APPErrorException {
        String[] ftpFiles = P_Client.dirDetails(m_FtpPath);
        for (String ftpFile : ftpFiles) {
            if (null != m_DownloadListeners) {
                FtpDownloadEvent e = new FtpDownloadEvent();
                e.setFileName(ftpFile);
                e.setFileExt(FileUtil.getFileExt(ftpFile));
                e.setCancel(false);
                m_DownloadListeners.beforeDownload(e);
                if (e.getCancel()) {
                    continue;
                }
            }
            if (!StringUtil.isNullOrEmpty(m_FtpPath) && !"\\".equals(m_FtpPath.substring(m_FtpPath.length() - 1)) && !"/".equals(m_FtpPath.substring(m_FtpPath.length() - 1))) {
                m_FtpPath = m_FtpPath + File.separator;
            }
            // 从FTP服务器中下载文件到指定文件夹
            P_Client.get(m_FtpPath + ftpFile, m_LocalPath + ftpFile);

            LogHelper.info("下载的文件:" + ftpFile);
            //readFiles.add(ftpFile);
            if(null!=m_DownloadListeners)
            {
                FtpDownloadEvent e=new FtpDownloadEvent();
                String ext= tgtools.tasklibrary.util.FileUtil.getFileExt(ftpFile);
                e.setFileName(ftpFile);
                e.setFileExt(ext);
                e.setCancel(false);
                m_DownloadListeners.deleteFile(e);
                if(!e.getCancel())
                {
                    // 下载完成后，删除FTP服务器上的文件
                    P_Client.delete(m_FtpPath + ftpFile);
                }
            }


        }

    }

}
