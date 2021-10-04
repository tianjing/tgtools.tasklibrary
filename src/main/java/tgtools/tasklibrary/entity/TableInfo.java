package tgtools.tasklibrary.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import tgtools.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tianjing
 */
public class TableInfo
        implements Serializable {
    private static final long serialVersionUID = -8903372874373185550L;
    @Element
    private String FileName;
    @Element
    private String TableName;
    @Element
    private int Length;
    @ElementList(name = "Columns", entry = "ColumnInfo", required = false)
    private ArrayList<ColumnInfo> Columns;


    @Element(required = false)
    private String Split;
    @Element(required = false)
    private String TagName;
    @Element(required = false)
    private String Charset;

    @Element(required = false)
    private String DataColumn;
    @Element(required = false)
    private String System;
    @Element(required = false)
    private String OutSql;
    @Element(required = false)
    private String FileExt;

    private static ColumnInfo getColumnsByName(List<ColumnInfo> pList, String pName) {
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i).getName().equals(pName)) {
                return pList.get(i);
            }
        }
        return null;
    }

    private static ColumnInfo getColumnsByAlisa(List<ColumnInfo> pList, String pAlisa) {
        for (int i = 0; i < pList.size(); i++) {
            if (!StringUtil.isNullOrEmpty(pList.get(i).getAlisa()) && pList.get(i).getAlisa().equals(pAlisa)) {
                return pList.get(i);
            }
        }
        return null;
    }

    public String getFileExt() {
        return FileExt;
    }

    public void setFileExt(String fileExt) {
        FileExt = fileExt;
    }

    public String getOutSql() {
        return OutSql;
    }

    public void setOutSql(String outSql) {
        OutSql = outSql;
    }

    public boolean isAlisaDataColumn() {
        return "Alisa".equals(DataColumn);
    }

    public String getSystem() {
        return System;
    }

    public void setSystem(String system) {
        System = system;
    }

    public String getDataColumn() {
        return DataColumn;
    }

    public void setDataColumn(String pDataColumn) {
        DataColumn = pDataColumn;
    }

    public String getCharset() {
        return Charset;
    }

    public void setCharset(String pCharset) {
        Charset = pCharset;
    }

    public String getSplit() {
        return Split;
    }

    public void setSplit(String pSplit) {
        Split = pSplit;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String pTagName) {
        TagName = pTagName;
    }

    public void SortColumnInfo() {
        // Collections.sort(this.Columns, new ColumnInfoComparator());
    }

    public List<ColumnInfo> getKeyPrimary() {
        ArrayList list = new ArrayList();
        for (ColumnInfo info : this.Columns) {
            if (info.isIsPrimaryKey()) {
                list.add(info);
            }
        }

        return list;
    }

    public int getLength() {
        return this.Length;
    }

    public void setLength(int pLength) {
        this.Length = pLength;
    }

    public List<ColumnInfo> getColumns() {
        return this.Columns;
    }

    public void setColumns(ArrayList<ColumnInfo> pColumns) {
        this.Columns = pColumns;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String pFileName) {
        this.FileName = pFileName;
    }

    public String getTableName() {
        return this.TableName;
    }

    public void setTableName(String pTableName) {
        this.TableName = pTableName;
    }

    public ColumnInfo getColumnsByName(String pName) {
        return getColumnsByName(this.getColumns(), pName);
    }

    public ColumnInfo getColumnsByAlisa(String pAlisa) {
        return getColumnsByAlisa(this.getColumns(), pAlisa);
    }
}