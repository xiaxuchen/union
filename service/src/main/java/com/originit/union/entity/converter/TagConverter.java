package com.originit.union.entity.converter;

import com.originit.union.entity.TagEntity;
import com.originit.union.entity.vo.TagInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author xxc、
 */
@Mapper
public interface TagConverter {

    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);

    List<TagInfoVO> to(List<TagEntity> entity);
}
