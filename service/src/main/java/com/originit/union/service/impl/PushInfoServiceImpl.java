package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.PushUserEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.mapper.PushInfoMapper;
import com.originit.union.mapper.PushInfoDao;
import com.originit.union.mapper.PushUserDao;
import com.originit.union.mapper.UserDao;
import com.originit.union.service.PushInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class PushInfoServiceImpl extends ServiceImpl<PushInfoDao, PushInfoEntity> implements PushInfoService {
    UserDao userDao;

    PushUserDao pushUserDao;

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
    public void addPushInfo(List<String> openidList, @Validated(CreateGroup.class) PushInfoDto pushInfoDto) {
        final List<UserBindEntity> userBindEntityList = userDao.selectList(new QueryWrapper<UserBindEntity>()
                .lambda()
                .select(UserBindEntity::getId)
                .in(UserBindEntity::getOpenId, openidList));
        final PushInfoEntity pushInfo = PushInfoMapper.INSTANCE.dto2Entity(pushInfoDto);
        baseMapper.insert(pushInfo);
        userBindEntityList.forEach(userBindEntity -> pushUserDao.insert( PushUserEntity.builder()
                .pushId(pushInfo.getId()).receiverId(userBindEntity.getId()).build()));
    }
}
