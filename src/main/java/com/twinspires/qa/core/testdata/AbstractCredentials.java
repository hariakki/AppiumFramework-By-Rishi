package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.util.Util;

import static com.twinspires.qa.core.enums.TestEnv.PROD;

/**
 * Created by chad.justice on 9/27/2016.
 */
public abstract class AbstractCredentials implements ICredentials {

    protected TestEnv env;

    private AbstractCredentials(){}
    public AbstractCredentials(TestEnv env) {
        if (null != env){
            this.env = env;
        } else {
            throw new NullPointerException();
        }
    }

    public String getPassword() {
        return "password1";
    }

    public String getEmailIDForPayPalLogin(Affiliate affiliate) {
        if (env.equals(PROD))
            return "ed.pauley@twinspires.com";
        else {
            switch (affiliate.toString().toLowerCase()) {
                case "twinspires":
                    return "ExistingPayPalQAA@twinspires.com";
                case "keenelandselect":
                    return "ExistingPayPalQAA@keenelandselect.com";
                default:
                    return null;
            }
        }
    }

    public String getEmailIDForPayPalFTDLogin() {
        switch (env) {
            case ITE:
                return "PayPalFTD@twinspires.com";
            case STE:
                return "PayPalFTD@twinspires.com";
            case PROD:
                return "";
            default:
                return null;
        }
    }

    public String getPasswordForPayPalLogin(){
        switch (env) {
            case ITE:
                return "QAA2017!";
            case STE:
                return "QAA2017!";
            case PROD:
                return Util.decrypt("QXV0bzEyMyFBdXRvMTIzIQ==","QXV0b21hdGlvblZlY3Rvcg==","8yMmxLRCEVoEXrL/K84mFg==");
            default:
                return null;
        }
    }
}
