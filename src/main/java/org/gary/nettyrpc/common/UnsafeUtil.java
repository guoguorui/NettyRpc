package org.gary.nettyrpc.common;

import org.gary.nettyrpc.client.ProxyHandler;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {

    public static long CONNECTFLAG;
    public static sun.misc.Unsafe unsafe;
    static {
        try{
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            Class<?> k = ProxyHandler.class;
            CONNECTFLAG = unsafe.objectFieldOffset
                    (k.getDeclaredField("connectFlag"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean cas(Object object,int oldValue,int newValue){
        return unsafe.compareAndSwapInt(object,CONNECTFLAG,oldValue,newValue);
    }

}
