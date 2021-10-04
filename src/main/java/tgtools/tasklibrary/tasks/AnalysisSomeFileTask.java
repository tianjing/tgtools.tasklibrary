package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.TaskContext;

import java.util.List;

/**
 * 解析多个E文件
 * @author
 */
public class AnalysisSomeFileTask extends AnalysisOneFileTask {
    protected List<TableInfo> tables;

    public AnalysisSomeFileTask() {
    }

    /**
     * 解析一个文件，解析完成后移动到指定目录
     *
     * @param pFile
     * @param pBackDir
     * @param pTable
     */
    public AnalysisSomeFileTask(String pFile, String pBackDir, TableInfo pTable) {
        super(pFile, pBackDir, pTable);
    }

    public AnalysisSomeFileTask(String pFile, String pBackDir, List<TableInfo> pTable) {
        super(pFile, pBackDir, null);
        tables = pTable;
    }

    public List<TableInfo> getTables() {
        return tables;
    }

    public void setTables(List<TableInfo> pTables) {
        tables = pTables;
    }

    @Override
    public void run(TaskContext pParam) {
        LogHelper.info("AnalysisSomeFileTask 开始：" + file);
        try {
            if (null == tables) {
                return;
            }
            for (int i = 0; i < tables.size(); i++) {

                EFileAnalysisTask task = createEFileAnalysisTask();
                task.setFile(file);
                task.setTable(tables.get(i));

                task.run(pParam);
            }

            moveFile(pParam);

            LogHelper.info("AnalysisSomeFileTask 结束：" + file);
        } catch (Exception ex) {
            LogHelper.error("解析文件出错：" + file, ex);

        }

    }


}
