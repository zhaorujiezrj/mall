package cn.zrj.mall.common.core.util;

import cn.hutool.core.util.RandomUtil;
import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import cn.zrj.mall.common.core.enums.OutRefundNoTypeEnum;
import cn.zrj.mall.common.core.enums.OutTradeNoTypeEnum;
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

    /**
     * 1~n位为支付类型的前缀
     * n + 1 ~ n + 16为时间戳
     * n + 17 ~ n + 19 为随机数，占三位
     * n + 20 + n + 24 为用户id，占五位
     * @param outTradeNoTypeEnum 支付类型
     * @param memberId 用户id
     * @return
     */
    public static String generateOutTradeNo(OutTradeNoTypeEnum outTradeNoTypeEnum, Long memberId) {
        // 用户id前补零保证五位，对超出五位的保留后五位
        String userIdFilledZero = String.format("%05d", memberId);
        String fiveDigitsUserId = userIdFilledZero.substring(userIdFilledZero.length() - 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = LocalDate.now(ZoneOffset.of("+8")).format(formatter);
        // 在前面加上wxo（weixin order）等前缀是为了人工可以快速分辨订单号是下单还是退款、来自哪家支付机构等
        // 将时间格式化（yyyyMMddHHmmss）+3位随机数+五位id组成商户订单号，规则参考自<a href="https://tech.meituan.com/2016/11/18/dianping-order-db-sharding.html">大众点评</a>
        return outTradeNoTypeEnum.getValue() + dateTime + RandomUtil.randomNumbers(3) + fiveDigitsUserId ;
    }

    public static String generateOutRefundNo(OutRefundNoTypeEnum outRefundNoTypeEnum, Long memberId) {
        // 用户id前补零保证五位，对超出五位的保留后五位
        String userIdFilledZero = String.format("%05d", memberId);
        String fiveDigitsUserId = userIdFilledZero.substring(userIdFilledZero.length() - 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = LocalDate.now(ZoneOffset.of("+8")).format(formatter);
        // 在前面加上wxo（weixin order）等前缀是为了人工可以快速分辨订单号是下单还是退款、来自哪家支付机构等
        // 将时间格式化（yyyyMMddHHmmss）+3位随机数+五位id组成商户订单号，规则参考自<a href="https://tech.meituan.com/2016/11/18/dianping-order-db-sharding.html">大众点评</a>
        return outRefundNoTypeEnum.getValue() + dateTime + RandomUtil.randomNumbers(3) + fiveDigitsUserId ;
    }
}
