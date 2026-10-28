package com.gojava.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品服务 Feign 客户端。
 *
 * 本地开发：url 写死直连 http://127.0.0.1:8083。
 * 接 Nacos 后：删掉 url 属性，name="product-service" 即从注册中心发现实例。
 */
@FeignClient(name = "product-service", url = "${product.base-url}")
public interface ProductClient {

    @GetMapping("/internal/product/{id}")
    Map<String, Object> getProduct(@PathVariable("id") Long id);

    @PostMapping("/internal/product/{id}/deduct")
    Map<String, Object> deductStock(@PathVariable("id") Long id,
                                    @RequestParam("qty") int qty);
}
