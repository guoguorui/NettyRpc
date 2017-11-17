package org.gary.nettyrpc.zookeeper;

import java.util.List;
import java.util.Random;

//将根节点分离
public class ServiceDiscover {
	
	private ZooKeeperManager zm=new ZooKeeperManager("127.0.0.1:2181");
	
	public ZooKeeperManager getZm() {
		return zm;
	}

	public String discover(String serviceName) {
		zm.connect();
		List<String> services = zm.listChildren("/origin");
		for(String service:services) {
			if(service.equals(serviceName)) {
				List<String> addresses=zm.listChildren("/origin/"+serviceName);
				if(addresses.size()>0) {
					Random ran=new Random();
					int index=ran.nextInt(addresses.size());
					String[] addressArray=addresses.toArray(new String[addresses.size()]);
					return addressArray[index];
				}
			}
		}
		return null;
	}
	
	public void closeZk() {
		try {
			zm.getZk().close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
