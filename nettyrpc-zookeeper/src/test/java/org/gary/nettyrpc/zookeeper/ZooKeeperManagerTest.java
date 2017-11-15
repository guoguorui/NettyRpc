package org.gary.nettyrpc.zookeeper;

import org.junit.Test;

public class ZooKeeperManagerTest {

	@Test
	public void testCreateNode() {
	    ZooKeeperManager manager = new ZooKeeperManager("127.0.0.1:2181");
	    manager.connect();
	    manager.createNode("zkFromEclipse");
	}
	 	
    @Test
    public void testDeleteNode() {
        ZooKeeperManager manager = new ZooKeeperManager("127.0.0.1:2181");
        manager.connect();
        manager.deleteNode(ZooKeeperManager.ZK_REGISTRY_PATH);
        System.out.println("delete node done");
    }
	
}
