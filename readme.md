 编译打包
 mvn clean package

使用
    
     1、打包得到的jar 包引入到项目
     
     2、引入自定义的mybatis拦截器，配置动态数据源，如果不需要分库，则使用正常数据源即可。
     ``` 
     <!-- 配置SqlSessionFactory -->
		<bean id="sqlMessageSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
			<property name="configLocation" value="classpath:mybatis-config.xml"></property>
			<property name="dataSource" ref="dynamicDataSource"></property>
			<property name="plugins">
				<array>
					<bean id="tableSegInterceptor" class="com.eebbk.tableshard.ShardInterceptor" />
				</array>
			</property>
		</bean>


     <bean id="dynamicDataSource" class="com.eebbk.tableshard.DynamicDataSource">
		<property name="targetDataSources">
			<map key-type="java.lang.String">
				<entry key="ds0">
					<bean class="com.zaxxer.hikari.HikariDataSource"
						destroy-method="close">
					</bean>
				</entry>
                <entry key="ds0">
					<bean class="com.zaxxer.hikari.HikariDataSource"
						destroy-method="close">
					</bean>
				</entry>
          </map>
      </property>
    </bean>
    ``` 

     3、共有四种分库分表的策略，如果只需要分库或分表，则另外一种可以随意选，
		根据不同的需求选择继承不同的类。如果不需要分库或者分表，请忽略对应信息的赋值。
		区间分是除以传入的对应参数得到整数，个数固定分是对传入的参数取余
		实现before和after不需要进行处理，只需调用父类的
	
		DbNumFixTableNumFix  db数量固定，表数量固定
		DbNumFixTableRegion  db数量固定，表按区间分
		DbRegionTableNumFix  db按区间分，表数量固定
		DbRegionTableRegion  db按区间分，表按区间分

                ``` 
		@Component
		@Aspect
		public class OldMessageShard extends TableShard {
			@PostConstruct
			public void init() {
				super.setDbPrefix("ds");//不分库不需要
				super.setDbShardParam(5000);//不分库不需要
				super.setTableShardParam(3);//不分表不需要
				super.setTablePrefix("account_message_");//不分表不需要
			}
		
			@Before(value = "execution(* com.bbk.im.logic.shard.oldmsg.dao.MessageDAO.*(..))")
			public void beforeAdvice(JoinPoint arg0) {
				super.beforeAdvice(arg0);
			}
		
			@After(value = "execution(* com.bbk.im.logic.shard.oldmsg.dao.MessageDAO.*(..))")
			public void afterAdvice() {
				super.afterAdvice();
			}
		
			@Override
			public int hashString(String s) {
				// TODO Auto-generated method stub
				return super.hashString(s);
			}
		}
		``` 
 

     4、实体类实现ShardId接口，返回分库分表的字段
     ```
     public class Message implements Serializable, ShardId {
		 	@Override
			public Object getShardId() {
				return msgId;
			}
			
     ``` 
     
     5、在对应的数据库操作.xml里将对应的表名称均修改为 table_name，例如
     ``` 
     <select id="getMaxSyncKeyByDialogId" resultType="java.lang.Long">
		select
		max(syncKey) from
		table_name where dialogId =#{0} ;
	</select>
	``` 
    6、为确保容错性，防止用户传入分库分表的参数 为null的时候异常影响用户的正常处理流程，为了分库分表之后不改变之前的逻辑，此种情况给用户返回空的查询      结果，需建立对应的以分库分表前缀为名称的库和表名。
     
          
