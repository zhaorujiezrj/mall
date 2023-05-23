package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统配置表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_config")
public class TSysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置KEY
     */
    private String configKey;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 描述信息
     */
    private String configDesc;

    /**
     * 分组key
     */
    private String groupKey;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 配置内容项
     */
    private String configVal;

    /**
     * 类型: text-输入框, textarea-多行文本, uploadImg-上传图片, switch-开关
     */
    private String type;

    /**
     * 显示顺序
     */
    private Long sortNum;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
