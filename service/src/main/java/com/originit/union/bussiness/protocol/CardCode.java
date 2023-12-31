package com.originit.union.bussiness.protocol;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;

@Data
public class CardCode {
    private String openid;
    private String card_id;

    public CardCode(String openid, String card_id) {
        this.openid = openid;
        this.card_id = card_id;
    }

    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsString(this);
    }
}
