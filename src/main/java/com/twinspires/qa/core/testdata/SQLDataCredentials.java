package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.enums.TestEnv;

/**
 * Created by tim.white on 8/15/17.
 */
public class SQLDataCredentials extends AbstractCredentials {

    public SQLDataCredentials(TestEnv env) {
        super(env);
    }

    @Override
    public String getUsername() {
        switch (env) {
            case ITE:
                return "usr_ig_qa";
            case STE:
                return "usr_ig_qa";
            case PROD:
            case LOAD:
                return "usr_ig_report";
            default:
                return null;
        }
    }

    public String getBDSUsername() {
        return "usr_ig_qa";
    }

    public String getBDSPassword() {
        return "igaming_qa";
    }

    @Override
    public String getPassword(){
        switch (env) {
            case ITE:
                return "igaming_qa";
            case STE:
                return "igaming_qa";
            case PROD:
            case LOAD:
                return "wgyLVyFqEuoMYTofgtnqpK7R";
            default:
                return null;
        }
    }

    public String getAdwUsername(){
        switch (env) {
            case ITE:
            case STE:
                return "usr_ig_qa";
            case PROD:
                return "usr_ig_report";
            default:
                return null;
        }
    }

    public String getAdwPassword(){
        switch (env) {
            case ITE:
            case STE:
                return "igaming_qa";
            case PROD:
                return "wgyLVyFqEuoMYTofgtnqpK7R";
            default:
                return null;
        }
    }

}
