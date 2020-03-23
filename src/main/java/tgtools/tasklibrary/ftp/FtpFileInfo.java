package tgtools.tasklibrary.ftp;

import java.util.Date;

/**
 * @author 田径
 * @date 2020-03-13 9:30
 * @desc
 **/
public class FtpFileInfo {

    /**
     * 文件名称
     */
    private String name;
    /**
     * 路径
     */
    private String path;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 是否是文件
     */
    protected boolean isFile;
    /**
     * 所属 用户
     */
    protected String owner;
    /**
     * 所属 角色
     */
    protected String group;
    /**
     * 权限
     */
    protected String permissions;

    /**
     *
     */
    protected Date lastModified;

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String pPath) {
        path = pPath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long pSize) {
        size = pSize;
    }

    public boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(boolean pFile) {
        isFile = pFile;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String pOwner) {
        owner = pOwner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String pGroup) {
        group = pGroup;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String pPermissions) {
        permissions = pPermissions;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date pLastModified) {
        lastModified = pLastModified;
    }
}
