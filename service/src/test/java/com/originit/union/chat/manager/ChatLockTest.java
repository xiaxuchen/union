package com.originit.union.chat.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatLockTest {

    @Autowired
    ChatLock lock;

    @Test
    public void name() {
        lock.lockUser(() -> {
            System.out.println("hello");
            hello();
            return null;
        });

        lock.lockUser(() -> {
            System.out.println("hello");
            hello();
            return null;
        });
    }

    public void hello () {
        lock.lockUser(() -> {
            System.out.println("good");
            return null;
        });
    }
}