package cn.zrj.mall.common.core.exception;

import cn.zrj.mall.common.core.result.ResultCode;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Data
public class BusinessException extends RuntimeException{

    private ResultCode resultCode;

    private String code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }
}
