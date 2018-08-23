package tgtools.tasklibrary.listeners.entity;

/**
 * Created by tian_ on 2016-08-23.
 */
public class FtpDownloadEvent {

    private String m_FileName;
    private String m_FileExt;

    private boolean m_Cancel;

    public String getFileName() {
        return m_FileName;
    }

    public void setFileName(String p_FileName) {
        m_FileName = p_FileName;
    }

    public String getFileExt() {
        return m_FileExt;
    }

    public void setFileExt(String p_FileExt) {
        m_FileExt = p_FileExt;
    }

    public boolean getCancel() {
        return m_Cancel;
    }

    public void setCancel(boolean p_Cancel) {
        m_Cancel = p_Cancel;
    }
}
