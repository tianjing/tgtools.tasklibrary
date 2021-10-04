package tgtools.tasklibrary.tables;


import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.util.FileUtil;
import tgtools.util.StringUtil;
import tgtools.util.XmlSerialize;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author tianjing
 */
public class TableFactory {
    private static HashMap<String, TableInfo> tables;

    public static synchronized HashMap<String, TableInfo> getTables() {
        if (tables == null) {
            tables = new HashMap(20);
        }
        return tables;
    }

    public static synchronized void loadData() {
        loadData(getPatch());
    }

    public static synchronized void loadData(String pPath) {
        String[] files = FileUtil.listFiles(pPath, new String[]{"config"});
        for (String name : files) {
            try {

                Object obj = XmlSerialize.deserialize(FileUtil.readFile(name, "UTF-8"),
                        "TableInfo", TableInfo.class);
                if ((obj != null) && ((obj instanceof TableInfo))) {
                    TableInfo table = (TableInfo) obj;
                    table.SortColumnInfo();
                    getTables().put(name, table);
                    LogHelper.info("加载文件成功：" + name);

                }
            } catch (Exception e) {
                LogHelper.error("加载文件失败：" + name, e);
            }
        }
    }

    private static String getPatch() {
        String patch = System.getProperty("user.dir");
        return patch + "/config";
    }



    public static TableInfo getTableByStartName(String pFileName) {
        TableInfo[] list = getTables().values().toArray(new TableInfo[getTables().values().size()]);
        for (int i = 0; i < list.length; i++) {
            if (pFileName.startsWith(list[i].getFileName())) {
                return list[i];
            }
        }

        return null;
    }

    public static List<TableInfo> getTablesByStartName(String pFileName) {
        return getTablesByStartName(pFileName, null);
    }

    public static List<TableInfo> getTablesByStartName(String pFileName, String pFileExt) {
        List<TableInfo> list = new ArrayList<TableInfo>();
        if (!StringUtil.isNullOrEmpty(pFileName)) {
            TableInfo[] tables = getTables().values().toArray(new TableInfo[getTables().values().size()]);
            for (int i = 0; i < tables.length; i++) {
                if (pFileName.startsWith(tables[i].getFileName())) {
                    if (!StringUtil.isNullOrEmpty(pFileExt)) {
                        if (!pFileExt.equals(tables[i].getFileExt())) {
                            continue;
                        }
                    }
                    list.add(tables[i]);
                }
            }
        }
        return list;
    }

    public static boolean hasTablesByStartName(String pFileName, String pFileExt) {
        List<TableInfo> list = new ArrayList<TableInfo>();
        if (!StringUtil.isNullOrEmpty(pFileName)) {
            TableInfo[] tables = getTables().values().toArray(new TableInfo[getTables().values().size()]);
            for (int i = 0; i < tables.length; i++) {
                if (pFileName.startsWith(tables[i].getFileName())) {
                    if (!StringUtil.isNullOrEmpty(pFileExt)) {
                        if (!pFileExt.equals(tables[i].getFileExt())) {
                            continue;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}