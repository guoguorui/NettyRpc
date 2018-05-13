package org.gary.nettyrpc.server;

import java.io.File;
import java.net.URL;

public class ScanImpl {
    static Object scanType(String basePackage, Class<?> interfaceClass) {
        String path = basePackage.replace(".", "/");
        ClassLoader cl = ScanImpl.class.getClassLoader();
        URL url = cl.getResource(path);
        String fileUrl = url.getFile().substring(1);
        File file = new File(fileUrl);
        String[] names = file.list();
        Object implObj = null;
        for (String name : names) {
            //兄弟，包名不能少啊
            name = basePackage + "." + name.split("\\.")[0];
            Class<?> tempClass = null;
            try {
                tempClass = Class.forName(name);
                if (interfaceClass.isAssignableFrom(tempClass)) {
                    implObj = tempClass.newInstance();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return implObj;
    }

}

