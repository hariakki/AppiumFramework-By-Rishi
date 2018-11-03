package com.twinspires.qa.ios.testdata;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.testdata.AbstractCredentials;

/**
 * Created by dalwinder.singh on 8/10/18.
 */
public class IOSCredentials extends AbstractCredentials {
    public IOSCredentials(TestEnv env) {
        super(env);
    }

    @Override
    public String getUsername() {
        return null;
    }
}
