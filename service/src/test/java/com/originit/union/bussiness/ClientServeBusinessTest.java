package com.originit.union.bussiness;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServeBusinessTest {

    @Autowired
    ClientServeBusiness business;

    @Test
    public void sendImageMessage() {
        business.sendImageMessage("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA","-KPkOS8zwAuKXY5Zir0TkBiO0FU0IMsTGDRXNX-ZHa_nSFepL0txyhe5m0Gy3icA");
    }
}