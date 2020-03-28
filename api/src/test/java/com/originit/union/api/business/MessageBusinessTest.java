package com.originit.union.api.business;

import com.originit.union.bussiness.MessageBusiness;
import com.originit.union.entity.dto.PushInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageBusinessTest {

    @Autowired
    MessageBusiness messageBusiness;

    @Test
    public void testPush() {
        PushInfoDto dto = new PushInfoDto();
        dto.setContent("wSlKNDaFbswh6rGTiNma_6QeNSnlDiB-fZ9wN_dgU7g");
        dto.setType(1);
        dto.setUsers(Arrays.asList("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA","o1U3TjudICYhr2y-4FNKf3UZsEy0"));
        messageBusiness.pushMessage(dto);
    }
}
