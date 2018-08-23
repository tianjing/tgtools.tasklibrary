package tgtools.tasklibrary.util;


import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;

import java.sql.Connection;
import java.sql.ResultSet;

public class DMDataAccess {

	public static void main(String[] args) {
		DMDataAccess R = new DMDataAccess();
		R.getConnection();
	}
	public  DMDataAccess()
	{
		
		this(null);
	}

	private IDataAccess m_DataAccess;

	public DMDataAccess(String p_DataSource)
	{
		if(tgtools.util.StringUtil.isNullOrEmpty(p_DataSource))
		{
			m_DataAccess=tgtools.db.DataBaseFactory.getDefault();

		}
		else
		{
			m_DataAccess=tgtools.db.DataBaseFactory.get(p_DataSource);
		}

	}
	public boolean init() {

	

		return true;
	}

	public Connection getConnection() {
		Connection m_Conn =null;

		return m_Conn;
	}

	public ResultSet executeQuery(String sql) throws APPErrorException {
		

		return m_DataAccess.executeQuery(sql);
	}
	public DataTable Query(String sql)throws APPErrorException
	{
		return m_DataAccess.Query(sql);
	}
	public int executeUpdate(String sql) throws APPErrorException {

		
		return m_DataAccess.executeUpdate(sql);
	}

	public int[] executeBatch(String[] sqls) throws APPErrorException {

		return m_DataAccess.executeBatch(sqls);
	}

	public void close(Connection p_Conn) {
		try {
			if (p_Conn != null)
				p_Conn.close();
		} catch (Exception localException) {
		}
		p_Conn = null;
	}

	public Connection createConnection() {
		return getConnection();
	}
}