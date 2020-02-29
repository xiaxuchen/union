package com.originit.union.entity.mapper;

import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.UserBindDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserBindMapper {

    UserBindMapper INSTANCE = Mappers.getMapper(UserBindMapper.class);

    @Mapping(source = "openId",target = "openid")
    UserBindDto to(UserBindEntity entity);

    List<UserBindDto> to(List<UserBindEntity> entity);
}
