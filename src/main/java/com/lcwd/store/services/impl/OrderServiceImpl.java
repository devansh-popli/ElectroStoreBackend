package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.OrderDto;
import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.dtos.UserDto;
import com.lcwd.store.entities.*;
import com.lcwd.store.exceptions.BadApiRequestException;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.*;
import com.lcwd.store.services.OrderService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.netty.util.internal.StringUtil;
import io.opencensus.internal.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ReferralRespository refferalRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${key}")
    private String key;
    @Value("${secret}")
    private String secret;
    private String currency = "INR";
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto, String userId, String cartId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not Found with given id"));
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("No cart Found"));
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.size() <= 0) {
            throw new BadApiRequestException("Invalid number of items in cart");
        }

        // Referral referralByLoggedInUser=refferalRepository.findByUser(user);
        //user.setReferral(referralByLoggedInUser);
        Referral referral = refferalRepository.findByReferralCode(orderDto.getReferralCode()).orElse(null);
        Order order = Order.builder().billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(orderDto.getDeliveredDate())
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .referralUser(referral != null ? referral.getUser() : null)
                .build();
        AtomicReference<Long> orderAmount = new AtomicReference<>(0L);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order).build();
            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());
        order.setRazorPayOrderId(orderDto.getRazorPayOrderId());
        order.setRazorPayPaymentId(orderDto.getRazorPayPaymentId());
