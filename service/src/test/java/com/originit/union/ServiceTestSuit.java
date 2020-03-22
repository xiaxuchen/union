package com.originit.union;

import com.originit.union.dao.UserDaoTest;
import com.originit.union.service.UserServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 所有测试类的启动
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {UserServiceTest.class, UserDaoTest.class}
)
public class ServiceTestSuit {
}
