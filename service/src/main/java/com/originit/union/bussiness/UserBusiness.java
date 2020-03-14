package com.originit.union.bussiness;

import com.alibaba.fastjson.JSONObject;
import com.originit.common.exceptions.RemoteAccessException;
import com.originit.union.bussiness.protocol.CardInfo;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.mapper.UserMapper;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.util.DateUtil;
import com.originit.union.util.WechatUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.bean.WxUserList;
import com.soecode.wxtools.bean.result.WxUserListResult;
import com.soecode.wxtools.exception.WxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 用户相关的微信端操作
 * @author xxc、
 */
@Component
@Slf4j
public class UserBusiness {

    private IService wxService;

    private CardBusiness cardBusiness;

    @Autowired
    public void setCardBusiness(CardBusiness cardBusiness) {
        this.cardBusiness = cardBusiness;
    }

    @Autowired
    public void setWxService(IService wxService) {
        this.wxService = wxService;
    }

    /**
     * 填充用户相关的信息
     * @param userInfoVOS 需要填充的数据
     * @return
     */
    public List<UserInfoVO> fillUserInfo (List<UserInfoVO> userInfoVOS) {
        // 记录位置，以免遍历
        Map<String,Integer> positionMap = new HashMap<>(userInfoVOS.size());
        // 获取用户信息需要的信息
        List<WxUserList.WxUser.WxUserGet> gets = new ArrayList<>();
        // 遍历列表，准备数据
        for (int i = 0; i < userInfoVOS.size(); i++) {
            String id = userInfoVOS.get(i).getId();
            positionMap.put(id,i);
            gets.add(new WxUserList.WxUser.WxUserGet(id, WeChatConstant.LANG));
            // 每100个人请求一次
            if (gets.size() == 100) {
                batchGetUserInfo(positionMap,userInfoVOS,gets);
                // 查找后清除，下一批
                gets.clear();
            }
        }
        if (!gets.isEmpty()) {
            batchGetUserInfo(positionMap,userInfoVOS,gets);
        }
        return userInfoVOS;
    }

    private void batchGetUserInfo(Map<String,Integer>positionMap,List<UserInfoVO> userInfoVOS,
                                          List<WxUserList.WxUser.WxUserGet> gets) {
        try {
            // 获取基本信息
            List<WxUserList.WxUser> userInfoList = wxService.batchGetUserInfo(gets).getUser_info_list();
            for (WxUserList.WxUser wxUser : userInfoList) {
                Integer position = positionMap.get(wxUser.getOpenid());
                UserInfoVO origin = userInfoVOS.get(position);
                UserInfoVO userInfoVO = UserMapper.INSTANCE.to(wxUser);
                userInfoVO.setTags(origin.getTags());
                userInfoVO.setPhone(origin.getPhone());
                userInfoVO.setId(origin.getId());
                if (wxUser.getSubscribe_time() != null) {
                    userInfoVO.setSubscribeTime(DateUtil.timeStampToStr(Long.parseLong(wxUser.getSubscribe_time())));
                }
                // 更新填充后的实体
                userInfoVOS.set(position,userInfoVO);
            }
        }catch (WxErrorException e1) {
            e1.printStackTrace();
            throw new RemoteAccessException(e1.getError());
        }
    }

    /**
     * 获取所有的用户的openId
     * @return 所有用户的openid
     */
    private List<String> getAllUserOpenIds() {
        try {
            WxUserListResult result = wxService.batchGetUserOpenId(null);
            List<String> openIdList = new ArrayList<>(result.getCount());
            while (result.getCount() != 0) {
                openIdList.addAll(Arrays.asList(result.getData().getOpenid()));
                result = wxService.batchGetUserOpenId(result.getNext_openid());
            }
            return openIdList;
        } catch (WxErrorException e) {
            log.error("获取所有用户openId失败:" + e.getError().getErrmsg());
            e.printStackTrace();
            throw new RemoteAccessException(e.getError());
        }
    }

