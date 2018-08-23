package tgtools.tasklibrary.entity;


import tgtools.util.StringUtil;

public class SqlEntity {
	private String insertSql;
	private String updateSql;
	private int index;
	private String filename;
	private String error;

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String sql) {
		this.insertSql = sql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getError() {
		return error;
	}

	public void setError(String result) {
		this.error = result;
	}

	@Override
	public String toString() {
		if (StringUtil.isNotEmpty(this.error)) {
			return "索引：" + this.index + "错误：" + this.error;
		} else {
			return "索引：" + this.index + ";sql:OK";
		}

	}

}
