package org.gary.nettyrpc.client;

import org.gary.nettyrpc.service.HelloService;
import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class ClientTest {
	public static void main(String[] args) {
		ServiceDiscover sd=new ServiceDiscover();
		FacadeClientFactory fcf=new FacadeClientFactory(new RpcClient(sd.discover("HelloService")));
		HelloService helloService=fcf.getFacadeClient(HelloService.class);
		System.out.println(helloService.hello("nico from ClientTest"));
		sd.sweep();
	}
}
