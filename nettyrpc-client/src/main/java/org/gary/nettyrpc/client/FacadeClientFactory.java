package org.gary.nettyrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.gary.nettyrpc.carrier.RpcResponse;

public class FacadeClientFactory implements InvocationHandler{
	
	RpcClient rpcClient;
	
	public FacadeClientFactory(RpcClient rpcClient) {
		this.rpcClient=rpcClient;
	}
	
	public <T> T getFacadeClient(Class<T> clazz)  {
		this.rpcClient.setServiceClass(clazz);
		//clazz是否还要getClass()需要实验测试,理论是不需要的,不过null也无妨，代表了默认的类加载器
		@SuppressWarnings("unchecked")
		T facadeClient=(T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, this);
		return facadeClient;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RpcResponse rpcResponse=(RpcResponse) rpcClient.call(method,args);
		return rpcResponse.getResult();
	}
}
