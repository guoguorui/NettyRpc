package org.gary.nettyrpc.zookeeper;

import com.google.common.base.Strings;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//加入日志
public class ZooKeeperManager {

    public static final int ZK_SESSION_TIMEOUT =5000;
    public static final String ZK_REGISTRY_PATH ="/origin";

    private String zkAddress;
    private ZooKeeper zk;

	public ZooKeeper getZk() {
		return zk;
	}

	public ZooKeeperManager(String zkAddress) {
        this.zkAddress = zkAddress;
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

            System.out.println("Successfully connected to " + zkAddress);
        } catch (IOException e) {
        	System.out.println("Failed to connect to " + zkAddress);
            e.printStackTrace();
        } catch (InterruptedException e) {
        	System.out.println("Failed to connect to " + zkAddress);
            e.printStackTrace();
        }
    }

    //每操作一次zk就close,以/root为根路径
    public void createNode(String nodePath) {
        try {
            if (zk.exists(ZK_REGISTRY_PATH, true) == null) {
                zk.create(ZK_REGISTRY_PATH, null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            zk.create(ZK_REGISTRY_PATH + "/" + nodePath, null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("Node " + nodePath + " have been created successfully.");
            zk.close();
        } catch (Exception e) {
        	System.out.println("Creating node " + nodePath + " failed.");
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
        checkGroupPath(nodePath);
        List<String> result = new ArrayList<String>();
        try {
        	//先false，再看
            List<String> children = zk.getChildren(nodePath, false);
            for (String c : children) {
                result.add(c);
            }
            //先不close，由上层处理
            //zk.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    //删除本节点和其直接子节点,稍后添加彻底遍历版本
    public void deleteNode(String nodePath) {
        checkGroupPath(nodePath);
        try {
            List<String> children = zk.getChildren(nodePath, true);
            for (String c : children) {
                zk.delete(nodePath + "/" + c, -1);
            }
            zk.delete(nodePath, -1);
            zk.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkGroupPath(String path) {
    	//Strings来自于guava
        if (Strings.isNullOrEmpty(path)) {
            String msg = "group path can not be null or empty: " + path;
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }
        if (!path.startsWith(ZK_REGISTRY_PATH)) {
            String msg = "Illegal access to group: " + path;
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
