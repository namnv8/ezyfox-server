package com.tvd12.ezyfoxserver.testing.handler;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.constant.EzyCommand;
import com.tvd12.ezyfoxserver.handler.EzySimpleRequestFactory;
import com.tvd12.ezyfoxserver.testing.BaseCoreTest;

public class EzyRequestFactoryTest extends BaseCoreTest {

    @Test
    public void test() {
        EzySimpleRequestFactory factory = new EzySimpleRequestFactory();
        factory.newRequest(EzyCommand.LOGIN);
        factory.newRequest(EzyCommand.APP_ACCESS);
        factory.newRequest(EzyCommand.APP_REQUEST);
        factory.newRequest(EzyCommand.HANDSHAKE);
    }
    
}
