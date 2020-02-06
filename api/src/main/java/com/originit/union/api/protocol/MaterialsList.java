package com.originit.union.api.protocol;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;

/**
 * @author super post参数  素材列表
 * @date 2020/2/4 15:09
 * @description 执念
 */
@Data
public class MaterialsList {
    private  String type;
    private int  offset;
    private  int count;

    public MaterialsList(String type, int offset, int count) {
        this.type = type;
        this.offset = offset;
        this.count = count;
    }
    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsString(this);
    }
    @Override
    public String toString() {
        return "MaterialsList{" +
                "type='" + type + '\'' +
                ", offset='" + offset + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
