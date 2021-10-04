package tgtools.tasklibrary.util;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.entity.ColumnInfo;
import tgtools.tasklibrary.entity.TableInfo;
import tgtools.util.DateUtil;
import tgtools.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author tianjing
 */
public class SqlHelper {
    private static String m_SeparateStr = ",";
    private static String m_NullValue = "null";

    public static String buildInsert(String[] pDatas, TableInfo pTable) throws APPErrorException {
        String sql = "Insert into {table} ({column}) values({values})";
        String columns = getColumns(pTable);
        String values = getValues(pDatas, pTable);

        sql = sql.replace("{table}", pTable.getTableName());
        sql = sql.replace("{column}", columns);
        sql = sql.replace("{values}", values);
        return sql + ";";
    }

    public static String buildUpdate(String[] pDatas, TableInfo pTable) throws APPErrorException {
        String sql = "Update {table} set {values} where {filter}";

        String values = getUpdateValues(pDatas, pTable);
        String filter = getFilter(pDatas, pTable);
        sql = sql.replace("{table}", pTable.getTableName());
        sql = sql.replace("{filter}", filter);
        sql = sql.replace("{values}", values);
        return sql + ";";
    }

    private static String getUpdateValues(String[] pDatas, TableInfo pTable) throws APPErrorException {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo item : pTable.getColumns()) {
            if (item.isIsPrimaryKey()) {
                continue;
            }
            String name = item.getName();
            String value = "";
            try {
                value = pDatas[(item.getIndex() - 1)];
            } catch (Exception e) {
                e.printStackTrace();
            }
            value = vaildValue(value, item);
            sb.append(name + "=" + value + m_SeparateStr);
        }
        sb = removeSeparateStr(sb);
        return sb.toString();
    }

    private static String getFilter(String[] pDatas, TableInfo pTable) throws APPErrorException {
        StringBuilder sb = new StringBuilder();
        List<ColumnInfo> list = pTable.getKeyPrimary();
        for (ColumnInfo item : list) {
            String name = item.getName();
            String value = pDatas[(item.getIndex() - 1)];
            value = vaildValue(value, item);
            sb.append(" and " + name + "=" + value);
        }
        if (sb.length() > 0) {
            sb.insert(0, " 1=1");
        }

        return sb.toString();
    }

    private static String getValues(String[] pDatas, TableInfo pTable) throws APPErrorException {
        if ((pDatas == null) || (pDatas.length < 1)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ColumnInfo info : pTable.getColumns()) {
            int index = info.getIndex();
            String value = "";
            if (index - 1 < pDatas.length) {
                value = pDatas[(index - 1)];
            }
            sb.append(vaildValue(value, info) + m_SeparateStr);
        }
        sb = removeSeparateStr(sb);
        return sb.toString();
    }

    private static String vaildValue(String pValue, ColumnInfo pInfo) throws APPErrorException {
        pValue = pValue.replace("\"", "");

        if ((pInfo.getDataType().startsWith("VARCHAR"))
                || (pInfo.getDataType().startsWith("CHAR"))) {
            return "'" + pValue + "'";
        }
        if ((pInfo.getDataType().startsWith("INT"))
                || (pInfo.getDataType().startsWith("NUMBER"))
                || (pInfo.getDataType().startsWith("DEC"))) {
            if (pValue.isEmpty()) {
                return m_NullValue;
            }
            return pValue;
        }
        if ((pInfo.getDataType().startsWith("TIMESTAMP"))
                || (pInfo.getDataType().startsWith("DATE"))) {
            if (pValue.isEmpty()) {
                return m_NullValue;
            }
            try {
                if (pValue.length() == 4) {
                    pValue = "01/01/" + pValue + " 00:00:00";
                } else if (pValue.length() == 10) {
                    if (pValue.indexOf("-") >= 0) {
                        pValue = StringUtil.replace(pValue, "-", "/");
                    }
                    pValue += " 00:00:00";
                } else if (pValue.length() == 19) {
                    pValue = StringUtil.replace(pValue, "-", "/");
                }
                Date date = new Date(Date.parse(pValue));

                return "'" + DateUtil.formatLongtime(date) + "'";
            } catch (Exception e) {
                throw new APPErrorException("时间转换出错", e);
            }
        }

        return m_NullValue;
    }

    private static String getColumns(TableInfo pTable) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo info : pTable.getColumns()) {
            sb.append(info.getName() + m_SeparateStr);
        }

        sb = removeSeparateStr(sb);
        return sb.toString();
    }

    private static StringBuilder removeSeparateStr(StringBuilder pStr) {
        if ((pStr.length() > 0)
                && (m_SeparateStr.equals(pStr.substring(pStr.length() - 1)))) {
            pStr.deleteCharAt(pStr.length() - 1);
        }
        return pStr;
    }
}