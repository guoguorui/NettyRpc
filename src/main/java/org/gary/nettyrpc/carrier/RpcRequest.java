package org.gary.nettyrpc.carrier;

import java.io.Serializable;

public class RpcRequest implements Serializable {

	private static final long serialVersionUID = 433376551561784065L;
	private String message;
	private String methodName;
	private Class<?> interfaceClass;
	private Object[] args;

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
