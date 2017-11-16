package org.gary.nettyrpc.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//加入日志
public class ZooKeeperManager {

    public static final int ZK_SESSION_TIMEOUT =5000;
    public static final String ZK_REGISTRY_PATH ="/origin";
    public static final Logger LOGGER=Logger.getLogger(ZooKeeperManager.class);
    private String zkAddress;
    private ZooKeeper zk;

	public ZooKeeperManager(String zkAddress) {
        this.zkAddress = zkAddress;
    }
	
	public ZooKeeper getZk() {
		return zk;
	}

    public void connect() {
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(zkAddress, ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    connectedSignal.countDown();
                }
            });
            connectedSignal.await();
            LOGGER.info("Successfully connected to " + zkAddress);
        } catch (IOException | InterruptedException e) {
        	LOGGER.error("Failed to connect to " + zkAddress);
            e.printStackTrace();
        }
    }
    
    public void createPersistentNode(String nodePath) {
    	try {
			zk.create(nodePath, null,
			        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void createEphemeralNode(String nodePath) {
    	try {
			zk.create(nodePath, null,
			        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    //此方法要求nodePath是完整的,所以要以/root开始
    public List<String> listChildren(String nodePath) {
        List<String> result = new ArrayList<String>();
        try {
        	//先不监听
            List<String> children = zk.getChildren(nodePath, false);
            for (String c : children) {
                result.add(c);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    //删除本节点和其直接子节点,稍后添加彻底遍历版本
    public void deleteNode(String nodePath) {
        try {
            List<String> children = zk.getChildren(nodePath, true);
            for (String c : children) {
                zk.delete(nodePath + "/" + c, -1);
            }
            zk.delete(nodePath, -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnect() {
    	try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
