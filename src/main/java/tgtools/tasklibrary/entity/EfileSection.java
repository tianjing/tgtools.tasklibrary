package tgtools.tasklibrary.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian_ on 2016-07-05.
 */
public class EfileSection {
    public EfileSection()
    {
        m_Data =new ArrayList<String[]>();
    }
    private String m_Tag;
    private String m_TableName;
    private String m_Count;
    private String[] m_ColmunAlias;
    private String[] m_ColumnName;
    private List<String[]> m_Data;

    public void addData(String[] data)
    {
        if(null!=data&&data.length>0)
            m_Data.add(data);
    }
    public List<String[]> getData() {
        return m_Data;
    }

    public void setData(List<String[]> p_Data) {
        m_Data = p_Data;
    }

    public String getTag() {
        return m_Tag;
    }

    public void setTag(String p_Tag) {
        m_Tag = p_Tag;
    }

    public String getTableName() {
        return m_TableName;
    }

    public void setTableName(String p_TableName) {
        m_TableName = p_TableName;
    }

    public String getCount() {
        return m_Count;
    }

    public void setCount(String p_Count) {
        m_Count = p_Count;
    }

    public String[] getColmunAlias() {
        return m_ColmunAlias;
    }

    public void setColmunAlias(String[] p_ColmunAlias) {
        m_ColmunAlias = p_ColmunAlias;
    }

    public String[] getColumnName() {
        return m_ColumnName;
    }

    public void setColumnName(String[] p_ColumnName) {
        m_ColumnName = p_ColumnName;
    }


}
