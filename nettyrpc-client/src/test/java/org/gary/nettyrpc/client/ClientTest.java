package org.gary.nettyrpc.client;

import org.gary.nettyrpc.service.HelloService;
import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class ClientTest {
	public static void main(String[] args) {
		ServiceDiscover sd=new ServiceDiscover();
		String serverAddress=sd.discover("HelloService");
		if(serverAddress==null) {
			System.out.println("no server available");
			sd.closeZk();
		}
		FacadeClientFactory fcf=new FacadeClientFactory(new RpcClient(serverAddress));
		HelloService helloService=fcf.getFacadeClient(HelloService.class);
		System.out.println(helloService.hello("nico from ClientTest"));
		sd.closeZk();
	}
}
