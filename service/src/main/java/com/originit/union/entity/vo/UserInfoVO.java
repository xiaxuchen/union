package com.originit.union.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xxc、
 */
@Data
@NoArgsConstructor
public class UserInfoVO {

    /**
     * headImg : https://timgsa.baidu.com/timg?image&amp;quality=80&amp;size=b9999_10000&amp;sec=1579165101420&amp;di=15b492e796aaf49d330fc00929bf4e7b&amp;imgtype=jpg&amp;src=http://img2.touxiang.cn/file/20171113/b213c1ac58be0e02906ea1424781b31b.jpg
     * subscribeTime : 2019-10-1 08:20
     * phone : 17779911413
     * sex : 男
     * name : 嘿
     * pushCount: 1 本月推送次数
     * id : 100 用户的openId
     */
    private String headImg;
    private String subscribeTime;
    private String phone;
    private String sex;
    private String name;
    private Integer pushCount;
    private String id;
    private List<TagInfoVO> tags;

    public List<TagInfoVO> getTags() {
        return tags;
    }

    public void setTags(List<TagInfoVO> tags) {
        this.tags = tags;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public String getPhone() {
        return phone;
    }

    public String getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
