package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException; // 導入 MailException
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // 導入 Logger
import org.slf4j.LoggerFactory; // 導入 LoggerFactory

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class); // ⭐ 新增：日誌記錄器 ⭐

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        logger.info("嘗試發送郵件到: {}, 主旨: {}", to, subject); // ⭐ 新增日誌 ⭐
        logger.debug("郵件內容: {}", text); // ⭐ 新增日誌 ⭐

        try {
            mailSender.send(message);
            logger.info("郵件已成功發送至: {}", to); // ⭐ 新增成功日誌 ⭐
        } catch (MailException e) {
            logger.error("發送郵件到 {} 時發生錯誤: {}", to, e.getMessage()); // ⭐ 新增錯誤日誌 ⭐
            logger.error("郵件發送異常詳細信息:", e); // ⭐ 打印堆棧追蹤 ⭐
            // 可以選擇重新拋出異常或進行其他錯誤處理
            throw new RuntimeException("無法發送郵件: " + e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "密碼重設請求";
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        String text = "您好，\n\n您請求重設密碼。請點擊以下連結重設您的密碼：\n" + resetLink + "\n\n如果您沒有請求此操作，請忽略此郵件。\n\n謝謝！";
        sendEmail(toEmail, subject, text);
    }
}