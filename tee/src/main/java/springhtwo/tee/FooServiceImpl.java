package springhtwo.tee;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FooServiceImpl implements FooService{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    //@Autowired
    //private FooService fooService;
    @Override
    @Transactional
    public void insertRecord(){
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('aaa1')");
    }

    @Override
    @Transactional(rollbackFor = UnexpectedRollbackException.class)
    public void insertThenRollback() throws UnexpectedRollbackException{
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('bbb1')");
        throw new UnexpectedRollbackException("sss");
    }

    @Override
    public void invokeInsertThenRollback() throws UnexpectedRollbackException{
        //insertThenRollback();//没有aop代理增强，没有事务
        ((FooService) (AopContext.currentProxy())).insertThenRollback();//当前类的代理对象
        //fooService.insertThenRollback();//注入实例
    }
}