//if(referral!=null && referral.getUser()!=null && !referral.getUser().isReferralRewardGiven())
//{
//    referral.getUser().setOneTimeReferralEarning(referral.getUser().getOneTimeReferralEarning()!=null?referral.getUser().getOneTimeReferralEarning()+200:200);
//    referral.getUser().setReferralRewardGiven(true);
//    if(!StringUtil.isNullOrEmpty(user.getParentReferralCode()))
//    {
//      Optional<Referral> parentReferral =refferalRepository.findByReferralCode(user.getParentReferralCode());
//      if(parentReferral.isPresent())
//      {
//       User parentUser= parentReferral.get().getUser();
//       parentUser.setOneTimeReferralEarning(parentUser.getOneTimeReferralEarning()!=null?parentUser.getOneTimeReferralEarning()+100:100);
//       userRepository.save(parentUser);
//      }
//    }
//    userRepository.save(user);
//}
//consider user is also a business user but child business user
        if(!StringUtil.isNullOrEmpty(user.getParentReferralCode()) && !user.isReferralRewardGiven()) {
            Optional<Referral> parentReferral = refferalRepository.findByReferralCode(user.getParentReferralCode());
            if (parentReferral.isPresent()) {
                User parentUser = parentReferral.get().getUser();
                parentUser.setOneTimeReferralEarning(parentUser.getOneTimeReferralEarning()!=null?parentUser.getOneTimeReferralEarning()+200:200);
                parentUser.setInActiveMoney(parentUser.getInActiveMoney() != null ? parentUser.getInActiveMoney() - 200 :0);
                if(!StringUtil.isNullOrEmpty(parentUser.getParentReferralCode())) {
                    Optional<Referral> parentsParentReferral = refferalRepository.findByReferralCode(parentUser.getParentReferralCode());
                    if (parentsParentReferral.isPresent()) {
                        User parentsParentUser = parentsParentReferral.get().getUser();
                        parentsParentUser.setOneTimeReferralEarning(parentsParentUser.getOneTimeReferralEarning()!=null?parentsParentUser.getOneTimeReferralEarning()+100:100);
                        parentsParentUser.setInActiveMoney(parentsParentUser.getInActiveMoney() != null ? parentsParentUser.getInActiveMoney() - 100 : 0);
                        userRepository.save(parentsParentUser);
                    }
                }
                userRepository.save(parentUser);
            }
        }
        user.setReferralRewardGiven(true);
        userRepository.save(user);
        cart.getItems().clear();
        cartRepository.save(cart);
        Order savedOrder = orderRepository.save(order);

        // Send order confirmation email
        emailService.sendOrderConfirmation(orderItems,loadEmailTemplate(orderItems,user.getName(), order.getOrderId(), order.getOrderAmount()),user.getEmail(), user.getName(), savedOrder.getOrderId(), savedOrder.getOrderAmount())
                .thenRun(() -> log.info("Order confirmation email sent successfully for Order ID: {}", savedOrder.getOrderId()));
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order is not found"));
        orderRepository.delete(order);

    }

    @Override
    public List<OrderDto> getOrdersOfUsers(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!!"));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getOrdersOfReferralUser(String referralId) {
        Referral referral = refferalRepository.findByReferralCode(referralId).orElseThrow(() -> new ResourceNotFoundException("User not found!!"));
        List<Order> orders = orderRepository.findByReferralUser(referral.getUser());
        List<User> users = userRepository.findByParentReferralCode(referralId);
        return Stream.concat(
                users.stream()
                        .flatMap(user -> user.getOrdersByReferralUser().stream()
                                .map(order -> {
                                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                                    orderDto.setIsChildOrder(true);
                                    orderDto.setChildUser(modelMapper.map(user, UserDto.class));
                                    orderDto.setYourCommission(orderDto.getOrderAmount() * 0.01);
                                    return orderDto;
                                })
                        ),
                orders.stream()
                        .map(order -> {
                            OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                            orderDto.setIsChildOrder(false);
                            orderDto.setYourCommission(orderDto.getOrderAmount() * 0.02);
                            return orderDto;
                        })
        ).toList();
    }
    private String loadEmailTemplate(List<OrderItem> orderItems,String customerName, String orderId, Long orderAmount) {
        String emailTemp = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); }
                .header { text-align: center; }
                .logo { width: 150px; margin-bottom: 10px; }
                .order-details { background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; }
                .product-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                .product-table th, .product-table td { border-bottom: 1px solid #ddd; padding: 10px; text-align: left; }
                .product-table th { background: #ff6600; color: white; }
                .product-image { width: 50px; height: 50px; object-fit: cover; border-radius: 5px; }
                .footer { text-align: center; font-size: 12px; color: #666; margin-top: 20px; }
                .button { background: #ff6600; color: white; text-decoration: none; padding: 10px 20px; border-radius: 5px; display: inline-block; margin-top: 20px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <img src="cid:marketmixlogo" alt="MarketMix Logo" class="logo">
                    <h2>Thank You for Your Order, {{customerName}}!</h2>
                    <p>We are excited to let you know that we have received your order.</p>
                </div>
                <div class="order-details">
                    <p><strong>Order ID:</strong> {{orderId}}</p>
                    <p><strong>Total Amount:</strong> ₹ {{orderAmount}}</p>
                </div>
                
                <h3>Order Summary</h3>
                <table class="product-table">
                    <tr>
                        <th>Product</th>
                        <th>Name</th>
                        <th>Qty</th>
                        <th>Price</th>
                    </tr>
                    {{productRows}}
                </table>
                
                <div class="footer">
                    <p>Need help? <a href="mailto:support@marketmix.co.in">Contact Support</a></p>
                    <p>© 2024 MarketMix. All Rights Reserved.</p>
                </div>
            </div>
        </body>
        </html>
    """;

// Dynamically generate product rows
        StringBuilder productRows = new StringBuilder();
        for (OrderItem orderItem : orderItems) {
            String productRow = """
        <tr>
            <td><img src="{{productImage}}" class="product-image"></td>
            <td>{{productName}}</td>
            <td>{{productQty}}</td>
            <td>₹ {{productPrice}}</td>
        </tr>
    """;
            productRow = productRow.replace("{{productImage}}", "https://devya.shop/images/product/"+orderItem.getProduct().getProductImages().get(0).toString())
                    .replace("{{productName}}", orderItem.getProduct().getTitle())
                    .replace("{{productQty}}", String.valueOf(orderItem.getQuantity()))
                    .replace("{{productPrice}}", String.valueOf(orderItem.getProduct().getDiscountedPrice()));

            productRows.append(productRow);
        }

// Replace placeholders
        emailTemp = emailTemp.replace("{{customerName}}", customerName)
                .replace("{{orderId}}", orderId)
                .replace("{{orderAmount}}", String.valueOf(orderAmount))
                .replace("{{productRows}}", productRows.toString());

        System.out.println(emailTemp);
        return emailTemp;

    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> orders = orderRepository.findAll(pageable);
        PageableResponse<OrderDto> response = HelperUtils.getPageableResponse(orders, OrderDto.class);
        return response;
    }

    @Override
    public OrderDto updateOrder(OrderDto orderDto, String orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found!!"));
        order.setPaymentStatus(orderDto.getPaymentStatus());
        order.setOrderStatus(orderDto.getOrderStatus());
        order.setBillingName(orderDto.getBillingName());
        order.setBillingPhone(orderDto.getBillingPhone());
        order.setBillingAddress(orderDto.getBillingAddress());
        if (orderDto.getOrderStatus().equalsIgnoreCase("Delivered"))
            order.setDeliveredDate(orderDto.getDeliveredDate());
//        if(orderDto.getOrderStatus().equalsIgnoreCase("Delivered"))
//        order.setDeliveredDate(new Date());
        orderRepository.save(order);
        return modelMapper.map(order, OrderDto.class);
    }

    public TransactionDetails createTransaction(Long amount) throws RazorpayException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount * 100);
        jsonObject.put("currency", currency);
        RazorpayClient razorpayClient = new RazorpayClient(key, secret);
        com.razorpay.Order order = razorpayClient.orders.create(jsonObject);
        log.info("Order : {}", order);
        return prepareTransaction(order);
    }

    private TransactionDetails prepareTransaction(com.razorpay.Order order) {
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");
        return new TransactionDetails(orderId, currency, amount, key);
    }
}
