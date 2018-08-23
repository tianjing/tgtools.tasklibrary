package tgtools.tasklibrary.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

@Root(name="Config")
public class ConfigInfo implements Serializable{


	/**
	 * @author tian.jing
	 * @date 2016年3月10日
	 */
	private static final long serialVersionUID = 3447473287483547086L;

	// 取FTP服务相关信息
	@Element(required=false,name="ftp_ip")
	private  String ftpIp = "";
	@Element(required=false,name="ftp_port")
	private  int ftpPort = 0;
	@Element(required=false,name="ftp_username")
	private  String ftpUsername = "";
	@Element(required=false,name="ftp_password")
	private  String ftpPassword = "";
	@Element(required=false,name="ftp_path")
	private  String ftpPath = "";
	@Element(required=false,name="ftp_type")
	private  String ftpType = "";

	@Element(required=false,name="ftp_model")
	private  String ftpModel = "PORT";//PASV


	@Element(required=false,name="SqlThread")
	private String SqlThread="";

	@ElementList(name = "FtpArray", entry = "Ftp", required = false)
	private ArrayList<FtpConfig> ftps;

	@Element(required=false,name="FileThread")
	private int fileThread;

	// 数据库配置信息
	@Element(required=false,name="DataSource")
	private  String dataSource ;


	@Element(required=false,name="SourceDir")
	private String sourceDir;

	@Element(required=false,name="TargetDir")
	private String targetDir;

	@Element(required=false)
	private int runInterval=0;


	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String pFtpIp) {
		ftpIp = pFtpIp;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int pFtpPort) {
		ftpPort = pFtpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String pFtpUsername) {
		ftpUsername = pFtpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String pFtpPassword) {
		ftpPassword = pFtpPassword;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String pFtpPath) {
		ftpPath = pFtpPath;
	}

	public String getFtpType() {
		return ftpType;
	}

	public void setFtpType(String pFtpType) {
		ftpType = pFtpType;
	}

	public String getFtpModel() {
		return ftpModel;
	}

	public void setFtpModel(String pFtpModel) {
		ftpModel = pFtpModel;
	}

	public String getSqlThread() {
		return SqlThread;
	}

	public void setSqlThread(String pSqlThread) {
		SqlThread = pSqlThread;
	}

	public ArrayList<FtpConfig> getFtps() {
		return ftps;
	}

	public void setFtps(ArrayList<FtpConfig> pFtps) {
		ftps = pFtps;
	}

	public int getFileThread() {
		return fileThread;
	}

	public void setFileThread(int pFileThread) {
		fileThread = pFileThread;
	}


	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String pDataSource) {
		dataSource = pDataSource;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String pSourceDir) {
		sourceDir = pSourceDir;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(String pTargetDir) {
		targetDir = pTargetDir;
	}

	public int getRunInterval() {
		return runInterval;
	}

	public void setRunInterval(int pRunInterval) {
		runInterval = pRunInterval;
	}
}
