package cn.zrj.mall.common.core.util;

import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 业务订单生成规则
 * @author zhaorujie
 * @date 2022/9/2
 */
@Component
public class BusinessNoUtils {

    private static StringRedisTemplate redisTemplate;

    public BusinessNoUtils(StringRedisTemplate redisTemplate) {
        BusinessNoUtils.redisTemplate = redisTemplate;
    }

    /**
     * 按照制定的规则生成业务单号
     * 1~3位为业务类型
     * 4~12位为时间格式为年月日
     * 13~n为自增序号
     * @param businessTypeEnum 业务类型枚举
     * @param digit 业务需要位数
     * @return
     */
    public static String generate(BusinessTypeEnum businessTypeEnum, Integer digit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = LocalDate.now(ZoneOffset.of("+8")).format(formatter);
        String key = RedisConstants.BUSINESS_NO + businessTypeEnum.getValue() + date;

        Long increment = redisTemplate.opsForValue().increment(key);
        return businessTypeEnum.getValue() + date + String.format("%0" + digit + "d", increment);
    }

    public static String generate(BusinessTypeEnum businessTypeEnum) {
        return generate(businessTypeEnum, 6);
    }
}
