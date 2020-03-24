package com.originit.union.entity.converter;

import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.SysUserEntity;
import com.originit.union.entity.dto.SysUserCreateDto;
import com.originit.union.entity.dto.SysUserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysUserConverter {

    SysUserConverter INSTANC = Mappers.getMapper(SysUserConverter.class);

    /**
     * 更新的时候将更新的dto转化为实体，存储到数据库
     * @param dto
     * @return 用户实体
     */
    @Mappings({
            @Mapping(source = "userId",target="userId"),
            @Mapping(source = "headImg",target="headImg"),
            @Mapping(source = "username",target="username"),
            @Mapping(source = "phone",target="phone"),
            @Mapping(source = "password",target="password"),
            @Mapping(source = "salt",target="salt")
    })
    SysUserEntity to (SysUserUpdateDto dto);

    /**
     * 添加用户是的dto转换为实体进行插入
     * @param sysUserDto 添加的dto
     * @return 系统用户实体
     */
    @Mappings({
            @Mapping(source = "headImg",target="headImg"),
            @Mapping(source = "username",target="username"),
            @Mapping(source = "phone",target="phone"),
            @Mapping(source = "password",target="password"),
            @Mapping(source = "salt",target="salt")
    })
    SysUserEntity to(SysUserCreateDto sysUserDto);

    /**
     * 添加用户时若有客户经理，需要从dto中提取经理实体保存
     * @param sysUserDto 添加系统用户dto
     * @return 经理实体信息
     */
    @Mappings({
            @Mapping(source = "des",target="des"),
            @Mapping(source = "name",target="name"),
            @Mapping(source = "sex",target="sex")
    })
    AgentInfoEntity toAgentInfoEntity(SysUserCreateDto sysUserDto);

    /**
     * 将更新用户信息的dto转换为经理的信息
     * @param sysUserDto 用户更新的dto
     * @return 客户经理信息
     */
    @Mappings({
            @Mapping(source = "des",target="des"),
            @Mapping(source = "name",target="name"),
            @Mapping(source = "sex",target="sex"),
            @Mapping(source = "agentId",target="id"),
            @Mapping(source = "userId",target="sysUserId")
    })
    AgentInfoEntity toAgentInfoEntity(SysUserUpdateDto sysUserDto);

}
