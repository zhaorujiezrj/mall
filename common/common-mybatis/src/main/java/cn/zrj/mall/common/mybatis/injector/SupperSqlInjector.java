package cn.zrj.mall.common.mybatis.injector;

import cn.zrj.mall.common.mybatis.injector.method.InsertIgnore;
import cn.zrj.mall.common.mybatis.injector.method.InsertIgnoreBatch;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @description: 自定义SQL注入器
 * @author: zhaorujie
 * @date: 2023/7/5
 * @version: v1.0
 **/
public class SupperSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertIgnore());
        methodList.add(new InsertIgnoreBatch());
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        return methodList;
    }
}
