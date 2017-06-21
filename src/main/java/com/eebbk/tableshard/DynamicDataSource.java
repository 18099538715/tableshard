package com.eebbk.tableshard;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @项目名称：tableshard
 * @类名称：DynamicDataSource
 * @类描述：自定义动态数据源
 * @创建人：liupengfei
 * @创建时间：2017年6月21日 下午3:20:44
 * @company:步步高教育电子有限公司
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DatabaseContextHolder.getShardParam().getDatabaseName();
	}
}
