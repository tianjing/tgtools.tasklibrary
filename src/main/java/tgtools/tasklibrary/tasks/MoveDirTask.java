package tgtools.tasklibrary.tasks;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.StringUtil;

import java.io.File;

/**
 * 移动文件任务
 */
public class MoveDirTask extends Task {

    private String m_SourceDir;
    private String m_TargeDir;
    private boolean m_includeDir;
    private String[] m_Names;
    private boolean m_IsStartName;

    public MoveDirTask() {

    }

    public MoveDirTask(String p_SourceDir, String p_TargeDir) {
        m_SourceDir = p_SourceDir;
        m_TargeDir = p_TargeDir;
        m_includeDir = true;
    }

    public MoveDirTask(String p_SourceDir, String p_TargeDir, boolean p_IncludeDir) {
        m_SourceDir = p_SourceDir;
        m_TargeDir = p_TargeDir;
        m_includeDir = p_IncludeDir;
    }

    public MoveDirTask(String p_SourceDir, String p_TargeDir, boolean p_IncludeDir, String[] p_Name, boolean p_IsStartName) {
        this(p_SourceDir, p_TargeDir, p_IncludeDir);
        m_Names = p_Name;
        m_IsStartName = p_IsStartName;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        if (StringUtil.isNullOrEmpty(m_SourceDir)) {
            Object obj1 = taskContext.get("SourceDir");
            if (null != obj1) {
                m_SourceDir = obj1.toString();
            }


        }

        if (StringUtil.isNullOrEmpty(m_TargeDir)) {
            Object obj1 = taskContext.get("TargeDir");
            if (null != obj1) {
                m_TargeDir = obj1.toString();
            }
        }

        if (StringUtil.isNullOrEmpty(m_TargeDir)) {
            LogHelper.error("错误的目标路径：" + m_TargeDir, new APPErrorException(""));
            return;
        }
        if (StringUtil.isNullOrEmpty(m_SourceDir)) {
            LogHelper.error("错误的源路径：" + m_SourceDir, new APPErrorException(""));
            return;
        }

        moveDir(m_SourceDir, m_TargeDir, m_includeDir);
    }

    private void moveDir(String p_Source, String p_Target, boolean p_IncludeDir) {
        File file = new File(p_Source);
        if (file.exists()) {
            if (file.isFile()) {
                moveFile(p_Source, p_Target);
            } else {

                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {

                    File temp = new File(p_Source + "/" + files[i]);
                    if (temp.isFile()) {
                        moveFile(p_Source + "/" + files[i], p_Target + "/" + files[i]);
                    } else if (temp.isDirectory()) {
                        if (!p_IncludeDir) {
                            continue;
                        }
                        File target = new File(p_Target + "/" + files[i]);
                        if (!target.exists()) {
                            target.mkdirs();
                        }
                        moveDir(p_Source + "/" + files[i], p_Target + "/" + files[i], p_IncludeDir);
                    }
                }
            }

        }

    }

    private boolean isValid(String p_File) {
        if (null == m_Names || m_Names.length < 1) {
            return true;
        }
        File file = new File(p_File);
        for (int i = 0, count = m_Names.length; i < count; i++) {
            if (m_IsStartName) {
                if (file.getName().startsWith(m_Names[i])) {
                    return true;
                }

            } else {
                int index = file.getName().lastIndexOf('.');
                if (index > -1) {
                    String ext = file.getName().substring(index + 1);
                    if (ext.equals(m_Names[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void moveFile(String p_Source, String p_Target) {
        File oldfile = new File(p_Source);
        File newfile = new File(p_Target);
        if (!isValid(p_Source)) {
            return;
        }
        if (!oldfile.exists()) {
            return;
        }
        if (newfile.exists()) {
            newfile.delete();
        }
        for (int i = 0; i < 3; i++) {
            if (oldfile.renameTo(newfile)) {
                LogHelper.info("移动文件成功，源："+p_Source+"目标："+p_Target);
                return;
            }
        }
        LogHelper.error("移动文件失败",new APPErrorException("源："+p_Source+"目标："+p_Target));
    }



}
