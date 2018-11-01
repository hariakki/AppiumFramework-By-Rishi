package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.util.Util;

public abstract class AbstractRegistrationData implements IRegistrationData {
    protected String userName;
    protected String mm;
    protected String dd;
    protected String yyyy;
    protected String ssn1;
    protected String ssn2;
    protected String ssn3;
    protected String email;

    public AbstractRegistrationData() {
        userName = Util.randomUsername();
        mm = Util.randomMM();
        dd = Util.randomDD();
        yyyy = Util.randomYYYY();
        ssn1 = Util.randomSSN1();
        ssn2 = Util.randomSSN2();
        ssn3 = Util.randomSSN3();
        email = userName + "@blackhole.io";
    }

    public String getMm() {
        return mm;
    }

    public String getDd() {
        return dd;
    }

    public String getYyyy() {
        return yyyy;
    }

    public String getSsn1() {
        return ssn1;
    }

    public String getSsn2() {
        return ssn2;
    }

    public String getSsn3() {
        return ssn3;
    }

    public String getUsername() {
        return userName;
    }

    public String getNewUserName() {
        return Util.randomUsername();
    }

    public String getPassword() {
        return "password1";
    }

    public String getConfirmPassword() {
        return "password1";
    }

    public String getFirstName() {
        return "Test";
    }

    public String getLastName() {
        return "Bris";
    }

    public String getAddress() {
        return "Test Address";
    }

    public String getZip() {
        String affiliate = System.getProperty("aff", "ts");
        if (affiliate.equalsIgnoreCase("ts")
                || affiliate.equalsIgnoreCase("ks")) {
            return "40517";
        } else if (affiliate.equalsIgnoreCase("cb")) {
            return "10011";
        } else if (affiliate.equalsIgnoreCase("ok")) {
            return "72201";
        }
        return "40517";
    }
    
    public String getZipSSN4() {
    	return "90001";
    }

    public String getPhone() {
        return "1234567890";
    }

    public String getCity() {
        String affiliate = System.getProperty("aff", "ts");
        if (affiliate.equalsIgnoreCase("ts")
                || affiliate.equalsIgnoreCase("ks")) {
            return "Lexington";
        } else if (affiliate.equalsIgnoreCase("cb")) {
            return "New York";
        } else if (affiliate.equalsIgnoreCase("ok")) {
            return "Little Rock";
        }
        return "Lexington";
    }

    public String getCityByZip() {
        return getCity();
    }

    public String getState() {
        String affiliate = System.getProperty("aff", "ts");
        if (affiliate.equalsIgnoreCase("ts")
                || affiliate.equalsIgnoreCase("ks")) {
            return "Kentucky";
        } else if (affiliate.equalsIgnoreCase("cb")) {
            return "New York";
        } else if (affiliate.equalsIgnoreCase("ok")) {
            return "Arkansas";
        }
        return "Kentucky";
    }
}
