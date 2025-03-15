package com.codelab.expensetracker.helper;
import org.springframework.stereotype.Component;  // For @Component
import java.util.Random;

@Component
public class OTPgenerator {

    private int otp;

    public OTPgenerator(int otp) {
        this.otp = otp;
    }

    public OTPgenerator() {
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }


    public int generateOTP(){

        Random random = new Random();
        int LB = 100000;
        int UB = 999999;

        int number =  random.nextInt(UB - LB + 1) + LB;
        return number;
    }
}
