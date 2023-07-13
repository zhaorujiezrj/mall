package cn.zrj.mall.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @description: CommonMapper
 * @author: zhaorujie
 * @date: 2023/7/5
 * @version: v1.0
 **/
public interface SupperMapper<T> extends BaseMapper<T> {

    /**
     * 插入数据，如果中已经存在相同的记录，则忽略当前新数据
     * {@link cn.zrj.mall.common.mybatis.injector.method.InsertIgnore}
     *
     * @param entity 实体类
     * @return 影响条数
     */
    int insertIgnore(T entity);

    /**
     * 批量插入数据，如果中已经存在相同的记录，则忽略当前新数据
     * {@link cn.zrj.mall.common.mybatis.injector.method.InsertIgnoreBatch}
     *
     * @param entityList 实体类列表
     * @return 影响条数
     */
    int insertIgnoreBatch(List<T> entityList);

    /**
     * 全量插入,等价于insert
     * {@link com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn}
     * @param entityList
     * @return
     */
    int insertBatchSomeColumn(List<T> entityList);
}
