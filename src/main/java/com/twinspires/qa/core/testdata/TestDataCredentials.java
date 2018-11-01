package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.enums.TestEnv;

/**
 * Created by tim.white on 8/15/17.
 */
public class TestDataCredentials extends AbstractCredentials {

    public TestDataCredentials(TestEnv env) {
        super(env);
    }

    @Override
    public String getUsername() {
        return "qa_automation";
    }

    @Override
    public String getPassword(){
        return "QAqa2017!";
    }
}
