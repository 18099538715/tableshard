package com.eebbk.tableshard;

/**
 * @项目名称：DbRegionTableNum
 * @类名称：DbRegionTableNum
 * @类描述：db分库个数固定，即对dbShardParam取余，table区间， 即除以传入tableShardParam得到的整数
 * @创建人：Administrator
 * @创建时间：2017年6月21日 下午5:34:33
 * @company:步步高教育电子有限公司
 */
public class DbNumFixTableRegion extends AbstractTableDbShard {

	public void setParam(Object object) {
		Long args = null;
		if (object instanceof Long || object instanceof Integer) {
			args = (Long) object;
		}
		if (object instanceof String) {
			args = (long) hashString((String) object);
		}
		// 如果不分库
		if (getDbPrefix() == null || getDbPrefix() == "") {
			DatabaseContextHolder.setTableName(getTablePrefix() + ((Long) args) / getTableShardParam());
			return;
		}
		if (getTablePrefix() == null || getTablePrefix() == "") {// 如果不分表
			DatabaseContextHolder.setDbName(this.getDbPrefix() + ((Long) args) % getDbShardParam());
			return;
		}
		DatabaseContextHolder.setShardParam(this.getTablePrefix() + ((Long) args) / getTableShardParam(), getDbPrefix() + ((Long) args) % getDbShardParam());
	}
}
