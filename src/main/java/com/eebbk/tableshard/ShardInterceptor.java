package com.eebbk.tableshard;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @项目名称：tableshard
 * @类名称：ShardInterceptor @类描述：在sql语句执行之前对分表的表名进行替换
 * @创建人：liupengfei
 * @创建时间：2017年6月21日 下午3:22:51
 * @company:步步高教育电子有限公司
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class ShardInterceptor implements Interceptor {
	/**
	 * 默认ObjectFactory
	 */
	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	/**
	 * 默认ObjectWrapperFactory
	 */
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	/**
	 * 默认ReflectorFactory
	 */
	private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
		doSplitTable(metaStatementHandler);
		// 传递给下一个拦截器处理
		return invocation.proceed();

	}

	public Object plugin(Object target) {
		// 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties properties) {

	}

	private void doSplitTable(MetaObject metaStatementHandler) throws ClassNotFoundException {
		String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
		ShardParam shardParam = DatabaseContextHolder.getShardParam();
		if (originalSql != null && !originalSql.equals("") && shardParam != null) {
			metaStatementHandler.setValue("delegate.boundSql.sql", originalSql.replace("table_name", shardParam.getTableName()));
		}
	}
}