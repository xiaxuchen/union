package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.page.Pager;
import com.originit.common.util.SpringUtil;
import com.originit.union.entity.UserAgentEntity;
import com.originit.union.entity.mapper.TagMapper;
import com.originit.union.entity.mapper.UserMapper;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.mapper.UserAgentDao;
import com.originit.union.service.UserAgentService;
import com.originit.union.util.PagerUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class UserAgentServiceImpl extends ServiceImpl<UserAgentDao, UserAgentEntity> implements UserAgentService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> addRelationClearOld(Long agentId, List<String> phones) {
        // 将不存在的导入，获取已存在的
        final List<String> deleteList = getService().addRelation(agentId, phones).stream().collect(Collectors.toList());
        if (deleteList.isEmpty()) {
            return deleteList;
        }
        // 删除掉其他经理已存在的
        baseMapper.delete(new QueryWrapper<UserAgentEntity>().lambda()
                .in(UserAgentEntity::getUserPhone,deleteList));
        // 重新插入为新的经理的
        this.saveBatch(deleteList.stream().map(phone -> UserAgentEntity.builder()
                .userPhone(phone)
                .agent(agentId)
                .build()).collect(Collectors.toList()),100);
        return deleteList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<String> addRelation(Long agentId, List<String> phones) {
        final List<String> list = baseMapper.selectList(new QueryWrapper<UserAgentEntity>().lambda()
                .ne(UserAgentEntity::getId,agentId)
                .select(UserAgentEntity::getUserPhone)
                .in(UserAgentEntity::getUserPhone, phones)).stream().map(user -> user.getUserPhone()).collect(Collectors.toList());
        // 已有的
        final List<String> alreadyHas = baseMapper.selectList(new QueryWrapper<UserAgentEntity>().lambda()
                .eq(UserAgentEntity::getId,agentId)
                .select(UserAgentEntity::getUserPhone)
                .in(UserAgentEntity::getUserPhone, phones)).stream().map(user -> user.getUserPhone()).collect(Collectors.toList());
        Set<String> alreadySet = new HashSet<>(alreadyHas);
        Set<String> excludes = new HashSet<String>(list);
        // 过滤掉排除掉的，如果不是自己或其他人已有，就添加
        final List<String> collect = phones.stream().filter(phone -> {
            return !(excludes.contains(phone) || alreadyHas.contains(phone));
        }).collect(Collectors.toList());
        // 剩下的批量插入到表中
        this.saveBatch(collect.stream().map(phone -> UserAgentEntity.builder()
                .userPhone(phone)
                .agent(agentId)
                .build()).collect(Collectors.toList()),100);
        return excludes;
    }

    @Override
    public Pager<UserInfoVO> pagerUserAgent(Long agentId,int curPage, int pageSize) {
        final Pager<UserInfoVO> userInfoVOPager = PagerUtil.fromIPage(baseMapper.selectByAgentId(PagerUtil.createPage(curPage, pageSize), agentId), userInfo -> {
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(userInfo.getOpenId());
            userInfoVO.setHeadImg(userInfo.getHeadImg());
            userInfoVO.setName(userInfo.getName());
            userInfoVO.setPhone(userInfo.getPhone());
            if (userInfo.getSubscribeTime() != null) {
                userInfoVO.setSubscribeTime(userInfo.getSubscribeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            userInfoVO.setSex(UserMapper.convertSex(userInfo.getSex()));
            userInfoVO.setPushCount(userInfo.getPushCount());
            if (userInfo.getTags() != null && !userInfo.getTags().isEmpty()) {
                userInfoVO.setTags(TagMapper.INSTANCE.to(userInfo.getTags()));
            }
            return userInfoVO;
        });
        return userInfoVOPager;
    }

    public UserAgentService getService () {
        return SpringUtil.getBean(UserAgentService.class);
    }
}
