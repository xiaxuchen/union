package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexStatisticVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * theMonthPushCount : 50
     * lastDayUserAddCount : 10
     * chartData : [{"date":"19/01/03","allCount":80,"percent":0.8,"receiveUserCount":100}]
     * theMonthUserAddCount : 50
     * theMonthReceiveCount : 20
     * allUserCount : 100
     * bindUserCount : 20
     * lastDayReceiveCount : 10
     * allPushCount : 100
     */
    private Long theMonthPushCount;
    private Integer lastDayUserAddCount;
    private List<ChartDataVO> chartData;
    private Integer theMonthUserAddCount;
    private Integer theMonthReceiveCount;
    private Integer allUserCount;
    private Integer bindUserCount;
    private Integer lastDayReceiveCount;
    private Long allPushCount;
    
}
