package com.example.plugin.manager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zeal on 2019/2/14.
 */

public class PluginManager {

    private static HashMap<Class, Class> sPlugins = new HashMap<>();
    /**
     * 缓存类
     */
    private static final Map<Class<?>, Object> mSingletonCaches = new HashMap();

//    private static class Holder {
//        public static PluginManager INSTANCE = new PluginManager();
//    }
//
//    public static PluginManager getInstance() {
//        return Holder.INSTANCE;
//    }

    public static void init() {
        try {

            Class<?> clzz = Class.forName("com.example.plugins.PluginManager");

            Method getMap = clzz.getMethod("getMap");

            HashMap<String, String> plugins = (HashMap<String, String>) getMap.invoke(clzz.newInstance());

            for (Map.Entry<String, String> plugin : plugins.entrySet()) {
                String key = plugin.getKey();
                String value = plugin.getValue();

                Class<?> clazzKey = Class.forName(key);
                Class<?> clazzValue = Class.forName(value);
                sPlugins.put(clazzKey,clazzValue);

            }
            System.out.println("加载完毕：" + plugins);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("加载异常：" + e.toString());
        }
    }


    public static <T> T getService(final Class<T> targetClazz) {
        if (!targetClazz.isInterface()) {
            throw new IllegalArgumentException("only accept interface: " + targetClazz);
        }
        return (T) Proxy.newProxyInstance(targetClazz.getClassLoader(), new Class<?>[]{targetClazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                try {
                    return invokeProxy(targetClazz, proxy, method, args);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                //Log.e(TAG, "proxy " + targetClazz);
                return null;
            }
        });
    }

    private static Object invokeProxy(final Class<?> targetClazz, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object temp;
        if ((temp = mSingletonCaches.get(targetClazz)) == null) {
            Class<?> clazz = sPlugins.get(targetClazz);
            if (clazz != null) {
                temp = clazz.newInstance();
                mSingletonCaches.put(targetClazz, temp);
                return method.invoke(temp, args);
            }
            return null;
        } else {
            return method.invoke(temp, args);
        }
    }

    /**
     * 移除单例
     *
     * @param targetClazz
     */
    public static void removeService(final Class<?> targetClazz) {
        if (targetClazz != null) {
            mSingletonCaches.remove(targetClazz);
        }
    }
}
