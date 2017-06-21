#编译打包
 mvn clean package

#使用
    
     1、打包得到的jar 包引入到项目
     
     2、引入自定义的mybatis拦截器，配置动态数据源，如果不需要分库，则使用正常数据源即可。
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

     3、继承类TableShard，初始化的时候设置好分库的区间，分表的个数，分库采用id/region ，分表采用  id%shard
        如果不需要分库或者分表，请忽略对应信息的赋值。实现before和after不需要进行处理，只需调用父类的
        每一个表一个划分类，不支持多个共用一个

        @Component
		@Aspect
		public class OldMessageShard extends TableShard {
			@PostConstruct
			public void init() {
				super.setDbPrefix("ds");
				super.setDbRegion(5000);
				super.setShard(3);
				super.setTablePrefix("message_");
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
 

     4、实体类实现ShardId接口，返回分库分表的字段
     public class Message implements Serializable, ShardId {
		 	@Override
			public Object getShardId() {
				return msgId;
			}
     }
          