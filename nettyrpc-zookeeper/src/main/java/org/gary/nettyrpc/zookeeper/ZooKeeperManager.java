package org.gary.nettyrpc.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//加入日志
public class ZooKeeperManager implements Watcher{

    public static final int ZK_SESSION_TIMEOUT =5000;
    public static final String ZK_REGISTRY_PATH ="/origin";
    public static final Logger LOGGER=Logger.getLogger(ZooKeeperManager.class);
    private String zkAddress;
    private ZooKeeper zk;
    final CountDownLatch connectedSignal = new CountDownLatch(1);
    final CountDownLatch availableSignal = new CountDownLatch(1);
    private boolean waitForavailable=false;
    
	public ZooKeeperManager(String zkAddress) {
        this.zkAddress = zkAddress;
    }
	
	public ZooKeeper getZk() {
		return zk;
	}

    public void connect() {
        try {
            zk = new ZooKeeper(zkAddress, ZK_SESSION_TIMEOUT, this);
            connectedSignal.await();
            LOGGER.info("Successfully connected to " + zkAddress);
        } catch (IOException | InterruptedException e) {
        	LOGGER.error("Failed to connect to " + zkAddress);
            e.printStackTrace();
        }
    }
    
    public void createPersistentNode(String nodePath) {
    	try {
    		if(zk.exists(nodePath, false)!=null) {
        		return;
        	}
    		else {
    			zk.create(nodePath, null,
    			        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    		}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void createEphemeralNode(String nodePath) {
    	try {
    		if(zk.exists(nodePath, false)!=null) {
        		return;
        	}
    		else {
    			zk.create(nodePath, null,
    			        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    		}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    
    //此方法要求nodePath是完整的,所以要以/origin开始
    public List<String> listChildren(String nodePath) {
    	List<String> children=null;
        try {
        	//监听若是available则通知阻塞的进程继续
            children = zk.getChildren(nodePath, true);
            if(children.size()==0) {
            	waitForavailable=true;
            	System.out.println("wait for available server");
            	availableSignal.await();
            	children = zk.getChildren(nodePath, true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }
    
    
    //删除本节点和其直接子节点,稍后添加彻底遍历版本
    public void deleteNode(String nodePath) {
        try {
            List<String> children = zk.getChildren(nodePath, true);
            for (String c : children) {
                zk.delete(nodePath + "/" + c, -1);
            }
            zk.delete(nodePath, -1);
        } catch (Exception e) {
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

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == Event.KeeperState.SyncConnected) {
            if (event.getType() == Event.EventType.None && event.getPath() == null) {
                connectedSignal.countDown();
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                	if(waitForavailable==true) {
                		waitForavailable=false;
                		availableSignal.countDown();
                	}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		
	}
}
