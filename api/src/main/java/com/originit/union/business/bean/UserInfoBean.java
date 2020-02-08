package com.originit.union.business.bean;

import lombok.Data;

/**
 * @author super
 * @date 2020/2/2 16:24
 * @description 执念
 */
@Data
public class UserInfoBean {
    private  String openid;
    private  String id;
    private String name;
    private String  headImg;
    private String phone;
    private TagListBean tagListBean;
    private int sex;
    private String subscribeTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public TagListBean getTagListBean() {
        return tagListBean;
    }

    public void setTagListBean(TagListBean tagListBean) {
        this.tagListBean = tagListBean;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "openid='" + openid + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", headImg='" + headImg + '\'' +
                ", phone='" + phone + '\'' +
                ", tagListBean=" + tagListBean +
                ", sex=" + sex +
                ", subscribeTime='" + subscribeTime + '\'' +
                '}';
    }
}
