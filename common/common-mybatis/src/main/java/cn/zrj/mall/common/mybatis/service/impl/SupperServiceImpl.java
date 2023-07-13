package cn.zrj.mall.common.mybatis.service.impl;

import cn.zrj.mall.common.mybatis.mapper.SupperMapper;
import cn.zrj.mall.common.mybatis.service.SupperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @description: CommonServiceImpl
 * @author: zhaorujie
 * @date: 2023/7/5
 * @version: v1.0
 **/
public class SupperServiceImpl<M extends SupperMapper<T>, T> extends ServiceImpl<SupperMapper<T>, T> implements SupperService<T> {

    @Override
    public boolean insertIgnore(T entity) {
        SupperMapper<T> baseMapper1 = this.baseMapper;
        return baseMapper.insertIgnore(entity) > 0;
    }

    @Override
    public boolean insertIgnoreBatch(List<T> entityList) {
        return baseMapper.insertIgnoreBatch(entityList) > 0;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean insertBatchSomeColumn(List<T> entityList) {
        return insertBatchSomeColumn(entityList, 1000);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean insertBatchSomeColumn(List<T> entityList, int batchSize) {
        try {
            int size = entityList.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            //保存单批提交的数据集合
            List<T> oneBatchList = new ArrayList<>();
            for(Iterator<T> var7 = entityList.iterator(); var7.hasNext(); ++i) {
                T element = var7.next();
                oneBatchList.add(element);
                if (i == idxLimit) {
                    baseMapper.insertBatchSomeColumn(oneBatchList);
                    //每次提交后需要清空集合数据
                    oneBatchList.clear();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
            }
        }catch (Exception e){
            log.error("saveBatch fail",e);
            return false;
        }
        return  true;
    }
}
