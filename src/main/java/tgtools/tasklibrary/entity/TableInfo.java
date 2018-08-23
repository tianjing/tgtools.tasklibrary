package tgtools.tasklibrary.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import tgtools.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TableInfo
  implements Serializable
{
  private static final long serialVersionUID = -8903372874373185550L;
  @Element
  private String FileName;
  @Element
  private String TableName;
  @Element
  private int Length;
  @ElementList(name="Columns",entry="ColumnInfo",required = false)
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

  public boolean isAlisaDataColumn()
  {
    return "Alisa".equals(DataColumn);
  }

  public String getSystem() {
    return System;
  }

  public void setSystem(String system) {
    System = system;
  }

  public String getDataColumn() {return DataColumn;  }
  public void setDataColumn(String p_DataColumn) {
    DataColumn = p_DataColumn;
  }

  public String getCharset() {return Charset;  }
  public void setCharset(String p_Charset) {
    Charset = p_Charset;
  }

  public String getSplit() {
    return Split;
  }

  public void setSplit(String p_Split) {
    Split = p_Split;
  }

  public String getTagName() {
    return TagName;
  }

  public void setTagName(String p_TagName) {
    TagName = p_TagName;
  }

  public void SortColumnInfo()
  {
   // Collections.sort(this.Columns, new ColumnInfoComparator());
  }

  public List<ColumnInfo> getKeyPrimary()
  {
    ArrayList list = new ArrayList();
    for (ColumnInfo info : this.Columns)
    {
      if (info.isIsPrimarykey()) {
        list.add(info);
      }
    }

    return list;
  }
  public int getLength() {
    return this.Length;
  }
  public void setLength(int p_Length) {
    this.Length = p_Length;
  }
  public List<ColumnInfo> getColumns() {
    return this.Columns;
  }
  public void setColumns(ArrayList<ColumnInfo> p_Columns) {
    this.Columns = p_Columns;
  }
  public String getFileName() {
    return this.FileName;
  }
  public void setFileName(String fileName) {
    this.FileName = fileName;
  }
  public String getTableName() {
    return this.TableName;
  }
  public void setTableName(String tableName) {
    this.TableName = tableName;
  }



  public  ColumnInfo getColumnsByName(String p_Name)
  {
    return getColumnsByName(this.getColumns(),p_Name);
  }
  public  ColumnInfo getColumnsByAlisa(String p_Alisa)
  {
    return getColumnsByAlisa(this.getColumns(),p_Alisa);
  }

  private static ColumnInfo getColumnsByName(List<ColumnInfo> p_List,String p_Name)
  {
    for(int i=0;i<p_List.size();i++)
    {
      if(p_List.get(i).getName().equals(p_Name))
      {
        return p_List.get(i);
      }
    }
    return null;
  }
  private static ColumnInfo getColumnsByAlisa(List<ColumnInfo> p_List,String p_Alisa)
  {
    for(int i=0;i<p_List.size();i++)
    {
      if(!StringUtil.isNullOrEmpty(p_List.get(i).getAlisa())&&p_List.get(i).getAlisa().equals(p_Alisa))
      {
        return p_List.get(i);
      }
    }
    return null;
  }
}