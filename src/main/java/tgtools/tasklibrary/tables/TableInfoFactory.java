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

public class TableInfoFactory {
    private HashMap<String, TableInfo> m_Tables;

    public synchronized HashMap<String, TableInfo> getTables() {
        if (m_Tables == null) {
            m_Tables = new HashMap<String, TableInfo>();
        }
        return m_Tables;
    }

    public synchronized void loadData() {
        loadData(getPatch());
    }

    public synchronized void loadData(String m_Path) {
        String[] files = FileUtil.listFiles(m_Path, new String[]{"config"});
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

    private String getPatch() {
        String patch = System.getProperty("user.dir");
        return patch + "/config";
    }


    public TableInfo getTableByStartName(String p_FileName) {
        TableInfo[] list =getTables().values().toArray(new TableInfo[getTables().values().size()]);
        for(int i=0;i<list.length;i++) {
            if (p_FileName.startsWith(list[i].getFileName())) {
                return list[i];
            }
        }

        return null;
    }

    public List<TableInfo> getTablesByStartName(String p_FileName) {
        return getTablesByStartName(p_FileName,null);
    }
    public List<TableInfo> getTablesByStartName(String p_FileName, String p_FileExt) {
        List<TableInfo> list=new ArrayList<TableInfo>();
        if(!StringUtil.isNullOrEmpty(p_FileName))
        {
            TableInfo[] tables =getTables().values().toArray(new TableInfo[getTables().values().size()]);
            for(int i=0;i<tables.length;i++) {
                if (p_FileName.startsWith(tables[i].getFileName())) {
                    if(!StringUtil.isNullOrEmpty(p_FileExt))
                    {
                        if(!p_FileExt.equals(tables[i].getFileExt()))
                        {
                            continue;
                        }
                    }
                    list.add(tables[i]);
                }
            }
        }
        return list;
    }

    public boolean hasTablesByStartName(String p_FileName,String p_FileExt){
        List<TableInfo> list=new ArrayList<TableInfo>();
        if(!StringUtil.isNullOrEmpty(p_FileName))
        {
            TableInfo[] tables =getTables().values().toArray(new TableInfo[getTables().values().size()]);
            for(int i=0;i<tables.length;i++) {
                if (p_FileName.startsWith(tables[i].getFileName())) {
                    if(!StringUtil.isNullOrEmpty(p_FileExt))
                    {
                        if(!p_FileExt.equals(tables[i].getFileExt()))
                        {
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