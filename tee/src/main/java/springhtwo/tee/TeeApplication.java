package springhtwo.tee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.beans.BeanProperty;
import java.sql.Connection;
import java.sql.SQLException;

@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class TeeApplication implements CommandLineRunner{
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private FooService fooservice;
	public static void main(String[] args){
		SpringApplication.run(TeeApplication.class,args);
	}

//	@Override
//	public void run(String... args) throws Exception{
//		log.info("count before transaction {}",getCount());
//		transactionTemplate.execute(new TransactionCallbackWithoutResult(){
//			@Override
//			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus){
//				jdbcTemplate.execute("INSERT INTO FOO (ID,BAR) VALUES (4,'AA1')");
//				log.info("count in transaction {}",getCount());
//				transactionStatus.setRollbackOnly();
//			}
//		});
//
//		log.info("count after transaction {}",getCount());
//	}

	private long getCount(){
		return (long) jdbcTemplate.queryForList("SELECT COUNT(*) AS CNT FROM FOO")
				.get(0).get("CNT");
	}
	@Override
	public void run(String... args) throws Exception{
		fooservice.insertRecord();
		log.info("aaa1 {}",
				jdbcTemplate.queryForList("SELECT COUNT(*) FROM FOO WHERE BAR='aaa1'",long.class));
		try{
			fooservice.insertThenRollback();
		}catch(Exception e) {
			log.info("bbb1 {}",
					jdbcTemplate.queryForList("SELECT COUNT(*) FROM FOO WHERE BAR='bbb1'",long.class));
		}
		try{
			fooservice.invokeInsertThenRollback();
		}catch(Exception e) {
			log.info("bbb1 {}",
					jdbcTemplate.queryForList("SELECT COUNT(*) FROM FOO WHERE BAR='bbb1'",long.class));
		}

	}
}
