package com.originit.union.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.domain.UserInfo;
import com.originit.union.entity.dto.SysUserDto;
import com.originit.union.entity.dto.SysUserQueryDto;
import com.originit.union.entity.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @author xxc、
 */
@Repository
public interface SysUserDao extends BaseMapper<SysUserEntity> {

    /**
     * 根据条件分页查询系统用户
     * @param page 分页
     * @param condition 条件
     * @return 系统用户列表
     */
    IPage<SysUserVO> selectByConditions(Page<?> page, @Param("condition") SysUserQueryDto condition);
}
