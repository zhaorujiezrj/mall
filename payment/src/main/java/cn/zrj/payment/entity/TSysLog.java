package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统操作日志表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_log")
public class TSysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "sys_log_id", type = IdType.AUTO)
    private Integer sysLogId;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户IP
     */
    private String userIp;

    /**
     * 所属系统： MGR-运营平台, MCH-商户中心
     */
    private String sysType;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法描述
     */
    private String methodRemark;

    /**
     * 请求地址
     */
    private String reqUrl;

    /**
     * 操作请求参数
     */
    private String optReqParam;

    /**
     * 操作响应结果
     */
    private String optResInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
