package tgtools.tasklibrary.ftp;

import tgtools.util.StringUtil;

/**
 * Created by tian_ on 2016-07-19.
 */
public final class FTPFactory {
    public static IFTPClient createClientByPort(String pFtpType, int port) {
        if ("ftp".equals(pFtpType))
        {
            return new FTPClient();
        }
        else if ("sftp".equals(pFtpType))
        {
            return new SFTPClient();
        }
        else {
            if (StringUtil.isNullOrEmpty(pFtpType) || (!"ftp".equals(pFtpType.toLowerCase()) && !"sftp".equals(pFtpType.toLowerCase()))) {
                if (22 == port) {
                    return new SFTPClient();
                } else if (21 == port) {
                    return new FTPClient();
                }
            }

            return null;
        }
    }

    public static IFTPClient createSftpClient() {
        return new SFTPClient();
    }
}
