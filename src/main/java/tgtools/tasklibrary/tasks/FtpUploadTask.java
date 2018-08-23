package tgtools.tasklibrary.tasks;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.Constants;
import tgtools.tasklibrary.config.ConfigInfo;
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

/**
 * FTP 上传任务 支持 ftp 和 sftp
 */
public class FtpUploadTask extends Task {
    private String m_FtpType;
    private String m_FtpModel="PORT";

    public FtpUploadTask(){}
    public FtpUploadTask(String p_FtpIP, int p_Port, String p_Username, String p_Password, String p_FtpPath, String p_LocalPath) {
        m_FtpIP = p_FtpIP;
        m_Port = p_Port;
        m_FtpPath = p_FtpPath;
        m_LocalPath = p_LocalPath;
        m_Username = p_Username;
        m_Password = p_Password;
        m_FtpType=null;
    }
    public FtpUploadTask(String pFtpType,String p_FtpIP, int p_Port, String p_Username, String p_Password,
                          String p_FtpPath, String p_LocalPath,String p_FtpModel) {
        m_FtpIP = p_FtpIP;
        m_Port = p_Port;
        m_FtpPath = p_FtpPath;
        m_LocalPath = p_LocalPath;
        m_Username = p_Username;
        m_Password = p_Password;
        m_FtpType=pFtpType;
        m_FtpModel=p_FtpModel;
    }

    protected IFtpDownloadListeners m_DownloadListeners;

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

    private ConfigInfo m_Config;
    private String m_FtpIP;
    private int m_Port;
    private String m_Username;
    private String m_Password;

    private String m_FtpPath;
    private String m_LocalPath;
    @Override
    public void run(TaskContext taskContext) {
        if (null != taskContext && taskContext.containsKey("config")) {
            Object obj = taskContext.get("config");
            if (null != obj && obj instanceof ConfigInfo) {
                m_Config = (ConfigInfo) obj;
                m_FtpIP = m_Config.getFtpIp();
                m_Port = m_Config.getFtpPort();
                m_Username = m_Config.getFtpUsername();
                m_Password = m_Config.getFtpPassword();
                m_FtpPath = m_Config.getFtpPath();
                m_LocalPath = Constants.ftp_backpath;
                m_FtpType=m_Config.getFtpType();
            }
        }
        if (StringUtil.isNullOrEmpty(m_FtpIP) || StringUtil.isNullOrEmpty(m_FtpPath) || StringUtil.isNullOrEmpty(m_LocalPath)
                || StringUtil.isNullOrEmpty(m_Username) || StringUtil.isNullOrEmpty(m_Password)) {
            return;
        }
        String[] files = FileUtil.listFiles(m_LocalPath, null);
        if (null == files || files.length < 1) {
            return;
        }
        IFTPClient ftpClient = FTPFactory.createClientByPort(m_FtpType,m_Port);
        try {
            ftpClient.ftpLogin(m_FtpIP, m_Port,m_FtpModel, m_Username, m_Password);

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
                    if(!"/".equals(m_FtpPath.substring(m_FtpPath.length()-1))&&!"\\".equals(m_FtpPath.substring(m_FtpPath.length()-1))){
                        m_FtpPath= m_FtpPath+File.separator;
                    }
                    ftpClient.upload(files[i], m_FtpPath + file.getName());
                    LogHelper.info("已上传文件："+files[i]);


                    if(null!=m_DownloadListeners)
                    {
                        FtpDownloadEvent e=new FtpDownloadEvent();
                        String ext= tgtools.tasklibrary.util.FileUtil.getFileExt(file.getName());
                        e.setFileName(file.getName());
                        e.setFileExt(ext);
                        e.setCancel(false);
                        m_DownloadListeners.deleteFile(e);
                        if(!e.getCancel())
                        {
                            deleteFile(files[i]);
                        }
                    }

                } catch (APPErrorException e) {
                    LogHelper.error("上传错误文件：" + files[i] + "ftp：" + m_FtpPath, e);
                }
            }
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
        finally {
            ftpClient.Dispose();
            ftpClient=null;
        }
    }
    private void deleteFile(String p_File)
    {
        for(int i=0;i<3;i++) {
           if( new File(p_File).delete())
           {return;}
        }
        LogHelper.error("删除文件失败",new APPErrorException(p_File));
    }
}
