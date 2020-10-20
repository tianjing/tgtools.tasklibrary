package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;

import java.lang.reflect.Constructor;

/**
 * 解析一个E文件的任务
 */
public class AnalysisOneFileTask extends Task {

    protected String file;
    protected String backDir;
    protected TableInfo table;
    protected Class<? extends EFileAnalysisTask> eFileAnalysisTask;

    public AnalysisOneFileTask() {
    }

    /**
     * 解析一个文件，解析完成后移动到指定目录
     *
     * @param p_File
     * @param p_BackDir
     * @param p_Table
     */
    public AnalysisOneFileTask(String p_File, String p_BackDir, TableInfo p_Table) {
        file = p_File;
        backDir = p_BackDir;
        table = p_Table;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String pFile) {
        file = pFile;
    }

    public String getBackDir() {
        return backDir;
    }

    public void setBackDir(String pBackDir) {
        backDir = pBackDir;
    }

    public TableInfo getTable() {
        return table;
    }

    public void setTable(TableInfo pTable) {
        table = pTable;
    }

    public Class<? extends EFileAnalysisTask> getEFileAnalysisTask() {
        return eFileAnalysisTask;
    }

    public void setEFileAnalysisTask(Class<? extends EFileAnalysisTask> pEFileAnalysisTask) {
        eFileAnalysisTask = pEFileAnalysisTask;
    }

    protected EFileAnalysisTask createEFileAnalysisTask() {
        if (null == eFileAnalysisTask) {
            return new EFileAnalysisTask();
        }
        try {
            Constructor vConstructor = eFileAnalysisTask.getConstructor(null);
            return (EFileAnalysisTask) vConstructor.newInstance(null);
        } catch (Exception e) {
            return new EFileAnalysisTask();
        }
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext p_Param) {

        try {
            //解析文件
            EFileAnalysisTask task = createEFileAnalysisTask();

            task.setFile(file);
            task.setTable(table);

            task.run(p_Param);
            //移动文件
            moveFile(p_Param);
            LogHelper.info("已全结束：" + file);
        } catch (Exception ex) {
            LogHelper.error("解析文件出错：" + file, ex);

        }

    }

    protected void moveFile(TaskContext p_Param) {
        try {
            MoveBackFileTask movetask = new MoveBackFileTask(file, backDir);
            movetask.run(p_Param);
        } catch (Exception ex) {
            LogHelper.error("移动文件出错：" + file, ex);

        }
    }
}
