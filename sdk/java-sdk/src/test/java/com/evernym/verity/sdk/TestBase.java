package com.evernym.verity.sdk;

import com.evernym.verity.sdk.utils.Context;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class TestBase {

    public void withContext(ThrowingConsumer<Context> testCode) throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            testCode.accept(context);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
