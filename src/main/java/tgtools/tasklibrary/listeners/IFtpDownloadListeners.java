package tgtools.tasklibrary.listeners;

import tgtools.tasklibrary.listeners.entity.FtpDownloadEvent;

/**
 * Created by tian_ on 2016-08-23.
 *
 * @author tianjing
 */
public interface IFtpDownloadListeners {
    /**
     * ftp 下载监听
     *
     * @param e
     */
    void beforeDownload(FtpDownloadEvent e);

    /**
     * ftp 删除文件监听
     *
     * @param e
     */
    void deleteFile(FtpDownloadEvent e);
}
