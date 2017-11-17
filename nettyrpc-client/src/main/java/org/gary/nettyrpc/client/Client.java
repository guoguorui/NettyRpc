package org.gary.nettyrpc.client;

import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class Client {
	public static <T> T getImpl(Class<T> clazz) {
		ServiceDiscover sd=new ServiceDiscover();
		String serverAddress=sd.discover(clazz.getSimpleName());
		ProxyFactory pf=new ProxyFactory(new RpcClient(serverAddress));
		T t=pf.getImplObj(clazz);
		sd.closeZk();
		return t;
	}
}
