package org.gary.nettyrpc.carrier;

import java.io.Serializable;

public class RpcResponse implements Serializable{

	private Object result;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
