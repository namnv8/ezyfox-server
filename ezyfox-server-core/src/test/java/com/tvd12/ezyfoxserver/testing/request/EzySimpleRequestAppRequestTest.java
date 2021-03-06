package com.tvd12.ezyfoxserver.testing.request;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.request.EzySimpleRequestPluginRequest;
import com.tvd12.ezyfoxserver.testing.BaseCoreTest;

public class EzySimpleRequestAppRequestTest extends BaseCoreTest {

    @Test
    public void test() {
        EzySimpleRequestPluginRequest request = new EzySimpleRequestPluginRequest();
        request.deserializeParams(EzyEntityFactory.newArrayBuilder()
                .append(1)
                .append(EzyEntityFactory.newArrayBuilder())
                .build());
        request.release();
    }
    
}
