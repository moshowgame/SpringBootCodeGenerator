
package com.softdev.system.generator.util;

import java.util.Map;

/**
 * @author zhenkai.blog.csdn.net
 */
public class MapUtil {
    public static String getString(Map map,String key){
            if(map!=null && map.containsKey(key)){
                try{
                    return map.get(key).toString();
                }catch (Exception e){
                    e.printStackTrace();
                    return "";
                }
            }else{
                return "";
            }
    }
    public static Integer getInteger(Map map,String key){
        if(map!=null && map.containsKey(key)){
            try{
                return (Integer) map.get(key);
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }
    }
    public static Boolean getBoolean(Map map,String key){
        if(map!=null && map.containsKey(key)){
            try{
                return (Boolean) map.get(key);
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }
}
