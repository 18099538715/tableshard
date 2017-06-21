package com.eebbk.tableshard;

/**
 * @项目名称：tableshard
 * @类名称：ShardParam
 * @类描述：保存分库分表的库名称和表名称的实体类
 * @创建人：Administrator
 * @创建时间：2017年6月21日 下午3:27:42
 * @company:步步高教育电子有限公司
 */
public class ShardParam {
	private String tableName;
	private String databaseName;

	public ShardParam(String tableName, String databaseName) {
		super();
		this.tableName = tableName;
		this.databaseName = databaseName;
	}

	public ShardParam() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}