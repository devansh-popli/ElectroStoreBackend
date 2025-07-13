package com.lcwd.store;

import com.lcwd.store.entities.Role;
import com.lcwd.store.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EmptyStackException;
import java.util.UUID;


@SpringBootApplication
@EnableCaching
public class ElectroStoreApplication implements CommandLineRunner {

    @Value("${admin.role.id}")
    private String role_admin_id;
    @Value("${normal.role.id}")
    private String role_normal_id;
    @Value("${business.role.id}")
    private String business_role_id;
    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
//        String emailTemp= """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <style>
//                    body { font-family: Arial, sans-serif; }
//                    .container { padding: 20px; text-align: center; }
//                    .order-details { background: #f9f9f9; padding: 15px; border-radius: 5px; }
//                    .button { background: #ff6600; color: white; text-decoration: none; padding: 10px 20px; border-radius: 5px; }
//                </style>
//            </head>
//            <body>
//                <div class="container">
//                    <h2>Thank You for Your Order, {{customerName}}!</h2>
//                    <img src="cid:marketmixlogo" alt="MarketMix Logo" width="150">
//                    <p>We are excited to let you know that we have received your order.</p>
//                    <div class="order-details">
//                        <p><strong>Order ID:</strong> {{orderId}}</p>
//                        <p><strong>Total Amount:</strong> ₹ {{orderAmount}}</p>
//                    </div>
//                    <a href="#" class="button">Track Your Order</a>
//                </div>
//            </body>
//            </html>
//        """;
//        emailTemp = emailTemp.replace("{{customerName}}", "devansh")
//                .replace("{{orderId}}", "orderId")
//                .replace("{{orderAmount}}", "orderAmount"+"");
//        System.out.println(emailTemp);
        SpringApplication.run(ElectroStoreApplication.class, args);
    }

    public void run(String... args) throws Exception {
        try {

            Role role_admin = Role.builder().roleId(role_admin_id).roleName("ROLE_ADMIN").build();
            Role role_normal = Role.builder().roleId(role_normal_id).roleName("ROLE_NORMAL").build();
            Role role_business = Role.builder().roleId(business_role_id).roleName("ROLE_BUSINESS").build();
            roleRepository.save(role_normal);
            roleRepository.save(role_admin);
            roleRepository.save(role_business);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
