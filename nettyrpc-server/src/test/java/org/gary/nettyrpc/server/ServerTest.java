package org.gary.nettyrpc.server;

import org.gary.nettyrpc.zookeeper.ServiceRegister;

public class ServerTest{ 
	
	public static void main(String[] args) {
		ServiceRegister sr=new ServiceRegister();
		sr.register("HelloService", "127.0.0.1:8888");
		RpcServer.processRequest();
	}
}
