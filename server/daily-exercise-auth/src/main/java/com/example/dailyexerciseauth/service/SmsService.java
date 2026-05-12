package com.example.dailyexerciseauth.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsService {
    
    private Map<String, String> codeStore = new HashMap<>();
    private Map<String, Long> codeTimeStore = new HashMap<>();
    private static final long CODE_EXPIRE_TIME = 300000;
    private static final int CODE_LENGTH = 6;

    public String sendCode(String phoneNumber) {
        String code = generateCode();
        codeStore.put(phoneNumber, code);
        codeTimeStore.put(phoneNumber, System.currentTimeMillis());
        
        System.out.println("=== 验证码已发送 ===");
        System.out.println("手机号: " + phoneNumber);
        System.out.println("验证码: " + code);
        System.out.println("有效期: 5分钟");
        
        return code;
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String storedCode = codeStore.get(phoneNumber);
        Long storedTime = codeTimeStore.get(phoneNumber);
        
        if (storedCode == null || storedTime == null) {
            return false;
        }
        
        if (System.currentTimeMillis() - storedTime > CODE_EXPIRE_TIME) {
            codeStore.remove(phoneNumber);
            codeTimeStore.remove(phoneNumber);
            return false;
        }
        
        boolean isValid = storedCode.equals(code);
        
        if (isValid) {
            codeStore.remove(phoneNumber);
            codeTimeStore.remove(phoneNumber);
        }
        
        return isValid;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}