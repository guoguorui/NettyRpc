package org.gary.nettyrpc.zookeeper;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ZooKeeperManagerTest {
	
	private static final Logger LOGGER = Logger.getLogger("ZooKeeperManagerTest");

	@Test
	public void testCreateNode() throws InterruptedException {
	    ZooKeeperManager manager = new ZooKeeperManager("127.0.0.1:2181");
	    manager.connect();
	    manager.createEphemeralNode("/hello");
	    LOGGER.info("manager create node zkFromEclipse");
	    manager.deleteNode("/hello");
	    LOGGER.info("manager delete all the nodes");
	    manager.closeConnect();
	}
	 		
}
