package tgtools.tasklibrary.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 *
 * @author
 */
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
  public void setAlisa(String pAlisa) {
    this.Alisa = pAlisa;
  }
  public int getIndex()
  {
    return this.Index;
  }
  public void setIndex(int pIndex) {
    this.Index = pIndex;
  }
  public String getName() {
    return this.Name;
  }
  public void setName(String pName) {
    this.Name = pName;
  }
  public String getDataType() {
    return this.DataType;
  }
  public void setDataType(String pDataType) {
    this.DataType = pDataType;
  }
  public boolean isIsPrimaryKey() {
    return this.IsPrimarykey;
  }
  public void setIsPrimaryKey(boolean pIsPrimaryKey) {
    this.IsPrimarykey = pIsPrimaryKey;
  }
}