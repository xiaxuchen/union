package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * headImg : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579165101420&di=15b492e796aaf49d330fc00929bf4e7b&imgtype=jpg&src=http://img2.touxiang.cn/file/20171113/b213c1ac58be0e02906ea1424781b31b.jpg
 * phone : 17779911413
 * name : 嘿
 * lastMessage : 你好啊
 * notRead : 10
 * wechatMessageId : 100
 * userId: 3
 * @author xxc、
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserVO {

    private String headImg;
    private String phone;
    private String name;
    private ChatMessageVO lastMessage;
    private Integer notRead;
    private String id;
    private Long agentId;
    private String time;
}
