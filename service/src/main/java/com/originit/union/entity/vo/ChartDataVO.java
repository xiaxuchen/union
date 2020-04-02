package com.originit.union.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChartDataVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * date : 19/01/03
     * allCount : 80
     * percent : 0.8
     * receiveUserCount : 100
     */
    private String date;
    private Integer allCount;
    private Double percent;
    private Integer receiveUserCount;
}