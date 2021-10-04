package tgtools.tasklibrary.ftp.transfer.sftp;

import com.jcraft.jsch.ChannelSftp;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.FtpFileInfo;
import tgtools.tasklibrary.ftp.SFTPClient;
import tgtools.tasklibrary.ftp.transfer.FileServerInfo;
import tgtools.tasklibrary.ftp.transfer.FileTransfer;
import tgtools.tasklibrary.ftp.transfer.util.PathUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 田径
 * @date 2020-03-12 17:00
 * @desc
 **/
public class SftpFileTransfer extends SFTPClient implements FileTransfer {
    public SftpFileTransfer() {
    }

    public SftpFileTransfer(FileServerInfo pFileServerInfo) {
        setIp(pFileServerInfo.getIp());
        setPort(pFileServerInfo.getPort());
        setUserName(pFileServerInfo.getUserName());
        setPassword(pFileServerInfo.getPassword());
        setEncoding(pFileServerInfo.getEncoding());
    }


    public SftpFileTransfer(String pIp, int pPort, String pUsername, String pPassword) {
        setIp(pIp);
        setPort(pPort);
        setUserName(pUsername);
        setPassword(pPassword);
    }

    public ChannelSftp initClient() throws APPErrorException {
        if (null == getClient()) {
            ftpLogin();
        }
        return getClient();
    }

    @Override
    public String[] listFiles(String pDirName, String[] pExtName) {
        try {
            initClient();
            super.listFiles(pDirName, pExtName);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FtpFileInfo> ls(String pPath, boolean pTreeDeep) throws APPErrorException {
        initClient();
        if (!pTreeDeep) {
            return lsDetails(pPath);
        } else {
            List<FtpFileInfo> vResult = new ArrayList();
            lsAll(pPath, vResult);
            return vResult;
        }
    }

    @Override
    public List<FtpFileInfo> lsDetails(String mPath) throws APPErrorException {
        initClient();
        return super.lsDetails(mPath);
    }

    protected void lsAll(String pPath, List<FtpFileInfo> pResult) throws APPErrorException {
        List<FtpFileInfo> vFiles = lsDetails(pPath);
        pResult.addAll(vFiles);
        for (FtpFileInfo vFile : vFiles) {
            if (!vFile.getIsFile()) {
                try {
                    lsAll(PathUtils.joinPath(vFile.getPath(), vFile.getName()).toString(), pResult);
                } catch (Exception e) {
                    //忽略递归错误，忽略权限问题无法读取。
                }
            }
        }
    }

    @Override
    public byte[] get(String pFile) throws APPErrorException {
        initClient();
        return super.get(pFile);
    }

    @Override
    public void get(String pRemoteFile, String pLocalFile) throws APPErrorException {
        initClient();
        super.get(pRemoteFile, pLocalFile);
    }

    @Override
    public void upload(String pSourcefile, String pTargefile) throws APPErrorException {
        initClient();
        super.upload(pSourcefile, pTargefile);
    }

    @Override
    public void upload(InputStream pSourcefile, String pTargefile) throws APPErrorException {
        initClient();
        super.upload(pSourcefile, pTargefile);
    }

    @Override
    public void delete(String pFile) throws APPErrorException {
        initClient();
        super.delete(pFile);
    }

}
