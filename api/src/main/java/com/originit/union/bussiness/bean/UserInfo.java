package com.originit.union.bussiness.bean;

import org.springframework.context.annotation.Bean;

/**
 * @author super
 * @date 2020/2/2 16:24
 * @description 执念
 */
public class UserInfo {
    private  int id;
    private String name;
    private String  headImg;
    private String phone;
    private TagList tagList;
    private int sex;
    private String subscribeTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public TagList getTagList() {
        return tagList;
    }

    public void setTagList(TagList tagList) {
        this.tagList = tagList;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", headImg='" + headImg + '\'' +
                ", phone='" + phone + '\'' +
                ", tagList=" + tagList +
                ", sex=" + sex +
                ", subscribeTime='" + subscribeTime + '\'' +
                '}';
    }
}
