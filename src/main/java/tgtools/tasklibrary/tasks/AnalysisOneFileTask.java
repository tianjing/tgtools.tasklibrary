package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;

/**
 * 解析一个E文件的任务
 */
public class AnalysisOneFileTask extends Task {
    /**
     * 解析一个文件，解析完成后移动到指定目录
     *
     * @param p_File
     * @param p_BackDir
     * @param p_Table
     */
    public AnalysisOneFileTask(String p_File, String p_BackDir, TableInfo p_Table) {
        m_File = p_File;
        m_BackDir = p_BackDir;
        m_Table = p_Table;
    }

    protected String m_File;
    protected String m_BackDir;
    protected TableInfo m_Table;

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext p_Param) {

        try {
            //解析文件
            EFileAnalysisTask task = new EFileAnalysisTask(m_File, m_Table);
            task.run(p_Param);
            //移动文件
            moveFile(p_Param);
            LogHelper.info("已全结束：" + m_File);
        } catch (Exception ex) {
            LogHelper.error("解析文件出错：" + m_File, ex);

        }

    }
    protected  void moveFile(TaskContext p_Param)
    {
        try {
            MoveBackFileTask movetask = new MoveBackFileTask(m_File, m_BackDir);
            movetask.run(p_Param);
        } catch (Exception ex) {
            LogHelper.error("移动文件出错：" + m_File, ex);

        }
    }
}
