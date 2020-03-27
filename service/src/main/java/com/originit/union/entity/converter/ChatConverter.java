package com.originit.union.entity.converter;

import com.originit.union.entity.AgentStateEntity;
import com.originit.union.entity.ChatUserEntity;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.AgentStateDto;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.util.DateUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 聊天相关的转换类
 * @author xxc、
 */
@Mapper(imports = {DateUtil.class})
public interface ChatConverter {

    ChatConverter INSTANCE = Mappers.getMapper(ChatConverter.class);

    /**
     * 转换出等待用户的vo
     * @param user 用户信息
     * @param notRead 未读信息条数
     * @return
     */
    @Mappings({
            @Mapping(source = "user.openId",target = "id"),
            @Mapping(source = "user.headImg",target = "headImg"),
            @Mapping(source = "user.phone",target = "phone"),
            @Mapping(source = "user.name",target = "name")
    })
    ChatUserVO convertWaitingUser (UserBindEntity user, Integer notRead);

    /**
     * 将消息转换为显示对象
     * @param message 消息实体
     * @return 消息vo
     */
    @Mappings({
            @Mapping(source="messageId",target = "id"),
            @Mapping(source="openId",target = "userId"),
            @Mapping(source="content",target = "message"),
            @Mapping(source="fromUser",target = "isUser"),
            @Mapping(target = "time",expression = "java(DateUtil.toDateTimeStr(message.getGmtCreate()))")
    })
    ChatMessageVO to (MessageEntity message);

    AgentStateEntity to(AgentStateDto dto);
}
