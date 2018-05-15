package org.gary.nettyrpc.carrier;

import java.io.Serializable;

public class RpcResponse implements Serializable {

    private Object result;
    private int id;
    //0代表正常回应，-1代表发生异常或连接中断
    private int status;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