    private final int MAX_COUNT = 100;


    public static final int HANDLE_SIZE = 200;
    /**
     * 批量获取所有的用户信息
     */
    public void batchGetAllUser(Consumer<List<UserBindEntity>> consumer) {
        List<String> userOpenIds = getAllUserOpenIds();
        List<UserBindEntity> users = new ArrayList<>(HANDLE_SIZE);
        // 子列表的开始位置
        int start = 0;
        int size = userOpenIds.size();
        while (start < size) {
            // 子列表的结束位置
            int end = start + MAX_COUNT;
            // 如果超过size则等于size
            if (end > size) {
                end = size;
            }
            List<WxUserList.WxUser.WxUserGet> gets = userOpenIds.subList(start, end).stream().map(openId -> {
                WxUserList.WxUser.WxUserGet get = new WxUserList.WxUser.WxUserGet();
                get.setOpenid(openId);
                return get;
            }).collect(Collectors.toList());
            try {
                // 从微信获取用户信息并转换为dto
                List<UserBindEntity> list = wxService.batchGetUserInfo(gets)
                        .getUser_info_list().stream().map(this::wxUserToUserBind)
                        .collect(Collectors.toList());
                users.addAll(list);
            } catch (WxErrorException e) {
                log.error(e.getError().getErrmsg());
                e.printStackTrace();
            }
            if (users.size() >= HANDLE_SIZE) {
                consumer.accept(users);
                users.clear();
            }
            log.info(MessageFormat.format("当前用户导入执行状态:{0}-{1},总量为{2}",start,end,size));
            start = end;
        }
        // 循环结束如果不为空则执行一次
        if (!users.isEmpty()) {
            consumer.accept(users);
            users.clear();
        }
        log.info("用户导入执行结束");
    }

    /**
     * 通过openid获取用户电话号码
     * @param openId 用户的openId
     * @return 用户电话号码
     */
    public String getPhone (String openId) {
        try {
            String code = cardBusiness.getCardCode(openId, WeChatConstant.CARD_ID);
            if (code == null) {
                return null;
            }
            String cardInfo=  wxService.post(WechatUtil.replaceToken(WeChatConstant.URL_GET_CARD_INFO), new CardInfo(WeChatConstant.CARD_ID, code).toJson());
            //josn转化为map
            Map<String,Object> cardInfoMap = JSONObject.parseObject(cardInfo);
            String phone = cardInfoMap.get(WeChatConstant.PHONE_FIELD).toString();
            if (phone.trim().isEmpty()) {
                return null;
            }
            return phone;
        } catch (WxErrorException e) {
            log.error(e.getError().getErrmsg());
            e.printStackTrace();
            throw new RemoteAccessException(e.getError());
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RemoteAccessException("微信请求异常:" + e.getMessage());
        }
    }

    /**
     * 通过openId从网络获取用户信息
     * @param openId 用户openId
     * @return 用户实体
     */
    public UserBindEntity getUserByOpenId (String openId) {
        WxUserList.WxUser wxUser = null;
        try {
            wxUser = wxService.getUserInfoByOpenId(new WxUserList.WxUser.WxUserGet(openId, WeChatConstant.LANG));
        } catch (WxErrorException e) {
            e.printStackTrace();
            return null;
        }
        return wxUserToUserBind(wxUser);
    }

    /**
     * 转换用户
     * @param user 微信返回的用户格式
     * @return 数据库用户实体
     */
    public UserBindEntity wxUserToUserBind (WxUserList.WxUser user) {
        return UserBindEntity.builder()
                .sex(user.getSex())
                .headImg(user.getHeadimgurl())
                .name(user.getNickname())
                .subscribeTime(LocalDateTime.ofEpochSecond(Long.parseLong(user.getSubscribe_time()), 0, ZoneOffset.ofHours(8)))
                .openId(user.getOpenid())
                .phone(getPhone(user.getOpenid()))
                .build();
    }
}
