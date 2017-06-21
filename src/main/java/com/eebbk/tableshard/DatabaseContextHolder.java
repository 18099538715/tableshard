package com.eebbk.tableshard;

/**
 * @项目名称：tableshard
 * @类名称：DatabaseContextHolder
 * @类描述：线程变量，保存当前需要访问的分库的库和表的名称
 * @创建人：liupengfei
 * @创建时间：2017年6月20日 上午9:18:10
 * @company:步步高教育电子有限公司
 */
public class DatabaseContextHolder {

	private static final ThreadLocal<ShardParam> contextHolder = new ThreadLocal<ShardParam>();

	public static void setShardParam(String tableName, String databaseName) {
		contextHolder.set(new ShardParam(tableName, databaseName));
	}

	public static void setTableName(String tableName) {
		ShardParam shardParam = new ShardParam();
		shardParam.setTableName(tableName);
		contextHolder.set(shardParam);
	}

	public static void setDbName(String db) {
		ShardParam shardParam = new ShardParam();
		shardParam.setDatabaseName(db);
		contextHolder.set(shardParam);
	}

	public static ShardParam getShardParam() {
		return contextHolder.get();
	}

	public static void clearShardParam() {
		contextHolder.remove();
	}

}
