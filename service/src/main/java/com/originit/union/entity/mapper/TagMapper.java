package com.originit.union.entity.mapper;

import com.originit.union.entity.TagEntity;
import com.originit.union.entity.vo.TagInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author xxc、
 */
@Mapper
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    List<TagInfoVO> to(List<TagEntity> entity);
}
