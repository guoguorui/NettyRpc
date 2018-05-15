package org.gary.nettyrpc.zookeeper;

public class ServiceRegister {

    private ZKManager zm;

    public ServiceRegister(String zkAddress) {
        zm = new ZKManager(zkAddress);
    }

    public void register(String serviceName, String address) {
        zm.connect();
        String prePath = ZKManager.ZK_REGISTRY_PATH + "/";
        zm.createPersistentNode(prePath + serviceName);
        zm.createEphemeralNode(prePath + serviceName + "/" + address);
        System.out.println("注册 " + prePath + serviceName + "/" + address + " 成功");
    }

    public void closeZk() {
        zm.closeConnect();
    }

}
