package io.github.yang.OLL.service;

import io.github.yang.OLL.entity.Bill;
import io.github.yang.OLL.mapper.BillMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BillService {

    private final BillMapper billMapper;

    public BillService(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    // 从一句话里解析并入库，解析不出来返回 null
    public Bill insertFromText(String text, long userId) {
        if (text == null) return null;

        String cleaned = text.replaceAll("\\[CQ:[^\\]]*\\]", "").trim();
        if (cleaned.isEmpty()) return null;

// 1. 取第一个数字当金额
        Matcher m = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(cleaned);
        if (!m.find()) return null;
        BigDecimal amount = new BigDecimal(m.group(1));

        if (amount.compareTo(new BigDecimal("100000")) > 0) return null;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return null;

// 2. 猜分类
        String category = guessCategory(cleaned);

// 3. 组装并入库
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setAmount(amount);
        bill.setCategory(category);
        bill.setNote(cleaned);
        bill.setSource("chat");
        bill.setOccurredAt(LocalDateTime.now());

        billMapper.insert(bill);
        return bill;
    }

    private String guessCategory(String text) {
        if (text.matches(".*(饭|吃|外卖|奶茶|咖啡|早餐|午餐|晚餐|食堂).*")) return "餐饮";
        if (text.matches(".*(打车|地铁|公交|高铁|机票|加油).*")) return "交通";
        if (text.matches(".*(买|购物|衣服|鞋|日用|超市).*")) return "购物";
        if (text.matches(".*(电影|游戏|唱歌|演出|娱乐|会员).*")) return "娱乐";
        return "其他";
    }
}
