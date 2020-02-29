package com.originit.union.bussiness.protocol;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;

@Data
public class CardInfo {
    private String card_id;
    private  String code;

    public CardInfo(String card_id, String code) {
        this.card_id = card_id;
        this.code = code;
    }

    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsString(this);
    }
}