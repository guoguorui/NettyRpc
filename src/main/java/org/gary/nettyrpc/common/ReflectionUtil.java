package org.gary.nettyrpc.common;

import org.gary.nettyrpc.carrier.RpcRequest;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {


    public static Object getResult(RpcRequest rpcRequest, String implPackage) throws Exception {
        Class<?> interfaceClass = rpcRequest.getInterfaceClass();
        Object implObject = ReflectionUtil.getImplObj(implPackage, interfaceClass);
        Method[] methods = implObject.getClass().getDeclaredMethods();
        for (Method method : methods)
            if (method.getName().equals(rpcRequest.getMethodName()))
                return (invokeMethod(implObject, method.getName(), rpcRequest.getArgs()));
        return null;
    }

    private static Object getImplObj(String basePackage, Class<?> interfaceClass) throws Exception {
        String[] classSimpleNames = getClassSimpleNames(basePackage);
        Object implObj = null;
        if (classSimpleNames != null) {
            for (String classSimpleName : classSimpleNames) {
                //兄弟，包名不能少啊
                String className = basePackage + "." + classSimpleName.split("\\.")[0];
                Class<?> tempClass = null;
                tempClass = Class.forName(className);
                if (interfaceClass.isAssignableFrom(tempClass)) {
                    implObj = tempClass.newInstance();
                    break;
                }
            }
        }
        return implObj;
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

    private static String[] getClassSimpleNames(String packageName) {
        String path = packageName.replace(".", "/");
        ClassLoader cl = ReflectionUtil.class.getClassLoader();
        URL url = cl.getResource(path);
        if (url != null) {
            String fileUrl = url.getFile().substring(1);
            File file = new File(fileUrl);
            return file.list();
        }
        return null;
    }

    public static List<String> getInterfaceNames(String packageName) throws Exception {
        List<String> list = null;
        String[] classSimpleNames = getClassSimpleNames(packageName);
        if (classSimpleNames != null) {
            list = new ArrayList<>();
            for (String classSimpleName : classSimpleNames) {
                String className = packageName + "." + classSimpleName.split("\\.")[0];
                Class<?> tempClass = null;
                tempClass = Class.forName(className);
                list.add(tempClass.getInterfaces()[0].getSimpleName());
            }
        }
        return list;
    }

}

