package tgtools.tasklibrary.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *  ftp配置 实体类
 * @author tianjing
 *
 */
@Root(name = "ftp")
public class FtpConfig {
    @Element(name = "FtpIP", required = false)
    private String m_FtpIP;
    @Element(name = "Port", required = false)
    private int m_Port;
    @Element(name = "UserName", required = false)
    private String m_UserName;
    @Element(name = "PassWord", required = false)
    private String m_PassWord;
    @Element(name = "TargetPath", required = false)
    private String m_TargetPath;
    @Element(name = "SourcePath", required = false)
    private String m_SourcePath;



    public String getFtpIP() {
        return m_FtpIP;
    }

    public void setFtpIP(String p_FtpIP) {
        m_FtpIP = p_FtpIP;
    }

    public int getPort() {
        return m_Port;
    }

    public void setPort(int p_Port) {
        m_Port = p_Port;
    }

    public String getUserName() {
        return m_UserName;
    }

    public void setUserName(String p_UserName) {
        m_UserName = p_UserName;
    }

    public String getPassWord() {
        return m_PassWord;
    }

    public void setPassWord(String p_PassWord) {
        m_PassWord = p_PassWord;
    }

    public String getTargetPath() {
        return m_TargetPath;
    }

    public void setTargetPath(String p_TargetPath) {
        m_TargetPath = p_TargetPath;
    }

    public String getSourcePath() {
        return m_SourcePath;
    }

    public void setSourcePath(String p_SourcePath) {
        m_SourcePath = p_SourcePath;
    }



}
