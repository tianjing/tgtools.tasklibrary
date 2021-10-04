package tgtools.tasklibrary.entity;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author
 */
public class DataCenter {

	public DataCenter()
	{
		data = new ConcurrentLinkedQueue<SqlEntity>();
	}
	private  ConcurrentLinkedQueue<SqlEntity> data;

	private  synchronized ConcurrentLinkedQueue<SqlEntity> getDatas() {
		if (null == data) {
			data = new ConcurrentLinkedQueue<SqlEntity>();
		}
		return data;
	}
	public int size()
	{
		return data.size();
	}
	public  SqlEntity get() {
		if(!getDatas().isEmpty()) {
			return getDatas().poll();
		}
		
		return null;
	}
	
	public  void add(SqlEntity pSql) {
		getDatas().add(pSql);
	}
	
	public  boolean isEmpty(){
		return getDatas().isEmpty();
	}
}
