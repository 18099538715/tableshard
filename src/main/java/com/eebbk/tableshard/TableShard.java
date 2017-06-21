package com.eebbk.tableshard;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

public class TableShard {
	private Logger log = Logger.getLogger(this.getClass());
	private String dbPrefix;// 数据源前缀
	private String tablePrefix;// 表名前缀
	private Integer dbRegion;// db分片区间
	private Integer shard;// 数据表分片个数

	public Integer getShard() {
		return shard;
	}

	public void setShard(Integer shard) {
		this.shard = shard;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public Integer getDbRegion() {
		return dbRegion;
	}

	public void setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public void setDbRegion(Integer dbRegion) {
		this.dbRegion = dbRegion;
	}

	/**
	 * 方法执行之前进行分库分表的设置
	 * @description
	 * @author liupengfei
	 * @date 2016年12月27日 上午10:16:47
	 * @param jionpoint
	 */

	public void beforeAdvice(JoinPoint jionpoint) {
		try {
			Object[] args = jionpoint.getArgs();
			if (args[0] instanceof Long || args[0] instanceof Integer || args[0] instanceof String) {
				setParam(args[0]);
			} else {
				Object object = ((ShardId) args[0]).getShardId();
				setParam(object);
			}
		} catch (Exception e) {
			log.error("分库分表出错", e);
		}
	}

	public void afterAdvice() {
		DatabaseContextHolder.clearShardParam();
	}

	private void setParam(Object object) {
		Long args = null;
		if (object instanceof Long || object instanceof Integer) {
			args = (Long) object;
		}
		if (object instanceof String) {
			args = (long) hashString((String) object);
		}
		// 如果不分库
		if (dbPrefix == null || dbPrefix == "") {
			DatabaseContextHolder.setTableName(this.tablePrefix + ((Long) args) % shard);
			return;
		}
		if (tablePrefix == null || tablePrefix == "") {// 如果不分表
			DatabaseContextHolder.setDbName(this.dbPrefix + ((Long) args) / dbRegion);
			return;
		}
		DatabaseContextHolder.setShardParam(this.tablePrefix + ((Long) args) % shard, this.dbPrefix + ((Long) args) / dbRegion);
	}

	/**
	 * @description
	 * @author liupengfei 默认的string类型的hash方法
	 * @date 2017年6月21日 下午3:41:23
	 * @param s
	 * @return
	 */
	public Integer hashString(String s) {
		return s.hashCode();
	}
}
