package org.gary.nettyrpc.common;

import org.gary.nettyrpc.carrier.RpcRequest;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

public class ReflectionUtil {

    private static Object getImplObj(String basePackage, Class<?> interfaceClass) {
        String path = basePackage.replace(".", "/");
        ClassLoader cl = ReflectionUtil.class.getClassLoader();
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

    public static Object getResult(RpcRequest rpcRequest, String implPackage) throws Exception {
        Class<?> interfaceClass = rpcRequest.getInterfaceClass();
        Object implObject = ReflectionUtil.getImplObj(implPackage, interfaceClass);
        Method[] methods = implObject.getClass().getDeclaredMethods();
        for (Method method : methods)
            if (method.getName().equals(rpcRequest.getMethodName()))
                return (invokeMethod(implObject, method.getName(), rpcRequest.getArgs()));
        return null;
    }

    private static Object invokeMethod(Object implObj, String methodName, Object[] args) throws Exception {
        Class<?> implObjClass = implObj.getClass();
        Class<?>[] argsClass = null;
        if (args != null) {
            argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        Method method = implObjClass.getMethod(methodName, argsClass);
        return method.invoke(implObj, args);
    }

}

