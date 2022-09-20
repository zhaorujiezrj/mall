package cn.zrj.mall.common.mybatis.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
public class PageUtils {

    private PageUtils() {

    }

    public static <R, V> Page<V> convertPageToVo(IPage<R> source, Class<V> clazz) {
        List<R> records = source.getRecords();
        Page<V> page = new Page<>();
        List<V> copyToList = BeanUtil.copyToList(records, clazz);
        BeanUtil.copyProperties(source, page);
        page.setRecords(copyToList);
        return page;
    }
}
