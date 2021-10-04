package tgtools.tasklibrary.core;

/**
 * 名  称：
 * @author:田径
 * 功  能：
 * 时  间：11:07
 */
public class Constants {
    /**
     * 监听
     */
    public static final String TASK_CONSTANTS_FTP_LISTENERS="Ftp_Listeners";
    /**
     * 增加配置文件路径
     */
    public static String CONFIG_PATH="";
    /**
     * 默认备份目录
     */
    public static String FTP_BACK_PATH = System.getProperty("user.dir")+"/file/";
    /**
     * 错误重试次数
     */
    public static int ERROR_RETRY_TIMES = 3;
    /**
     *  sftp 端口
     */
    public static int PORT_SFTP = 22;
    /**
     * ftp 端口
     */
    public static int PORT_FTP = 21;
    /**
     *  ftp 模式 主动
     */
    public static final String FTP_MODE_ACTIVE = "ACTIVE";
    /**
     * ftp 模式 被动
     */
    public static final String FTP_MODE_PASSIVE = "PASV";

}
