package com.gojava.product.controller;

import com.gojava.product.entity.Product;
import com.gojava.product.mapper.ProductMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 内部服务间调用接口（不经过网关，仅 order-service 在内网调用）。
 */
@RestController
@RequestMapping("/internal/product")
public class ProductInnerController {

    private final ProductMapper mapper;
    public ProductInnerController(ProductMapper mapper) { this.mapper = mapper; }

    /** 读价格+库存（下单第1、2步用） */
    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        Product p = mapper.getForUpdate(id);
        if (p == null) return err(4004, "商品不存在");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", p.getId());
        data.put("sku", p.getSku());
        data.put("name", p.getName());
        data.put("priceCents", p.getPriceCents());
        data.put("stock", p.getStock());
        return ok(data);
    }

    /** 扣库存（下单第4步用）；库存不足返回失败 */
    @PostMapping("/{id}/deduct")
    @Transactional
    public Map<String, Object> deduct(@PathVariable Long id, @RequestParam int qty) {
        int rows = mapper.deductStock(id, qty);
        if (rows != 1) return err(4005, "库存不足");
        return ok(null);
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
