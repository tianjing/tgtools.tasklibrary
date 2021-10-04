package tgtools.tasklibrary.util;

/**
 * @author tianjing
 */
public class LogHelper {
    private static String m_BizName = "";

    public static void init(String pBizName) {
        m_BizName = pBizName;
    }

    public static void error(String pMessage, Exception pException) {
        //田径 增加系统日志
        tgtools.util.LogHelper.error("", pMessage, m_BizName, pException);
    }

    public static void info(String pMessage) {
        //田径 增加系统日志
        tgtools.util.LogHelper.infoForce("", pMessage, m_BizName);
    }
}