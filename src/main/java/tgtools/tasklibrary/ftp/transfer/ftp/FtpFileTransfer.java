package tgtools.tasklibrary.ftp.transfer.ftp;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.FTPClient;
import tgtools.tasklibrary.ftp.FtpFileInfo;
import tgtools.tasklibrary.ftp.transfer.FileServerInfo;
import tgtools.tasklibrary.ftp.transfer.FileTransfer;
import tgtools.tasklibrary.ftp.transfer.util.PathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 田径
 * @date 2020-03-12 17:00
 * @desc
 **/
public class FtpFileTransfer extends FTPClient implements FileTransfer {

    public FtpFileTransfer() {
    }

    public FtpFileTransfer(FileServerInfo pFileServerInfo) {
        if (null == pFileServerInfo) {
            return;
        }

        setIp(pFileServerInfo.getIp());
        setPort(pFileServerInfo.getPort());
        setUserName(pFileServerInfo.getUserName());
        setPassword(pFileServerInfo.getPassword());
        setEncoding(pFileServerInfo.getEncoding());
        if (null != pFileServerInfo.getFtpModel()) {
            setFtpModel(pFileServerInfo.getFtpModel().getValie());
        }
    }


    public FtpFileTransfer(String pIp, int pPort, String pUserName, String pPassword) {
        setIp(pIp);
        setPort(pPort);
        setUserName(pUserName);
        setPassword(pPassword);
    }


    @Override
    public List<FtpFileInfo> ls(String pPath, boolean pTreeDeep) throws APPErrorException {
        ftpLogin();
        if (!pTreeDeep) {
            return lsDetails(pPath);
        }
        List<FtpFileInfo> vResult = new ArrayList<>();
        lsAll(pPath, vResult);
        return vResult;
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

}
