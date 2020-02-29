package com.originit.union.util;

import java.util.HashMap;
import java.util.Map;

public class EventUtil {

    /**
     * 获取EventKey中的参数
     * @param eventKey 事件的key
     * @return 参数map
     */
    public static Map<String,String> getEventKeyParams (String eventKey) {
        String[] url = eventKey.split("\\?");
        if (url.length > 1) {
            String[] params = url[1].split("&");
            Map<String,String> map = new HashMap<>(params.length);
            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                map.put(param[0].trim(),param[1].trim());
            }
            return map;
        }
        return new HashMap<>(0);
    }
}
