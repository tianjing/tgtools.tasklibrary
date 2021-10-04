package tgtools.tasklibrary.tasks;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.ConfigInfo;
import tgtools.tasklibrary.entity.*;
import tgtools.tasklibrary.util.DMDataAccess;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasklibrary.util.SqlHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.tasks.TaskRunner;
import tgtools.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 将每一个E文件标签内容转换成sql
 * @author tianjing
 */
public class EFileAnalysisSqlTask extends Task {

    private TableInfo table;
    private DataCenter datas;
    private Queue<Task> tasks;
    private DMDataAccess dmDataAccess;
    private int sqlOfThread;

    public EFileAnalysisSqlTask() {
        this(null);
    }

    public EFileAnalysisSqlTask(TableInfo p_Table) {
        this(p_Table,10000);
    }

    /**
     * @param p_Table
     * @param p_SqlOfThread 多少sql 一个线程,0或小于0则只是用一个线程（ 比如 输入10000 如果sql 数量10001则会使用2个线程处理这些sql语句）
     */
    public EFileAnalysisSqlTask(TableInfo p_Table, int p_SqlOfThread) {
        table = p_Table;
        datas = new DataCenter();
        sqlOfThread = p_SqlOfThread;
    }

    public TableInfo getTable() {
        return table;
    }

    public void setTable(TableInfo pTable) {
        table = pTable;
    }

    public DMDataAccess getDmDataAccess() {
        return dmDataAccess;
    }

    public void setDmDataAccess(DMDataAccess pDmDataAccess) {
        dmDataAccess = pDmDataAccess;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {

        if (taskContext.containsKey("config")) {

            Object obj = taskContext.get("config");

            if (null != obj && obj instanceof ConfigInfo) {

                ConfigInfo config = (ConfigInfo) obj;
                if (null == dmDataAccess) {
                    dmDataAccess = new DMDataAccess(config.getDataSource());
                }

                if (!StringUtil.isNullOrEmpty(config.getSqlThread())) {
                    try {
                        sqlOfThread = Integer.valueOf(config.getSqlThread());
                    } catch (Exception e) {
                        sqlOfThread = 0;
                    }
                }
            }
        }
        if (null == dmDataAccess) {
            LogHelper.error("StepDataProcessSqlTask 解析失败", new Exception("数据库访问参数失败"));
            return;
        }
        tasks = new ConcurrentLinkedQueue<Task>();


        if (taskContext.containsKey("EfileSection") && null != taskContext.get("EfileSection")) {
            List<EfileSection> list = (List<EfileSection>) taskContext.get("EfileSection");
            for (int i = 0; i < list.size(); i++) {

                if (!StringUtil.isNullOrEmpty(table.getTagName()) && table.getTagName().equals(list.get(i).getTag())) {
                    try {
                        parseAllSql(list.get(i));
                    } catch (Exception e) {
                        LogHelper.error("解析数据出错", e);
                    }
                }
            }

        }

        tasks = new ConcurrentLinkedQueue<Task>();
        int datacount = datas.size();
        //int processcount=0;
        int threadcount = sqlOfThread < 1 ? 1 : datacount / sqlOfThread;
        if (datas.size() < 1) {
            return;
        }

        if (threadcount > 20) {
            threadcount = 20;
        } else if (threadcount < 1) {
            threadcount = 1;
        }
        LogHelper.info(table.getTableName() + " sql处理开始");
        TaskRunner<StepSqlProcessTask> runner = new TaskRunner<StepSqlProcessTask>();
        for (int i = 0; i < threadcount; i++) {

            StepSqlProcessTask task = new StepSqlProcessTask(datas);
            runner.add(task);
        }
        runner.runThreadTillEnd(taskContext);

    }

    /**
     * 将标签转换sql
     *
     * @param p_EFile
     * @throws APPErrorException
     */
    protected void parseAllSql(EfileSection p_EFile) throws APPErrorException {

        for (int i = 0; i < p_EFile.getData().size(); i++) {
            SqlEntity entity = parseSql(i, getColumnName(p_EFile), p_EFile.getData().get(i));
            if (null != entity) {
                ProcessSql(entity);
            }
        }
        LogHelper.info("解析节点：" + p_EFile.getTag() + "  共：" + datas.size());
    }

    /**
     * 将sql 加入到 存储区
     *
     * @param p_Sql
     */
    protected void ProcessSql(SqlEntity p_Sql) {
        datas.add(p_Sql);
    }

    /**
     * 获取所有列名称
     *
     * @param p_EFile
     * @return
     * @throws APPErrorException
     */
    protected String[] getColumnName(EfileSection p_EFile) throws APPErrorException {
        List<String> list = new ArrayList<String>();
        String[] columns = p_EFile.getColumnName();
        String column = tgtools.util.StringUtil.isNullOrEmpty(table.getDataColumn()) ? "Name" : table.getDataColumn();
        for (int i = 0; i < columns.length; i++) {
            ColumnInfo info = null;
            if ("Name".equals(column)) {
                info = table.getColumnsByName(columns[i]);
            } else if ("Alisa".equals(column)) {
                info = table.getColumnsByAlisa(columns[i]);
            }
            if (null == info) {
                throw new APPErrorException("错误的列名，配置文件名：" + columns[i] + ";配置文件Tag名称：" + table.getTagName());
            }
            list.add(info.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 将每一行数据转换成sql
     *
     * @param p_Index
     * @param p_ColumnName
     * @param p_Data
     * @return
     */
    protected SqlEntity parseSql(int p_Index, String[] p_ColumnName, String[] p_Data) {
        if (null != p_ColumnName && null != p_Data && p_ColumnName.length > 0 && p_Data.length > 0 && p_ColumnName.length == p_Data.length) {
            try {
                String sql = SqlHelper.buildInsert(p_Data, table);
                String upsql = SqlHelper.buildUpdate(p_Data, table);
                if (!StringUtil.isNullOrEmpty(sql) && !StringUtil.isNullOrEmpty(upsql)) {
                    MySqlEntity entity = new MySqlEntity();
                    entity.setInsertSql(sql);
                    entity.setUpdateSql(upsql);
                    entity.setIndex(p_Index + 1);
                    entity.setHasDataSql(getHasDataSql(entity.getUpdateSql(), table.getTableName()));
                    return entity;
                }

            } catch (APPErrorException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 检查数据是否存在
     *
     * @param p_UpdateSQL
     * @param p_TableName
     * @return
     */
    protected String getHasDataSql(String p_UpdateSQL, String p_TableName) {
        String sql = "select * from ${tablename} ${where}";
        String where = p_UpdateSQL.substring(p_UpdateSQL.indexOf("where"));
        sql = StringUtil.replace(sql, "${tablename}", p_TableName);
        sql = StringUtil.replace(sql, "${where}", where);
        return sql;
    }

}
