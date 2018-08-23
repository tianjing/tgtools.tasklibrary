package tgtools.tasklibrary.entity;

/**
 * 记录必须的服务器以及数据库配置信息
 * 
 * @author Administrator
 *
 */
public class serverConfig {
	// ftp服务器连接信息
	private String ftp_ip = "";
	private int ftp_port;
	private String ftp_username = "";
	private String ftp_password = "";
	private String ftp_path = "";
	private String ftp_backpath = "";

	// 数据库连接信息
	private String db_type = "";
	private String db_url = "";
	private String db_username = "";
	private String db_password = "";
	// 读取的数据文件
	private String ftp_file_array = null;

	public String getFtp_ip() {
		return ftp_ip;
	}

	public void setFtp_ip(String ftp_ip) {
		this.ftp_ip = ftp_ip;
	}

	public int getFtp_port() {
		return ftp_port;
	}

	public void setFtp_port(int ftp_port) {
		this.ftp_port = ftp_port;
	}

	public String getFtp_username() {
		return ftp_username;
	}

	public void setFtp_username(String ftp_username) {
		this.ftp_username = ftp_username;
	}

	public String getFtp_password() {
		return ftp_password;
	}

	public void setFtp_password(String ftp_password) {
		this.ftp_password = ftp_password;
	}

	public String getFtp_path() {
		return ftp_path;
	}

	public void setFtp_path(String ftp_path) {
		this.ftp_path = ftp_path;
	}

	public String getFtp_backpath() {
		return ftp_backpath;
	}

	public void setFtp_backpath(String ftp_backpath) {
		this.ftp_backpath = ftp_backpath;
	}

	public String getDb_type() {
		return db_type;
	}

	public void setDb_type(String db_type) {
		this.db_type = db_type;
	}

	public String getDb_url() {
		return db_url;
	}

	public void setDb_url(String db_url) {
		this.db_url = db_url;
	}

	public String getDb_username() {
		return db_username;
	}

	public void setDb_username(String db_username) {
		this.db_username = db_username;
	}

	public String getDb_password() {
		return db_password;
	}

	public void setDb_password(String db_password) {
		this.db_password = db_password;
	}

	public String getFtp_file_array() {
		return ftp_file_array;
	}

	public void setFtp_file_array(String ftp_file_array) {
		this.ftp_file_array = ftp_file_array;
	}

}
