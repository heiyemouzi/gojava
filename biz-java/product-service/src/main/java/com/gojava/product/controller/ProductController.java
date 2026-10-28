package com.gojava.product.controller;

import com.gojava.product.entity.Product;
import com.gojava.product.mapper.ProductMapper;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    private final ProductMapper mapper;
    public ProductController(ProductMapper mapper) { this.mapper = mapper; }

    /** 功能3：商品库存+价格列表（前端下单页与库存页共用） */
    @GetMapping("/list")
    public Map<String, Object> list(@RequestHeader(value = "X-Tenant-Id", defaultValue = "default") String tenantId) {
        List<Product> list = mapper.listOnShelf(tenantId);
        return ok(list);
    }

    private static Map<String, Object> ok(Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", 0); m.put("msg", "ok"); m.put("data", data);
        return m;
    }
}
