package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.vo.ChartDataVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.charts.ChartData;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface PushInfoDao extends BaseMapper<PushInfoEntity> {

    /**
     * 查找全部的推送记录条数
     * @param start 开始日期
     * @param end 结束日期
     * @return
     */
    Long selectAllPushCount(@Param("start") String start,@Param("end") String end);

    /**
     * 查找全部的接受记录条数
     * @param start 开始日期
     * @param end 结束日期
     * @return
     */
    Long selectAllSentCount (@Param("start") String start,@Param("end") String end);

    /**
     * 查询
     * @param start
     * @param end
     * @return
     */
    List<ChartDataVO> selectChartData (@Param("start") String start,@Param("end") String end);

}
