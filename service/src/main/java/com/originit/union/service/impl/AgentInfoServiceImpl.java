package com.originit.union.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.mapper.AgentInfoDao;
import com.originit.union.service.AgentInfoService;
import org.springframework.stereotype.Service;

@Service
public class AgentInfoServiceImpl extends ServiceImpl<AgentInfoDao,AgentInfoEntity> implements AgentInfoService {
}
