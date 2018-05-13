package org.gary.nettyrpc.zookeeper;

public class ServiceRegister {

	private static ZooKeeperManager zm = new ZooKeeperManager("127.0.0.1:2181");

	public static void register(String serviceName, String address) {
		zm.connect();
		zm.createPersistentNode("/origin/" + serviceName);
		zm.createEphemeralNode("/origin/" + serviceName + "/" + address);
		System.out.println("创建" + "/origin/" + serviceName + "/" + address + "成功");
	}

}
