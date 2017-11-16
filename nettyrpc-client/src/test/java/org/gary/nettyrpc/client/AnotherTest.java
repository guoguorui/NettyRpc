
package org.gary.nettyrpc.client;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.gary.nettyrpc.zookeeper.ZooKeeperManager;

public class AnotherTest {
	public static void main(String[] args) {
		ZooKeeperManager zm=new ZooKeeperManager("127.0.0.1:2181");
		zm.connect();
		ZooKeeper zk=zm.getZk();
		try {
			Stat stat=zk.exists("/origin/HelloService", false);
			if(stat!=null) {
				System.out.println("/origin/HelloService exists");
				List<String> sons=zk.getChildren("/origin/HelloService", false);
				for(String son:sons) {
					System.out.println(son);
				}
			}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
