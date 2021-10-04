package tgtools.tasklibrary.ftp;

import tgtools.exceptions.APPErrorException;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by tian_ on 2016-07-18.
 * @author tianjing
 */
public interface IFTPClient extends Closeable {
    /**
     * 获取文件信息
     * @param pDirName
     * @param pExtName
     * @return
     * @throws APPErrorException
     */
    String[] listFiles(String pDirName, String[] pExtName)throws APPErrorException;

    /**
     * ftp 登录
     * @param ftpIp
     * @param ftpPort
     * @param ftpModel
     * @param ftpUserName
     * @param ftpPassword
     * @throws APPErrorException
     */
    void ftpLogin(String ftpIp, int ftpPort, String ftpModel,
                  String ftpUserName, String ftpPassword)throws APPErrorException;

    /**
     * ftp 登录
     * @throws APPErrorException
     */
    void ftpLogin()throws APPErrorException;

    /**
     *  关闭ftp
     * @throws APPErrorException
     */
    void closeFtp()throws APPErrorException;

    /**
     * 将内容保存到本地文件
     * @param filepath
     * @param byteArr
     * @return
     * @throws APPErrorException
     */
    boolean createFileToLocal(String filepath, byte[] byteArr)throws APPErrorException;

    /**
     * 获取目录信息
     * @param pPath
     * @return
     * @throws APPErrorException
     */
    String[]  dirDetails(String pPath)throws APPErrorException;

    /**
     * 获取目录详情
     * @param pPath
     * @return
     * @throws APPErrorException
     */
    List<FtpFileInfo> lsDetails(String pPath)throws APPErrorException;

    /**
     * 设置 字符集编码
     * @param pEncoding
     */
    void setEncoding(String pEncoding);

    /**
     * 获取 字符集编码
     * @return
     */
    String getEncoding();

    /**
     * 获取文件数据
     * @param file
     * @return
     * @throws APPErrorException
     */
    byte[] get(String file) throws APPErrorException;

    /**
     * 将文件下载的本地
     * @param remoteFile
     * @param localFile
     * @throws APPErrorException
     */
    void get(String remoteFile, String localFile) throws APPErrorException;

    /**
     *  将文件下载的本地
     * @param remoteFile
     * @param outputStream
     * @throws APPErrorException
     */
    void get(String remoteFile, OutputStream outputStream) throws APPErrorException;

    /**
     * 将文件上传的本地
     * @param sourcefile
     * @param targefile
     * @throws APPErrorException
     */
    void upload(String sourcefile, String targefile) throws APPErrorException;

    /**
     * 将文件上传的本地
     * @param sourcefile
     * @param targefile
     * @throws APPErrorException
     */
    void upload(InputStream sourcefile, String targefile) throws APPErrorException;

    /**
     * 删除远程文件
     * @param file
     * @throws APPErrorException
     */
    void delete(String file)throws APPErrorException;

    /**
     * 设置 ftp 模式
     * @param pFtpModel
     */
    void setFtpModel(String pFtpModel);

}
