package org.gary.nettyrpc.zookeeper;

import java.util.List;
import java.util.Random;

public class ServiceDiscover {

    private ZKManager zm;

    public ServiceDiscover(String zkAddress) {
        zm = new ZKManager(zkAddress);
    }

    public String discover(String serviceName,String exclude) {
        zm.connect();
        List<String> services = zm.listChildren(ZKManager.ZK_REGISTRY_PATH,null);
        String address = null;
        for (String service : services) {
            if (service.equals(serviceName)) {
                List<String> addresses = zm.listChildren( ZKManager.ZK_REGISTRY_PATH + "/"+ serviceName,exclude);
                Random ran = new Random();
                //不允许size为0
                int index = ran.nextInt(addresses.size());
                address = addresses.get(index);
                break;
            }
        }
        zm.closeConnect();
        return address;
    }
}
