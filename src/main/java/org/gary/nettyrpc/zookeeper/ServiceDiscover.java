package org.gary.nettyrpc.zookeeper;

import java.util.List;
import java.util.Random;

public class ServiceDiscover {

    private ZKManager zm;

    public ServiceDiscover(String zkAddress){
        zm=new ZKManager(zkAddress);
    }

	public String discover(String serviceName) {
		zm.connect();
		List<String> services = zm.listChildren(ZKManager.ZK_REGISTRY_PATH);
		String address=null;
		for (String service : services) {
			if (service.equals(serviceName)) {
				List<String> addresses = zm.listChildren(ZKManager.ZK_REGISTRY_PATH+"/" + serviceName);
				Random ran = new Random();
				int index = ran.nextInt(addresses.size());
				address=addresses.get(index);
			}
		}
        zm.closeConnect();
		return address;
	}
}
