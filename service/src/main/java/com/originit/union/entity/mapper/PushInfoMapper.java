package com.originit.union.entity.mapper;

import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.dto.PushInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PushInfoMapper {

    PushInfoMapper INSTANCE = Mappers.getMapper(PushInfoMapper.class);

    PushInfoEntity dto2Entity(PushInfoDto dto);

}
