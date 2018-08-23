package tgtools.tasklibrary.util;


public class LogHelper {
	public static void init(String p_BizName)
	{
		m_BizName=p_BizName;
	}
	private static String m_BizName="";
	public static void error(String p_Message, Exception p_Exception) {
		//田径 增加系统日志
		tgtools.util.LogHelper.error("", p_Message,m_BizName, p_Exception);
	}

	public static void info(String p_Message) {
		//田径 增加系统日志
		tgtools.util.LogHelper.infoForce("",  p_Message,m_BizName);
	}
}