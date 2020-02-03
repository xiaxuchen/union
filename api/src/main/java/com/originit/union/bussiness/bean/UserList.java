package com.originit.union.bussiness.bean;

import java.util.List;

/**
 * @author super
 * @date 2020/2/2 16:23
 * @description 执念
 */
public class UserList {
    private   int  total;
    private List<UserInfo> userInfo;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserList{" +
                "total=" + total +
                ", userInfo=" + userInfo +
                '}';
    }
}
