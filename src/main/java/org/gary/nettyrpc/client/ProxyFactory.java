package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory implements InvocationHandler {

	RpcClient rpcClient;

	public ProxyFactory(RpcClient rpcClient) {
		this.rpcClient = rpcClient;
	}

	public <T> T getImplObj(Class<T> clazz) {
		this.rpcClient.setServiceClass(clazz);
		@SuppressWarnings("unchecked")
		T implObj = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
		return implObj;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RpcResponse rpcResponse = (RpcResponse) rpcClient.call(method, args);
		return rpcResponse.getResult();
	}
}
