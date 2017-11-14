package org.gary.nettyrpc.client;

import org.gary.nettyrpc.service.HelloService;

public class ClientTest {
	public static void main(String[] args) {
		FacadeClientFactory fcf=new FacadeClientFactory(new RpcClient());
		HelloService helloService=fcf.getFacadeClient(HelloService.class);
		System.out.println(helloService.hello("nico from ClientTest"));
	}
}
