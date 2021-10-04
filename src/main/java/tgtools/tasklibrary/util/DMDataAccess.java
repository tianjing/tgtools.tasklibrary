package tgtools.tasklibrary.util;


import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * @author tianjing
 */
public class DMDataAccess {


    private IDataAccess dataAccess;

    public DMDataAccess() {

        this(null);
    }

    public DMDataAccess(String pDataSource) {
        if (tgtools.util.StringUtil.isNullOrEmpty(pDataSource)) {
            dataAccess = tgtools.db.DataBaseFactory.getDefault();

        } else {
            dataAccess = tgtools.db.DataBaseFactory.get(pDataSource);
        }

    }

    public boolean init() {


        return true;
    }


    public ResultSet executeQuery(String sql) throws APPErrorException {
        return dataAccess.executeQuery(sql);
    }

    public DataTable Query(String sql) throws APPErrorException {
        return dataAccess.Query(sql);
    }

    public int executeUpdate(String sql) throws APPErrorException {
        return dataAccess.executeUpdate(sql);
    }

    public int[] executeBatch(String[] sqls) throws APPErrorException {
        return dataAccess.executeBatch(sqls);
    }

    public void close(Connection p_Conn) {
        try {
            if (p_Conn != null) {
                p_Conn.close();
            }
        } catch (Exception localException) {
        }
        p_Conn = null;
    }
}