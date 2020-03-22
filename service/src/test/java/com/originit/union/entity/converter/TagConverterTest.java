package com.originit.union.entity.converter;

import com.originit.union.entity.TagEntity;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TagConverterTest {

    @Test
    public void to() {
        final TagEntity tagEntity = new TagEntity();
        tagEntity.setId(1L);
        tagEntity.setName("vip");
        System.out.println(TagConverter.INSTANCE.to(Arrays.asList(tagEntity)));
    }
}