package com.originit.union.entity;

import java.util.List;

/**
 * @author super
 * @date 2020/2/2 16:23
 * @description 执念
 */
public class UserTagListEntity {
    private   int  total;
    private List<UserInfoEntity> userInfo;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserInfoEntity> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<UserInfoEntity> userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserTagListEntity{" +
                "total=" + total +
                ", userInfo=" + userInfo +
                '}';
    }
}
