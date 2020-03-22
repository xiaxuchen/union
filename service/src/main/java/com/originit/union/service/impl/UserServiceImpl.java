package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.page.Pager;
import com.originit.common.util.SpringUtil;
import com.originit.union.bussiness.UserBusiness;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.converter.WeChatUserConverter;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.dao.UserDao;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.PagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xxc、
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, UserBindEntity> implements WeChatUserService {

    private UserBusiness userBusiness;

    private SqlSessionTemplate sqlSessionTemplate;


    private ThreadPoolExecutor executor;

    @Autowired
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Autowired
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserBindEntity getUserInfoByOpenId(String openId) {
        UserBindEntity user;
        user = getOne(new QueryWrapper<UserBindEntity>().lambda().eq(UserBindEntity::getOpenId, openId));
        if (user == null) {
            UserBindEntity entity = userBusiness.getUserByOpenId(openId);
            if (entity != null) {
                baseMapper.insert(entity);
                user = getOne(new QueryWrapper<UserBindEntity>().lambda().eq(UserBindEntity::getOpenId, openId));
            }
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateUsers(List<UserBindEntity> users) {
        log.info("执行批量导入");
        try (SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory()
                .openSession(ExecutorType.BATCH, false)) {
            UserDao userDao = sqlSession.getMapper(UserDao.class);
            for (int i = 0; i < users.size(); i++) {
                UserBindEntity userBindEntity = users.get(i);
                // 如果是空就跳过
                if (userBindEntity == null) {
                    continue;
                }
                userDao.insertOrUpdateUser(userBindEntity);
                if (i >= 1 && i % 100 == 0) {
                    sqlSession.flushStatements();
                }
            }
            sqlSession.flushStatements();
            sqlSession.commit();
        }
        log.info("执行批量成功");
    }

    @Override
    public Pager<UserInfoVO> getUserInfoList(List<String> phone, List<Integer> tagList, int curPage, int pageSize) {
        // 获取用户openId、标签并转换
        return PagerUtil.fromIPage(baseMapper.selectUserByPhonesAndTags(new Page<>(curPage, pageSize), phone, tagList), WeChatUserConverter.INSTANCE::to);
    }

    @Override
    public void importUsers() {
        userBusiness.batchGetAllUser(users -> executor.execute(() -> {
            log.info("开始导入");
            getService().addOrUpdateUsers(users);
            log.info("导入结束");
        }));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer[] getUserStatistic() {
        Integer allCount = baseMapper.selectCount(null);
        Integer bindCount = baseMapper.selectCount(new QueryWrapper<UserBindEntity>()
                .lambda().isNotNull(UserBindEntity::getPhone));
        return new Integer[]{allCount,bindCount};
    }

    private WeChatUserService getService () {
        return SpringUtil.getBean(this.getClass());
    }
}
