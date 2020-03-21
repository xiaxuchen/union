package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.vo.IndexStatisticVO;
import org.apache.poi.ss.formula.functions.T;

/**
 * 通过此类可以访问推送消息表
 * @author xxc、
 */
public interface PushInfoService extends IService<PushInfoEntity> {
    /**
     *添加用户推送信息
     * @param pushInfoDto  推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
     void addPushInfo(PushInfoDto pushInfoDto);

    /**
     * 获取推送的统计信息
     * @return 推送统计信息
     */
    IndexStatisticVO getPushStatistic(String start,String end);
}
