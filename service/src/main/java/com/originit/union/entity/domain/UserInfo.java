package com.originit.union.entity.domain;

import com.originit.union.entity.TagEntity;
import com.originit.union.entity.UserBindEntity;

import java.util.List;

/**
 * 用户信息
 */
public class UserInfo extends UserBindEntity {

    /**
     * 用户的标签
     */
    private List<TagEntity> tags;

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "tags=" + tags +
                '}' + super.toString();
    }
}
