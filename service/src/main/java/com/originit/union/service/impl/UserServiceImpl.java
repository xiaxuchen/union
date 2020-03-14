package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.page.Pager;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.bussiness.UserBusiness;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.mapper.TagMapper;
import com.originit.union.entity.mapper.UserMapper;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.mapper.UserDao;
import com.originit.union.service.UserService;
import com.originit.union.util.PagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author xxc、
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, UserBindEntity> implements UserService {

    private UserBusiness userBusiness;

    private SqlSessionTemplate sqlSessionTemplate;

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
        UserBindEntity user = null;
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
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory()
                .openSession(ExecutorType.BATCH,false);
        try  {
            UserDao userDao = sqlSession.getMapper(UserDao.class);
            for (int i = 0; i < users.size(); i++) {
                userDao.insertOrUpdateUser(users.get(i));
                if (i >= 1 && i % 100 == 0) {
                    sqlSession.flushStatements();
                }
            }
            sqlSession.flushStatements();
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
        log.info("执行批量成功");
    }

    @Override
    public Pager<UserInfoVO> getUserInfoList(List<String> phone, List<Integer> tagList, int curPage, int pageSize) {
        // 获取用户openId、标签并转换
        Pager<UserInfoVO> userInfoVOPager = PagerUtil.fromIPage(baseMapper.selectUserByPhonesAndTags(new Page<>(curPage, pageSize), phone, tagList), userInfo -> {
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(userInfo.getOpenId());
            userInfoVO.setHeadImg(userInfo.getHeadImg());
            userInfoVO.setName(userInfo.getName());
            userInfoVO.setPhone(userInfo.getPhone());
            if (userInfo.getSubscribeTime() != null) {
                userInfoVO.setSubscribeTime(userInfo.getSubscribeTime().format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            userInfoVO.setSex(UserMapper.convertSex(userInfo.getSex()));
            userInfoVO.setPushCount(userInfo.getPushCount());
            userInfoVO.setTags(TagMapper.INSTANCE.to(userInfo.getTags()));
            return userInfoVO;
        });
        return userInfoVOPager;
    }

}
