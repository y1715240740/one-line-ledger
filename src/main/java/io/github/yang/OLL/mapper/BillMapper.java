package io.github.yang.OLL.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.yang.OLL.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    //BillMapper 继承 BaseMapper<Bill>，获得对 bill 表的通用增删改查能力；
// 运行时由 MyBatis-Plus 生成实现，Spring 把它注入给 BillService，Service 就能直接用。
}