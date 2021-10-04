package tgtools.tasklibrary.util;

import com.enterprisedt.net.ftp.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author tianjing
 */
public class FtpUtil {

	public static String[] listFiles(String pDirName, String[] pExtName) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File dir = new File(pDirName);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (!file.isFile()) {
					continue;
				}
				try {
					String[] s = file.getCanonicalPath().split("\\\\");
					if (s.length > 0) {
						String fileExtName = s[(s.length - 1)];
						for (String extName : pExtName) {
							if (fileExtName.equalsIgnoreCase(extName)) {
								fileNames.add(file.getCanonicalPath());
								break;
							}
						}
					}else{
						fileNames.add(file.getCanonicalPath());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (String[]) fileNames.toArray(new String[fileNames.size()]);
	}
	/**
	 * 登录远程FTP服务器
	 * @param ftp_ip
	 * @param ftp_port
	 * @param ftp_username
	 * @param ftp_password
	 * @return
	 */
	public static FTPClient ftpLogin(String ftp_ip, int ftp_port,
			String ftp_username, String ftp_password) {
		FTPClient client = new FTPClient();
		try {
			// 指定服务器地址
			client.setRemoteHost(ftp_ip);
			// 端口号
			client.setRemotePort(ftp_port);
			// 读取文件编码格式
			client.setControlEncoding("GBK");
			// 服务器端监听
			FTPMessageCollector listener = new FTPMessageCollector();
			client.setMessageListener(listener);
			client.setTimeout(999999999);
			client.connect();
			client.login(ftp_username, ftp_password);
			client.setConnectMode(FTPConnectMode.PASV);
			client.setType(FTPTransferType.BINARY);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return client;
	}

	/**
	 * 关闭连接
	 * @param client
	 */
	public static void closeFtp(FTPClient client) {
		try {
			if (client != null) {
				client.quit();
				client = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 将文件在本地生成一份
	 *
	 * @param filepath
	 * @param byteArr
	 */
	public static boolean createFileToLocal(String filepath, byte[] byteArr) {
		boolean flag = true;
		File file = new File(filepath);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			flag = false;
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			try {
				out.write(byteArr, 0, byteArr.length);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 登录远程FTP服务器
	 * @param ftp_ip
	 * @param ftp_port
	 * @param ftp_username
	 * @param ftp_password
	 * @return
	 */
	public static FileTransferClient FileTransferClientLogin(String ftp_ip, int ftp_port,
			String ftp_username, String ftp_password) {
		FileTransferClient client = new FileTransferClient();
		try {
			client.setRemoteHost(ftp_ip); // 指定服务器地址
			client.setRemotePort(ftp_port); // 端口号
			client.setUserName(ftp_username);
			client.setPassword(ftp_password);
			client.getAdvancedSettings().setControlEncoding("GBK");
			client.getAdvancedSettings().setTransferBufferSize(1024);
			client.getAdvancedSettings().setTransferNotifyInterval(5000);
			
			client.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return client;
	}
	
	public static boolean disconnect(FileTransferClient client){
		try {
			client.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	public static void ftpDownload(FileTransferClient client,String localFilePath,String remoteFilePath){
		try {
			client.downloadFile(localFilePath, remoteFilePath, com.enterprisedt.net.ftp.WriteMode.OVERWRITE);
		} catch (Exception e) {
		}
	}

}
