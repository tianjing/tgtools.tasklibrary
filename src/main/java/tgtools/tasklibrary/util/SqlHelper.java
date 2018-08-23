package tgtools.tasklibrary.util;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.entity.ColumnInfo;
import tgtools.tasklibrary.entity.TableInfo;
import tgtools.util.DateUtil;
import tgtools.util.StringUtil;

import java.util.Date;
import java.util.List;

public class SqlHelper {
    private static String m_SeparateStr = ",";
    private static String m_NullValue = "null";

    public static String buildInsert(String[] p_Datas, TableInfo p_Table) throws APPErrorException {
        String sql = "Insert into {table} ({column}) values({values})";
        String columns = getColumns(p_Table);
        String values = getValues(p_Datas, p_Table);

        sql = sql.replace("{table}", p_Table.getTableName());
        sql = sql.replace("{column}", columns);
        sql = sql.replace("{values}", values);
        return sql + ";";
    }

    public static String buildUpdate(String[] p_Datas, TableInfo p_Table) throws APPErrorException {
        String sql = "Update {table} set {values} where {filter}";

        String values = getUpdateValues(p_Datas, p_Table);
        String filter = getFilter(p_Datas, p_Table);
        sql = sql.replace("{table}", p_Table.getTableName());
        sql = sql.replace("{filter}", filter);
        sql = sql.replace("{values}", values);
        return sql + ";";
    }

    private static String getUpdateValues(String[] p_Datas, TableInfo p_Table) throws APPErrorException {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo item : p_Table.getColumns()) {
            if (item.isIsPrimarykey()) {
                continue;
            }
            String name = item.getName();
            String value = "";
            try {
                value = p_Datas[(item.getIndex() - 1)];
            } catch (Exception e) {
                e.printStackTrace();
            }
            value = vaildValue(value, item);
            sb.append(name + "=" + value + m_SeparateStr);
        }
        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    private static String getFilter(String[] p_Datas, TableInfo p_Table) throws APPErrorException {
        StringBuilder sb = new StringBuilder();
        List<ColumnInfo> list = p_Table.getKeyPrimary();
        for (ColumnInfo item : list) {
            String name = item.getName();
            String value = p_Datas[(item.getIndex() - 1)];
            value = vaildValue(value, item);
            sb.append(" and " + name + "=" + value);
        }
        if (sb.length() > 0) {
            sb.insert(0, " 1=1");
        }

        return sb.toString();
    }

    private static String getValues(String[] p_Datas, TableInfo p_Table) throws APPErrorException {
        if ((p_Datas == null) || (p_Datas.length < 1)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ColumnInfo info : p_Table.getColumns()) {
            int index = info.getIndex();
            String value = "";
            if (index - 1 < p_Datas.length) {
                value = p_Datas[(index - 1)];
            }
            sb.append(vaildValue(value, info) + m_SeparateStr);
        }
        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    private static String vaildValue(String p_Value, ColumnInfo p_Info) throws APPErrorException {
        p_Value = p_Value.replace("\"", "");

        if ((p_Info.getDataType().startsWith("VARCHAR"))
				||(p_Info.getDataType().startsWith("CHAR"))){
            return "'" + p_Value + "'";
        }
        if ((p_Info.getDataType().startsWith("INT"))
                || (p_Info.getDataType().startsWith("NUMBER"))
                || (p_Info.getDataType().startsWith("DEC"))) {
            if (p_Value.isEmpty()) {
                return m_NullValue;
            }
            return p_Value;
        }
        if ((p_Info.getDataType().startsWith("TIMESTAMP"))
                || (p_Info.getDataType().startsWith("DATE"))) {
            if (p_Value.isEmpty()) {
                return m_NullValue;
            }
            try {
                if (p_Value.length() == 4) {
                    p_Value = "01/01/" + p_Value + " 00:00:00";
                } else if (p_Value.length() == 10) {
                    if (p_Value.indexOf("-") >= 0) {
                        p_Value = StringUtil.replace(p_Value, "-", "/");
                    }
                    p_Value += " 00:00:00";
                } else if (p_Value.length() == 19) {
                    p_Value = StringUtil.replace(p_Value, "-", "/");
                }
                Date date = new Date(Date.parse(p_Value));

                return "'" + DateUtil.formatLongtime(date) + "'";
            } catch (Exception e) {
                throw new APPErrorException("时间转换出错", e);
            }
        }

        return m_NullValue;
    }

    private static String getColumns(TableInfo p_Table) {
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo info : p_Table.getColumns()) {
            sb.append(info.getName() + m_SeparateStr);
        }

        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    private static StringBuilder RemoveSeparateStr(StringBuilder p_Str) {
        if ((p_Str.length() > 0)
                && (m_SeparateStr.equals(p_Str.substring(p_Str.length() - 1)))) {
            p_Str.deleteCharAt(p_Str.length() - 1);
        }
        return p_Str;
    }
}