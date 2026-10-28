package com.gojava.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gojava.order.client.ProductClient;
import com.gojava.order.dto.CreateOrderReq;
import com.gojava.order.entity.Order;
import com.gojava.order.entity.OrderItem;
import com.gojava.order.mapper.OrderItemMapper;
import com.gojava.order.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final ProductClient productClient;

    public OrderService(OrderMapper orderMapper,
                         OrderItemMapper itemMapper,
                         ProductClient productClient) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.productClient = productClient;
    }

    /**
     * 下单全链路：读库存/价格 → 校验 → 扣库存 → 写订单 → 返回。
     * 跨库无法本地事务强一致，写订单失败应发 MQ 回补库存（TODO）。
     */
    @Transactional
    public Map<String, Object> create(String tenantId, Long userId, String username,
                                       CreateOrderReq req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("items 不能为空");
        }

        List<OrderItem> lines = new ArrayList<>();
        long total = 0;

        // 1+2：读库存、读价格
        for (CreateOrderReq.Item it : req.getItems()) {
            if (it.getQty() == null || it.getQty() <= 0) {
                throw new IllegalArgumentException("qty 必须 > 0");
            }
            Map<String, Object> resp = productClient.getProduct(it.getProductId());
            if (resp == null || !Integer.valueOf(0).equals(resp.get("code"))) {
                throw new IllegalStateException("商品不存在或已下架: " + it.getProductId());
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> p = (Map<String, Object>) resp.get("data");
            int stock = ((Number) p.get("stock")).intValue();
            if (stock < it.getQty()) {
                throw new IllegalStateException("库存不足: " + p.get("name") + " 剩 " + stock);
            }
            long priceCents = ((Number) p.get("priceCents")).longValue();
            total += priceCents * it.getQty();

            OrderItem line = new OrderItem();
            line.setProductId(((Number) p.get("id")).longValue());
            line.setSku((String) p.get("sku"));
            line.setProductName((String) p.get("name"));
            line.setPriceCents(priceCents);
            line.setQty(it.getQty());
            lines.add(line);
        }

        // 3：扣库存
        for (OrderItem line : lines) {
            Map<String, Object> r = productClient.deductStock(line.getProductId(), line.getQty());
            if (r == null || !Integer.valueOf(0).equals(r.get("code"))) {
                throw new IllegalStateException("扣库存失败: " + line.getProductName());
            }
        }

        // 4：写订单主表
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setTenantId(tenantId);
        order.setUserId(userId);
        order.setUsername(username);
        order.setTotalCents(total);
        order.setStatus("CREATED");
        orderMapper.insert(order);

        // 写明细
        for (OrderItem line : lines) {
            line.setOrderId(order.getId());
            itemMapper.insert(line);
        }

        // 5：返回
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("totalCents", total);
        data.put("status", "CREATED");
        data.put("items", lines);
        return data;
    }

    /** 我的订单列表 */
    public List<Order> listMy(Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getId));
    }

    /** 取消订单 */
    public void cancel(Long userId, String orderNo) {
        int rows = orderMapper.update(null,
                new LambdaUpdateWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId)
                        .eq(Order::getStatus, "CREATED")
                        .set(Order::getStatus, "CANCELLED"));
        if (rows == 0) throw new IllegalStateException("订单不存在或已取消");
    }

    private String generateOrderNo() {
        return "O" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }
}
