package tgtools.tasklibrary.ftp.transfer;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.transfer.ftp.FtpFileTransfer;
import tgtools.tasklibrary.ftp.transfer.samba.SambaFileTransfer;
import tgtools.tasklibrary.ftp.transfer.sftp.SftpFileTransfer;

/**
 * @author 田径
 * @date 2020-03-17 9:12
 * @desc
 **/
public class FileTransferFactory {
    /**
     * ftp 默认端口
     */
    private final int PROTOCOL_DEFAULT_PORT_FTP = 21;
    /**
     * sftp 默认端口
     */
    private final int PROTOCOL_DEFAULT_PORT_SFTP = 22;
    //TCP端口139,445  UDP端口 137,138
    /**
     * samba tcp 端口1
     */
    private final int PROTOCOL_DEFAULT_PORT_SAMBA1 = 139;
    /**
     * samba tcp 端口2
     */
    private final int PROTOCOL_DEFAULT_PORT_SAMBA2 = 445;


    public FileTransfer createFileTransfer(FileServerInfo pFileServerInfo) throws APPErrorException {
        if (null == pFileServerInfo.getProtocol()) {
            switch (pFileServerInfo.getPort()) {
                case PROTOCOL_DEFAULT_PORT_FTP:
                    return new FtpFileTransfer(pFileServerInfo);
                case PROTOCOL_DEFAULT_PORT_SFTP:
                    return new SftpFileTransfer(pFileServerInfo);
                case PROTOCOL_DEFAULT_PORT_SAMBA1:
                case PROTOCOL_DEFAULT_PORT_SAMBA2:
                    return new SambaFileTransfer(pFileServerInfo);
                default:
                    throw new APPErrorException("找不到适合的FileTransfer");
            }
        } else {
            switch (pFileServerInfo.getProtocol()) {
                case FTP:
                    pFileServerInfo.setPort((pFileServerInfo.getPort() < 1) ? PROTOCOL_DEFAULT_PORT_FTP : pFileServerInfo.getPort());
                    return new FtpFileTransfer(pFileServerInfo);
                case SFTP:
                    pFileServerInfo.setPort((pFileServerInfo.getPort() < 1) ? PROTOCOL_DEFAULT_PORT_SFTP : pFileServerInfo.getPort());
                    return new SftpFileTransfer(pFileServerInfo);
                case SAMBA:
                    return new SambaFileTransfer(pFileServerInfo);
                default:
                    throw new APPErrorException("找不到适合的FileTransfer");
            }
        }
    }
}
