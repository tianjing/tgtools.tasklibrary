package tgtools.tasklibrary.ftp.transfer;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.FtpFileInfo;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;

/**
 * @author 田径
 * @date 2020-03-12 16:50
 * @desc
 **/
public interface FileTransfer extends Closeable {


    /**
     * 列出指定目录下文件和目录信息
     * @param pPath  目录
     * @param pTreeDeep 是否查询子目录
     * @throws APPErrorException
     * @return
     */
    List<FtpFileInfo> ls(String pPath, boolean pTreeDeep) throws APPErrorException;

    /**
     *  下载文件
     * @param file 文件名及路径
     * @return
     * @throws APPErrorException
     */
    byte[] get(String file) throws APPErrorException;

    /**
     * 下载路径到本地
     * @param remoteFile 远程文件
     * @param localFile   本地文件
     * @throws APPErrorException
     */
    void get(String remoteFile, String localFile) throws APPErrorException;

    /**
     * 上传文件
     * @param sourcefile  本地文件
     * @param targefile  远程文件
     * @throws APPErrorException
     */
    void upload(String sourcefile, String targefile) throws APPErrorException;

    /**
     * 上传文件
     * @param sourcefile 本地文件流
     * @param targefile 远程文件
     * @throws APPErrorException
     */
    void upload(InputStream sourcefile, String targefile) throws APPErrorException;

    /**
     * 删除文件
     * @param file
     * @throws APPErrorException
     */
    void delete(String file) throws APPErrorException;

}
