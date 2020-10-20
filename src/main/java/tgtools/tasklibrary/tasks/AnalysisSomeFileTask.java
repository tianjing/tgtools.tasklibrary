package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.TaskContext;

import java.util.List;

/**
 * 解析多个E文件
 */
public class AnalysisSomeFileTask extends AnalysisOneFileTask {
    protected List<TableInfo> m_Tables;

    public AnalysisSomeFileTask() {
    }

    /**
     * 解析一个文件，解析完成后移动到指定目录
     *
     * @param p_File
     * @param p_BackDir
     * @param p_Table
     */
    public AnalysisSomeFileTask(String p_File, String p_BackDir, TableInfo p_Table) {
        super(p_File, p_BackDir, p_Table);
    }

    public AnalysisSomeFileTask(String p_File, String p_BackDir, List<TableInfo> p_Table) {
        super(p_File, p_BackDir, null);
        m_Tables = p_Table;
    }

    public List<TableInfo> getTables() {
        return m_Tables;
    }

    public void setM_Tables(List<TableInfo> pM_Tables) {
        m_Tables = pM_Tables;
    }

    @Override
    public void run(TaskContext p_Param) {
        LogHelper.info("AnalysisSomeFileTask 开始：" + m_File);
        try {
            if (null == m_Tables) {
                return;
            }
            for (int i = 0; i < m_Tables.size(); i++) {

                EFileAnalysisTask task = createEFileAnalysisTask();
                task.setFile(m_File);
                task.setTable(m_Tables.get(i));

                task.run(p_Param);
            }

            moveFile(p_Param);

            LogHelper.info("AnalysisSomeFileTask 结束：" + m_File);
        } catch (Exception ex) {
            LogHelper.error("解析文件出错：" + m_File, ex);

        }

    }


}
