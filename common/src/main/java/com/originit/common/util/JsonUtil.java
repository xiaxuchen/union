package com.originit.common.util;


import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {
	
    private static  ObjectMapper mapper;
    static{
        mapper=new ObjectMapper();
    }
    public static String toJson(Object obj) throws IOException {
        if (obj == null)
            return "";
        return mapper.writeValueAsString(obj);
    }
}
