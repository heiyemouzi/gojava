package com.gojava.order.controller;

import com.gojava.order.dto.CreateOrderReq;
import com.gojava.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    /** 创建订单：网关剥掉 /api/order 前缀，前端 POST /api/order/create */
    @PostMapping("/create")
    public Map<String, Object> create(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-Username") String username,
            @RequestBody CreateOrderReq req) {
        try {
            Long uid = Long.parseLong(userId);
            return ok(orderService.create(tenantId, uid, username, req));
        } catch (Exception e) {
            return err(5001, e.getMessage());
        }
    }

    /** 我的订单列表 */
    @GetMapping("/list")
    public Map<String, Object> list(@RequestHeader("X-User-Id") String userId) {
        return ok(orderService.listMy(Long.parseLong(userId)));
    }

    /** 取消订单 */
    @PostMapping("/cancel")
    public Map<String, Object> cancel(@RequestHeader("X-User-Id") String userId,
                                      @RequestParam String orderNo) {
        try {
            orderService.cancel(Long.parseLong(userId), orderNo);
            return ok(null);
        } catch (Exception e) {
            return err(5002, e.getMessage());
        }
    }

    private static Map<String, Object> ok(Object d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", 0); m.put("msg", "ok"); m.put("data", d);
        return m;
    }
    private static Map<String, Object> err(int c, String m) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", c); r.put("msg", m);
        return r;
    }
}
