package com.originit.union.entity.converter;

import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.dto.PushInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PushInfoConverter {

    PushInfoConverter INSTANCE = Mappers.getMapper(PushInfoConverter.class);

    PushInfoEntity dto2Entity(PushInfoDto dto);

}
