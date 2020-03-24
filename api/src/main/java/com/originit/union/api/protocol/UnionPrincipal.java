package com.originit.union.api.protocol;

import com.originit.union.entity.SysUserEntity;
import com.sun.security.auth.UserPrincipal;

import java.security.Principal;

public class UnionPrincipal implements Principal {

    private SysUserEntity userEntity;

    private String name;

    public UnionPrincipal(SysUserEntity userEntity,String name) {
        this.userEntity = userEntity;
        this.name = name;
    }

    public SysUserEntity getUserEntity() {
        return userEntity;
    }

    @Override
    public String toString() {
        return "UnionPrincipal{" +
                "userEntity=" + userEntity +
                '}';
    }

    @Override
    public String getName() {
        return this.name;
    }
}
