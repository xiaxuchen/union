package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.page.Pager;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.UserBindDto;
import com.originit.union.entity.mapper.UserBindMapper;
import com.originit.union.mapper.UserDao;
import com.originit.union.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xxc、
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserBindEntity> implements UserService {
    @Override
    public void addUserIfNotExist(List<String> openIdList) {
        // 1. 校验信息的正确性
        if (openIdList == null || openIdList.size() == 0) {
            throw new IllegalArgumentException("openid 列表不能为空");
        }
        // 2. 将数据库转换并插入数据库
        baseMapper.insertUsersIfNotExist(openIdList.stream()
                .map(openId -> UserBindEntity.builder().openId(openId).build())
                .collect(Collectors.toList()));
    }

    @Override
    public List<String> getOpenidListWithoutPhone() {
        LambdaQueryWrapper<UserBindEntity> queryWrapper = new QueryWrapper<UserBindEntity>().lambda();
        queryWrapper.select(UserBindEntity::getOpenId);
        queryWrapper.isNull(UserBindEntity::getPhone);
        return baseMapper.selectObjs(queryWrapper).stream().map(o -> (String) o).collect(Collectors.toList());
    }

    @Override
    public void updateUserBind(List<UserBindDto> userBindDtoList) {
        if (userBindDtoList == null || userBindDtoList.isEmpty()) {
            throw new IllegalArgumentException("传入的用户绑定列表至少要有一个元素");
        }
        userBindDtoList.forEach(userBindDto -> {
            if (userBindDto.getOpenid() == null || userBindDto.getPhone() == null) {
                throw new IllegalArgumentException("传入的用户绑定信息的openId和phone必须不为空");
            }
            final UserBindEntity userBindEntity = UserBindEntity.builder()
                    .openId(userBindDto.getOpenid())
                    .phone(userBindDto.getPhone())
                    .build();
            // 更新openId对应的行的电话
            LambdaUpdateWrapper<UserBindEntity> updateWrapper = new UpdateWrapper<UserBindEntity>().lambda();
            updateWrapper.set(UserBindEntity::getPhone,userBindEntity.getPhone());
            updateWrapper.eq(UserBindEntity::getOpenId,userBindEntity.getOpenId());
            baseMapper.update(userBindEntity,updateWrapper);
        });
    }

    @Override
    public List<String> getUseridByphone(List<String> phoneList) {
        final LambdaQueryWrapper<UserBindEntity> qw = new QueryWrapper<UserBindEntity>().lambda();
        qw.select(UserBindEntity::getOpenId).in(UserBindEntity::getPhone,phoneList);
        return baseMapper.selectList(qw).stream()
                .map(UserBindEntity::getOpenId)
                .collect(Collectors.toList());
    }

    @Override
    public Pager<UserBindDto> getAllUserBindInfo(int curPage, int pageSize) {
        final IPage<UserBindEntity> userBindData = baseMapper.selectPage(new Page<>(curPage, pageSize), new QueryWrapper<UserBindEntity>().lambda()
                .select(UserBindEntity::getOpenId, UserBindEntity::getPhone));
        return new Pager<>(UserBindMapper.INSTANCE.to(userBindData.getRecords()),userBindData.getTotal());
    }

}
