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
 */
public class StepSqlProcessTask extends Task {

	public StepSqlProcessTask(DataCenter p_Datas)
	{
		m_Datas=p_Datas;
	}
	protected DataCenter m_Datas;
	@Override
	protected boolean canCancel() {
		return false;
	}
	public int getPorcessCount(){return m_index;}
	protected DMDataAccess dmDataAccess ;

	@Override
	public void run(TaskContext p_Param) {
		m_index=0;
		if(p_Param.containsKey("config"))
		{
			Object obj =p_Param.get("config");
			if(null!=obj  && obj instanceof ConfigInfo){
			ConfigInfo config =(ConfigInfo)obj;
			dmDataAccess= new DMDataAccess(config.getDataSource());
			}
		}
		
		if(null==dmDataAccess)
		{
			LogHelper.error("StepDataProcessSqlTask 解析失败",new Exception("数据库访问参数失败"));
			return ;
		}
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("数据库处理：" + e);
		}
		while (true) {
			SqlEntity entity = m_Datas.get();
			if(null==entity)
			{
				return ;
			}
			try {
				oneSqlExt(entity,p_Param);
				m_index++;
				// bacthSql(entity);
			} catch (Exception e) {
				System.out.println("数据库处理：" + entity.toString());
			}
		}
	}
	protected  void oneSqlExt(SqlEntity entity , TaskContext p_Param)
	{
		try {
		String hasdatasql=((MySqlEntity)entity).getHasDataSql();
		DataTable dt= dmDataAccess.Query(hasdatasql);
		if(DataTable.hasData(dt))
		{
			int res =-1;
			try {
				res = dmDataAccess.executeUpdate(entity.getUpdateSql());
				if(res<1)
				{throw new APPErrorException("更新数据失败，影响数据0行");}
			}catch (Exception ex)
			{
				LogHelper.error("更新数据出错；UpdateSql:"+entity.getUpdateSql()+";Update结果："+res , ex);
				p_Param.put("error", "false");
			}
		}
		else
		{
			try {
				dmDataAccess.executeUpdate(entity.getInsertSql());
			} catch (Exception ex) {
				LogHelper.error("添加数据出错；InsertSql:"+entity.getInsertSql() , ex);
				p_Param.put("error", "false");
			}
		}

		} catch (Exception e) {
			LogHelper.error("数据查询失败；hasdatasql:"+((MySqlEntity)entity).getHasDataSql() , e);
			p_Param.put("error", "false");

		}

	}


	int m_index=0;
	protected void oneSql(SqlEntity entity , TaskContext p_Param) {
		try {

			int res=dmDataAccess.executeUpdate(entity.getUpdateSql());
			if(res<1)
			{
				
				try {
					dmDataAccess.executeUpdate(entity.getInsertSql());
				} catch (Exception ex) {
					LogHelper.error("数据库处理Insert错误,InsertSQL:"+entity.getInsertSql()+"UpdateSql:"+entity.getUpdateSql()+";Update结果："+res , ex);
					p_Param.put("error", "false");
				}
			}
		} catch (Exception e) {
					LogHelper.error("数据库处理Update错误" , e);
					p_Param.put("error", "false");
		}


	}


}
