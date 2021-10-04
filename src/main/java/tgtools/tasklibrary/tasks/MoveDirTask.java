package tgtools.tasklibrary.tasks;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.StringUtil;

import java.io.File;

/**
 * 移动文件任务
 * @author tianjing
 */
public class MoveDirTask extends Task {

    private String m_SourceDir;
    private String m_TargeDir;
    private boolean m_includeDir;
    private String[] m_Names;
    private boolean m_IsStartName;

    public MoveDirTask() {

    }

    public MoveDirTask(String pSourceDir, String pTargeDir) {
        m_SourceDir = pSourceDir;
        m_TargeDir = pTargeDir;
        m_includeDir = true;
    }

    public MoveDirTask(String pSourceDir, String pTargeDir, boolean pIncludeDir) {
        m_SourceDir = pSourceDir;
        m_TargeDir = pTargeDir;
        m_includeDir = pIncludeDir;
    }

    public MoveDirTask(String pSourceDir, String pTargeDir, boolean pIncludeDir, String[] pName, boolean pIsStartName) {
        this(pSourceDir, pTargeDir, pIncludeDir);
        m_Names = pName;
        m_IsStartName = pIsStartName;
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

    private void moveDir(String pSource, String pTarget, boolean pIncludeDir) {
        File file = new File(pSource);
        if (file.exists()) {
            if (file.isFile()) {
                moveFile(pSource, pTarget);
            } else {

                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {

                    File temp = new File(pSource + "/" + files[i]);
                    if (temp.isFile()) {
                        moveFile(pSource + "/" + files[i], pTarget + "/" + files[i]);
                    } else if (temp.isDirectory()) {
                        if (!pIncludeDir) {
                            continue;
                        }
                        File target = new File(pTarget + "/" + files[i]);
                        if (!target.exists()) {
                            target.mkdirs();
                        }
                        moveDir(pSource + "/" + files[i], pTarget + "/" + files[i], pIncludeDir);
                    }
                }
            }

        }

    }

    private boolean isValid(String pFile) {
        if (null == m_Names || m_Names.length < 1) {
            return true;
        }
        File file = new File(pFile);
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

    private void moveFile(String pSource, String pTarget) {
        File oldfile = new File(pSource);
        File newfile = new File(pTarget);
        if (!isValid(pSource)) {
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
                LogHelper.info("移动文件成功，源：" + pSource + "目标：" + pTarget);
                return;
            }
        }
        LogHelper.error("移动文件失败", new APPErrorException("源：" + pSource + "目标：" + pTarget));
    }


}
