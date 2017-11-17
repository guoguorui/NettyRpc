package org.gary.nettyrpc.client;

import org.gary.nettyrpc.service.HelloService;

public class ClientTest {
	public static void main(String[] args) {
		HelloService helloService=Client.getImpl(HelloService.class);
		if(helloService!=null) {
			System.out.println(helloService.hello());
		}
	}
}
