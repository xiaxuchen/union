package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.originit.union.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 角色DAO
 * @Author Sans
 * @CreateTime 2019/6/14 15:57
 */
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {

    /**
     * 通过用户ID查询角色集合
     * @Author Sans
     * @CreateTime 2019/6/18 18:01
     * @Param  userId 用户ID
     * @Return List<SysRoleEntity> 角色名集合
     */
    List<SysRoleEntity> selectSysRoleByUserId(@Param("userId") Long userId);

}
