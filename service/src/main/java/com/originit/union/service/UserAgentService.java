package com.originit.union.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.common.page.Pager;
import com.originit.union.entity.UserAgentEntity;
import com.originit.union.entity.vo.UserInfoVO;

import java.util.List;
import java.util.Set;

public interface UserAgentService extends IService<UserAgentEntity> {

    /**
     * 清除其中以前有的关系（phone和其他的经理有关系）并导入新的
     * @param agentId  经理id
     * @param phones 电话号码列表
     */
    public List<String> addRelationClearOld (Long agentId, List<String> phones);

    /**
     * 添加新的关系，如果已被其他用户设置则跳过，并提示
     * @param agentId
     * @param phones
     * @return 在其他用户已存在的
     */
    public Set<String> addRelation (Long agentId, List<String> phones);

    /**\
     * 分页获取用户管理的用户列表
     * @param curPage 当前页
     * @param pageSize 每页的大小
     * @return 分页的列表
     */
    public Pager<UserInfoVO> pagerUserAgent(Long agentId,int curPage, int pageSize);
}
