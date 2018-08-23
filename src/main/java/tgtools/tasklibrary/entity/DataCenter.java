package tgtools.tasklibrary.entity;

import java.util.concurrent.ConcurrentLinkedQueue;


public class DataCenter {

	public DataCenter()
	{
		m_Data = new ConcurrentLinkedQueue<SqlEntity>();
	}
	private  ConcurrentLinkedQueue<SqlEntity> m_Data;

	private  synchronized ConcurrentLinkedQueue<SqlEntity> getDatas() {
		if (null == m_Data) {
			m_Data = new ConcurrentLinkedQueue<SqlEntity>();
		}
		return m_Data;
	}
	public int size()
	{
		return m_Data.size();
	}
	public  SqlEntity get() {
		if(!getDatas().isEmpty())
		return getDatas().poll();
		
		return null;
	}
	
	public  void add(SqlEntity p_Sql) {
		getDatas().add(p_Sql);
	}
	
	public  boolean isEmpty(){
		return getDatas().isEmpty();
	}
}
