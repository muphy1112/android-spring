package me.muphy.spring.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import me.muphy.spring.util.StringUtils;

/**
 * 流程处理
 *
 * @className: FlowRessageMessageDto
 * @author: 若非
 * @date: 2021-05-28 00:01
 */
public class Result<T> {
    @JSONField(serialize = false)
    private static String OK = "成功";
    @JSONField(serialize = false)
    private static String ERR = "未知错误";
    private boolean success;
    private String info;
    private String msg;
    private T data;

    public Result() {
    }

    public static Result ok() {
        Result dto = new Result();
        dto.success = true;
        return dto;
    }

    public static <K> Result ok(K data) {
        Result<K> dto = new Result();
        dto.success = true;
        dto.data = data;
        return dto;
    }

    public static Result success(boolean success) {
        Result dto = new Result();
        dto.success = success;
        return dto;
    }

    public static Result info(String info) {
        Result dto = new Result();
        dto.success = true;
        dto.info = info;
        return dto;
    }

    public static <K> Result info(String info, K data) {
        Result dto = new Result<K>();
        dto.success = true;
        dto.data = data;
        dto.info = info;
        return dto;
    }

    public static Result error(String msg) {
        Result dto = new Result();
        dto.success = false;
        dto.msg = msg;
        return dto;
    }

    public static Result error() {
        return success(false);
    }

    public boolean hasInfo() {
        return !StringUtils.isEmpty(info);
    }

    public Result addInfo(String info) {
        if (StringUtils.isEmpty(info)) {
            return this;
        }
        if (StringUtils.isEmpty(this.info)) {
            this.info = info;
        } else {
            this.info += "\n" + info;
        }
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMsg() {
        if (StringUtils.isEmptyOrWhiteSpace(msg)) {
            return success ? OK : ERR;
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result addMsg(String msg) {
        if (StringUtils.isEmpty(msg) || OK.equals(msg) || ERR.equals(msg)) {
            return this;
        }
        if (StringUtils.isEmpty(this.msg)) {
            this.msg = msg;
        } else {
            this.msg += "\n" + msg;
        }
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
