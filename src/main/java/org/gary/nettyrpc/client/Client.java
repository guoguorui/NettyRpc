package org.gary.nettyrpc.client;

import org.gary.nettyrpc.zookeeper.ServiceDiscover;

import java.util.HashMap;

public class Client {

    //static HashMap<Object,String> map=new HashMap<>();

	public static <T> T getImpl(Class<T> clazz) {
		ServiceDiscover sd = new ServiceDiscover();
		String serverAddress = sd.discover(clazz.getSimpleName());
		ProxyFactory pf = new ProxyFactory(new RpcClient(),serverAddress);
		T t = pf.getImplObj(clazz);
		///map.put(t,serverAddress);
		sd.closeZk();
		return t;
	}
}
