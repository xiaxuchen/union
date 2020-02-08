package com.originit.union.business.bean;

import java.util.List;

/**
 * @author super
 * @date 2020/2/2 16:23
 * @description 执念
 */
public class UserListBean {
    private   int  total;
    private List<UserInfoBean> userInfoBean;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserInfoBean> getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(List<UserInfoBean> userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    @Override
    public String toString() {
        return "UserListBean{" +
                "total=" + total +
                ", userInfoBean=" + userInfoBean +
                '}';
    }
}
