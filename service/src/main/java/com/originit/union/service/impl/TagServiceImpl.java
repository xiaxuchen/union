package com.originit.union.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.union.entity.TagEntity;
import com.originit.union.entity.mapper.TagMapper;
import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.mapper.TagDao;
import com.originit.union.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagDao, TagEntity> implements TagService {
    @Override
    public List<TagInfoVO> getTagList() {
        return TagMapper.INSTANCE.to(baseMapper.selectList(null));
    }
}
