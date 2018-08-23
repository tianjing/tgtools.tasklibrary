package tgtools.tasklibrary.listeners;

import tgtools.tasklibrary.listeners.entity.FtpDownloadEvent;

/**
 * Created by tian_ on 2016-08-23.
 */
public interface IFtpDownloadListeners {
    void beforeDownload(FtpDownloadEvent e);
    void deleteFile(FtpDownloadEvent e);
}
