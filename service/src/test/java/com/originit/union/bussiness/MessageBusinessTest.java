package com.originit.union.bussiness;

import com.originit.union.entity.dto.PushInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageBusinessTest {

    @Autowired
    MessageBusiness messageBusiness;

    @Test
    public void previewPush() {
    }

    @Test
    public void pushMessage() {
        List<String> list = new ArrayList<>();
        list.add("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA");
        list.add("o1U3TjoBfIKeo_dyR380-Z4Vw_vU");
        PushInfoDto pushInfoDto = new PushInfoDto();
        pushInfoDto.setType(1);
        pushInfoDto.setUsers(list);
        pushInfoDto.setContent("wSlKNDaFbswh6rGTiNma__oVrm6SkiUbCm54Hthk2DI");
        messageBusiness.pushMessage(pushInfoDto);
    }
}