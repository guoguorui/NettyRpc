package org.gary.nettyrpc.client;

import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class Client {
	public static <T> T getImpl(Class<T> clazz) {
		ServiceDiscover sd=new ServiceDiscover();
		String serverAddress=sd.discover(clazz.getSimpleName());
		if(serverAddress==null) {
			System.out.println("no server available");
			sd.closeZk();
			return null;
		}
		FacadeClientFactory fcf=new FacadeClientFactory(new RpcClient(serverAddress));
		T t=fcf.getFacadeClient(clazz);
		sd.closeZk();
		return t;
	}
}
