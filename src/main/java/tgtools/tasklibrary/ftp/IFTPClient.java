package tgtools.tasklibrary.ftp;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by tian_ on 2016-07-18.
 */
public interface IFTPClient extends IDispose {
    String[] listFiles(String p_dirName, String[] p_extName)throws APPErrorException;

    void ftpLogin(String ftp_ip, int ftp_port, String ftp_model,
                  String ftp_username, String ftp_password)throws APPErrorException;


    void ftpLogin()throws APPErrorException;

    void closeFtp()throws APPErrorException;

    boolean createFileToLocal(String filepath, byte[] byteArr)throws APPErrorException;

    String[]  dirDetails(String m_path)throws APPErrorException;

    List<FtpFileInfo> lsDetails(String m_path)throws APPErrorException;

    void setEncoding(String pEncoding);
    String getEncoding();

    byte[] get(String file) throws APPErrorException;
    void get(String remoteFile, String localFile) throws APPErrorException;
    void get(String remoteFile, OutputStream outputStream) throws APPErrorException;

    void upload(String sourcefile, String targefile) throws APPErrorException;
    void upload(InputStream sourcefile, String targefile) throws APPErrorException;
    void delete(String file)throws APPErrorException;

    void setFtpModel(String pFtpModel);

}
