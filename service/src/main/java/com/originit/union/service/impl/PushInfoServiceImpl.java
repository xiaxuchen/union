package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.util.DateUtil;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.constant.SystemConstant;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.PushUserEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.mapper.PushInfoMapper;
import com.originit.union.entity.vo.IndexStatisticVO;
import com.originit.union.mapper.PushInfoDao;
import com.originit.union.mapper.PushUserDao;
import com.originit.union.mapper.UserDao;
import com.originit.union.service.PushInfoService;
import com.originit.union.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class PushInfoServiceImpl extends ServiceImpl<PushInfoDao, PushInfoEntity> implements PushInfoService {
    private UserDao userDao;

    private PushUserDao pushUserDao;

    private RedisService redisService;

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public void setPushUserDao(PushUserDao pushUserDao) {
        this.pushUserDao = pushUserDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPushInfo(@Validated(CreateGroup.class) PushInfoDto pushInfoDto) {
        Assert.notNull(pushInfoDto.getPushId(),"推送异常，没有正常进行推送");
        Assert.notNull(pushInfoDto.getPusher(),"推送异常，推送者的id不能为空");
        final List<UserBindEntity> userBindEntityList = userDao.selectList(new QueryWrapper<UserBindEntity>()
                .lambda()
                .select(UserBindEntity::getId)
                .in(UserBindEntity::getOpenId, pushInfoDto.getUsers()));
        final PushInfoEntity pushInfo = PushInfoMapper.INSTANCE.dto2Entity(pushInfoDto);
        // 将状态设置为发送中
        pushInfo.setStatus(PushInfoEntity.STATUS.SENDIND);
        // 设置预期的发送人数
        pushInfo.setCount(pushInfoDto.getUsers().size());
        baseMapper.insert(pushInfo);
        userBindEntityList.forEach(userBindEntity -> pushUserDao.insert( PushUserEntity.builder()
                .pushId(pushInfo.getId()).receiverId(userBindEntity.getId()).build()));

    }

    @Override
    @Transactional(readOnly = true)
    public IndexStatisticVO getPushStatistic(String start, String end) {
        return IndexStatisticVO.builder()
                .chartData(baseMapper.selectChartData(start,end))
                .build();
    }

//
//    @Override
//    public synchronized void refreshPushStatistic(Integer addCount,Integer sentCount) {
//        final Long allPushCount = redisService.get(SystemConstant.ALL_PUSH_COUNT, Long.class);
//        // 更新所有的推送用户量
//        if (allPushCount == null) {
//            redisService.set(SystemConstant.ALL_PUSH_COUNT,addCount);
//        } else {
//            redisService.set(SystemConstant.ALL_PUSH_COUNT,allPushCount + addCount);
//        }
//        // 更新每月的发送增长
//        Integer monthPushCount = redisService.get(SystemConstant.MONTH_PUSH_COUNT, Integer.class);
//        if (monthPushCount == null || DateUtil.isMonth_1st()) {
//            monthPushCount = addCount;
//        } else {
//            monthPushCount += addCount;
//        }
//        redisService.set(SystemConstant.MONTH_PUSH_COUNT, monthPushCount);
//    }
}
