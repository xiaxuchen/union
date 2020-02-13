package com.originit.union.change;


/**
 * 为{@link com.originit.union.entity.dto.PushInfoDto} 添加了属性，使得信息能够完整的传递，否则下层无法获取信息进行存储
 * 添加 {@link com.originit.common.page.Pager}类，由于分页一般要获取总记录数和记录，所以需要一个类来封装，当前页和pageSize等属性都是传递过来的，故无需传回
 * 修改 {@link com.originit.union.service.UserService#getAllUserBindInfo(int, int)}方法的返回值为{@link com.originit.common.page.Pageruju<com.originit.union.entity.dto.UserBindDto>}，因为是分页
 */
public class 推送_用户信息 {
    
    public static void main(String args[]){
    }
}
