package tgtools.tasklibrary.ftp.transfer;

/**
 * @author 田径
 * @date 2020-03-17 9:13
 * @desc
 **/
public class FileServerInfo {
    private String ip;
    private int port;
    private String userName;
    private String password;
    private String encoding;
    private FtpModelEnum ftpModel;

    private NetworkProtocolEnum protocol;

    public String getIp() {
        return ip;
    }

    public void setIp(String pIp) {
        ip = pIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int pPort) {
        port = pPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        password = pPassword;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String pEncoding) {
        encoding = pEncoding;
    }

    public NetworkProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(NetworkProtocolEnum pProtocol) {
        protocol = pProtocol;
    }

    public FtpModelEnum getFtpModel() {
        return ftpModel;
    }

    public void setFtpModel(FtpModelEnum pFtpModel) {
        ftpModel = pFtpModel;
    }
}
