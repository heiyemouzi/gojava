package com.gojava.product.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gojava.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    default List<Product> listOnShelf(String tenantId) {
        return selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getStatus, 1)
                .orderByAsc(Product::getId));
    }

    /** 悲观锁读（供内部接口用） */
    @Select("select * from product where id = #{id} for update")
    Product getForUpdate(Long id);

    /** 条件 UPDATE 扣库存，影响行数=0 即库存不足 */
    @Update("update product set stock = stock - #{qty} where id = #{id} and stock >= #{qty} and status = 1")
    int deductStock(Long id, int qty);
}
