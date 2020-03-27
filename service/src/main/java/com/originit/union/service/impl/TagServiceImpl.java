package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.exceptions.BusinessException;
import com.originit.union.dao.UserDao;
import com.originit.union.dao.UserTagDao;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.UserTagEntity;
import com.originit.union.entity.converter.TagConverter;
import com.originit.union.entity.dto.TagUserAddDto;
import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.dao.TagDao;
import com.originit.union.service.TagService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务
 * @author xxc、
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, TagEntity> implements TagService {

    private UserTagDao userTagDao;

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserTagDao(UserTagDao userTagDao) {
        this.userTagDao = userTagDao;
    }

    @Override
    public List<TagInfoVO> getTagList() {
        return TagConverter.INSTANCE.to(baseMapper.selectList(null));
    }

    @Override
    public List<TagInfoVO> getAllTagWithCount() {
        return baseMapper.selectAllWithCount();
    }

    @Override
    public TagInfoVO createTag(String tagName) {
        final TagEntity tag = new TagEntity();
        tag.setName(tagName);
        baseMapper.insert(tag);
        final TagInfoVO tagInfoVO = new TagInfoVO();
        tagInfoVO.setId(tag.getId());
        tagInfoVO.setName(tag.getName());
        tagInfoVO.setCount(0);
        return tagInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(String tagId) {
        // 先删除掉该标签下的用户
        userTagDao.delete(new QueryWrapper<UserTagEntity>().lambda().eq(UserTagEntity::getTagId,tagId));
        // 然后删除该标签
        baseMapper.deleteById(tagId);
    }

    @Override
    public void updateTag(Long tagId, String tagName) {
        baseMapper.update(null,new UpdateWrapper<TagEntity>().lambda()
        .set(TagEntity::getName,tagName)
                .eq(TagEntity::getId,tagId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagOfUser(List<TagUserAddDto> userInfo, Long tagId) {
        List<UserTagEntity> userTagEntities = new ArrayList<>(userInfo.size());
        userInfo.forEach(tagUserAddDto -> {
            final UserTagEntity userTagEntity = new UserTagEntity();
            if (tagUserAddDto.getId() != null) {
                userTagEntity.setTagId(tagId);
                userTagEntity.setBindUserId(tagUserAddDto.getId());
                userTagEntities.add(userTagEntity);
                return;
            }
            final LambdaQueryWrapper<UserBindEntity> userQw = new QueryWrapper<UserBindEntity>()
                    .lambda().select(UserBindEntity::getId);
            if (tagUserAddDto.getPhone() != null && tagUserAddDto.getPhone().trim() != "") {
                userQw.eq(UserBindEntity::getPhone,tagUserAddDto.getPhone());
            }
            final UserBindEntity user = userDao.selectOne(userQw);
            if (user == null) {
                // TODO 添加错误报告处理
            } else {
                userTagEntity.setTagId(tagId);
                userTagEntity.setBindUserId(user.getId());
                userTagEntities.add(userTagEntity);
            }
        });
        if (userTagEntities.isEmpty()) {
            return;
        }
        if (userTagEntities.size() == 1) {
            userTagDao.insert(userTagEntities.get(0));
            return;
        }
        final SqlSession sqlSession = this.sqlSessionBatch();
        final UserTagDao batchUserTagDao = sqlSession.getMapper(UserTagDao.class);
        for (UserTagEntity userTagEntity : userTagEntities) {
            batchUserTagDao.insert(userTagEntity);
        }
        sqlSession.flushStatements();
        sqlSession.commit();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTagOfUser(Long userTagId) {
        final int count = userTagDao.deleteById(userTagId);
        if (count == 0) {
            throw new BusinessException("删除失败");
        }
    }

}
