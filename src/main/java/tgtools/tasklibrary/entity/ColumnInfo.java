package tgtools.tasklibrary.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name="ColumnInfo")
public class ColumnInfo
  implements Serializable
{
  private static final long serialVersionUID = -5806324921830039678L;
  @Element
  private int Index;
  @Element
  private String Name;
  @Element
  private String DataType;
  @Element(required=false)
  private boolean IsPrimarykey;
  @Element(required=false)
  private String Alisa;

  public String getAlisa() {
    return this.Alisa;
  }
  public void setAlisa(String p_Alisa) {
    this.Alisa = p_Alisa;
  }
  public int getIndex()
  {
    return this.Index;
  }
  public void setIndex(int p_Index) {
    this.Index = p_Index;
  }
  public String getName() {
    return this.Name;
  }
  public void setName(String p_Name) {
    this.Name = p_Name;
  }
  public String getDataType() {
    return this.DataType;
  }
  public void setDataType(String p_DataType) {
    this.DataType = p_DataType;
  }
  public boolean isIsPrimarykey() {
    return this.IsPrimarykey;
  }
  public void setIsPrimarykey(boolean isPrimarykey) {
    this.IsPrimarykey = isPrimarykey;
  }
}