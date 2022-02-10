package me.muphy.spring.common;

import java.util.List;

/**
 * Auto-generated: 2021-08-19 10:0:43
 *
 * @author ruphy
 */
public class HttpResponse<T> {

    private int code;
    private String msg;
    private List<T> data;
    private boolean ok;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean getOk() {
        return ok;
    }

}