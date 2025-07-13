package com.lcwd.store.services.impl;
import com.lcwd.store.entities.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public CompletableFuture<Void> sendOrderConfirmation(List<OrderItem> orderItems, String template, String recipientEmail, String name, String orderId, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom(senderEmail);
                helper.setTo(recipientEmail);
                helper.setSubject("Order Confirmation - " + orderId);
                helper.setText(template, true);
                helper.addInline("marketmixlogo", new ClassPathResource("static/images/logo/marketmixlogo.png"));
//                for (OrderItem orderItem : orderItems) {
//                    System.out.println(orderItem.getProduct().getProductImages().get(0));
//                    //ClassPathResource productImage = new ClassPathResource("/images/product/" +  orderItem.getProduct().getProductImages().get(0));
//                    //File productImage = new File("src/main/resources/images/product/" + orderItem.getProduct().getProductImages().get(0));
////                    if (productImage.exists()) {
//                        log.info("product image exists : {}",true);
//                        template.addInline("product_" + orderItem.getProduct().getProductId());
////                    }
//                }
                mailSender.send(message);
                return null;
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send order confirmation email", e);
            }
             return null;
        });
    }
}

