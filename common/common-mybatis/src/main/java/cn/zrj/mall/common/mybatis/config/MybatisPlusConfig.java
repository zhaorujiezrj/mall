package cn.zrj.mall.common.mybatis.config;


import cn.zrj.mall.common.mybatis.handler.MyMetaObjectHandler;
import cn.zrj.mall.common.mybatis.injector.SupperSqlInjector;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充数据库创建人、创建时间、更新人、更新时间
     * @return
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        return globalConfig;
    }

    /**
     * 自定义SQL注入器，扩展mybatis-plus提供的SQL实现
     * @return
     */
    @Bean
    public SupperSqlInjector sqlInjector() {
        return new SupperSqlInjector();
    }
}
