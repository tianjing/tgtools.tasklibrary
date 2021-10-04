package tgtools.tasklibrary.util;

import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.ConfigInfo;
import tgtools.util.StringUtil;

/**
 * @author
 * Created by tian_ on 2016-07-26.
 */
public class DBHelper {

    public static void init(ConfigInfo pConfig) {
        m_Config = pConfig;
    }

    private static ConfigInfo m_Config;

    public static IDataAccess getDataAccess() {
        if (StringUtil.isNullOrEmpty(m_Config.getDataSource())) {
            return tgtools.db.DataBaseFactory.getDefault();
        } else {
            return tgtools.db.DataBaseFactory.get(m_Config.getDataSource());
        }
    }

    public static DataTable query(String pSql) throws APPErrorException {
        return getDataAccess().Query(pSql);
    }

}