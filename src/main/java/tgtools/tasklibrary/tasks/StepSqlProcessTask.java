package tgtools.tasklibrary.tasks;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.config.ConfigInfo;
import tgtools.tasklibrary.entity.DataCenter;
import tgtools.tasklibrary.entity.MySqlEntity;
import tgtools.tasklibrary.entity.SqlEntity;
import tgtools.tasklibrary.util.DMDataAccess;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;

/**
 * DataCenter sql 处理
 *
 * @author tianjing
 */
public class StepSqlProcessTask extends Task {

    protected DataCenter datas;
    protected DMDataAccess dmDataAccess;
    protected int index = 0;

    public StepSqlProcessTask(DataCenter pDatas) {
        datas = pDatas;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    public int getProcessCount() {
        return index;
    }

    @Override
    public void run(TaskContext pParam) {
        index = 0;
        if (pParam.containsKey("config")) {
            Object obj = pParam.get("config");
            if (null != obj && obj instanceof ConfigInfo) {
                ConfigInfo config = (ConfigInfo) obj;
                dmDataAccess = new DMDataAccess(config.getDataSource());
            }
        }

        if (null == dmDataAccess) {
            LogHelper.error("StepDataProcessSqlTask 解析失败", new Exception("数据库访问参数失败"));
            return;
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("数据库处理：" + e);
        }
        while (true) {
            SqlEntity entity = datas.get();
            if (null == entity) {
                return;
            }
            try {
                oneSqlExt(entity, pParam);
                index++;
                // bacthSql(entity);
            } catch (Exception e) {
                System.out.println("数据库处理：" + entity.toString());
            }
        }
    }

    protected void oneSqlExt(SqlEntity entity, TaskContext pParam) {
        try {
            String hasdatasql = ((MySqlEntity) entity).getHasDataSql();
            DataTable dt = dmDataAccess.Query(hasdatasql);
            if (DataTable.hasData(dt)) {
                int res = -1;
                try {
                    res = dmDataAccess.executeUpdate(entity.getUpdateSql());
                    if (res < 1) {
                        throw new APPErrorException("更新数据失败，影响数据0行");
                    }
                } catch (Exception ex) {
                    LogHelper.error("更新数据出错；UpdateSql:" + entity.getUpdateSql() + ";Update结果：" + res, ex);
                    pParam.put("error", "false");
                }
            } else {
                try {
                    dmDataAccess.executeUpdate(entity.getInsertSql());
                } catch (Exception ex) {
                    LogHelper.error("添加数据出错；InsertSql:" + entity.getInsertSql(), ex);
                    pParam.put("error", "false");
                }
            }

        } catch (Exception e) {
            LogHelper.error("数据查询失败；hasdatasql:" + ((MySqlEntity) entity).getHasDataSql(), e);
            pParam.put("error", "false");

        }

    }

    protected void oneSql(SqlEntity entity, TaskContext pParam) {
        try {

            int res = dmDataAccess.executeUpdate(entity.getUpdateSql());
            if (res < 1) {

                try {
                    dmDataAccess.executeUpdate(entity.getInsertSql());
                } catch (Exception ex) {
                    LogHelper.error("数据库处理Insert错误,InsertSQL:" + entity.getInsertSql() + "UpdateSql:" + entity.getUpdateSql() + ";Update结果：" + res, ex);
                    pParam.put("error", "false");
                }
            }
        } catch (Exception e) {
            LogHelper.error("数据库处理Update错误", e);
            pParam.put("error", "false");
        }


    }


}
