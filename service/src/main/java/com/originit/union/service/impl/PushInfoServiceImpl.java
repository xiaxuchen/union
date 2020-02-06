package com.originit.union.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.mapper.PushInfoDao;
import com.originit.union.service.PushInfoService;
import org.springframework.stereotype.Service;

@Service
public class PushInfoServiceImpl extends ServiceImpl<PushInfoDao, PushInfoEntity> implements PushInfoService {
}
