package io.github.yang.OLL.service;

import io.github.yang.OLL.entity.Bill;
import io.github.yang.OLL.mapper.BillMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baomidou.mybatisplus.extension.toolkit.Db.save;

@Service
public class BillService {

    private final BillMapper billMapper;
    private final AIService aiService;

    public BillService(BillMapper billMapper, AIService aiService) {
        this.billMapper = billMapper;
        this.aiService = aiService;
    }

    public Bill insertByAI(String text, long userId) {
        if(text == null) return null;

        String cleaned = text.replaceAll("\\[CQ:[^\\]]*\\]", "").trim();
        if (cleaned.isEmpty()) return null;

        try {
            Map<String, Object> r = aiService.parseBill(cleaned);

            if (Boolean.TRUE.equals(r.get("isBill"))) {
                BigDecimal amount = (BigDecimal) r.get("amount");

                if (amount != null
                        && amount.compareTo(BigDecimal.ZERO) > 0
                        && amount.compareTo(new BigDecimal("100000")) <= 0) {
                    return save(userId, amount, (String) r.get("category"), cleaned);
                }
            }

            return null; // AI 明确说不是账单

        } catch (Exception e) {
            System.out.println("[调试] 异常类型：" + e.getClass().getName());
            System.out.println("AI 解析失败，回退正则：" + e.getMessage());
        }

// 2. AI 挂了，走正则
        return insertByRule(cleaned, userId);
    }

    // 保底分类方案：从一句话里解析并入库，解析不出来返回 null
    public Bill insertByRule(String text, long userId) {
        System.out.println("[调试] 进入 insertByRule：" + text);
        if (text == null) return null;

// 1. 取第一个数字当金额
        Matcher m = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(text);
        if (!m.find()) return null;
        BigDecimal amount = new BigDecimal(m.group(1));

        if (amount.compareTo(new BigDecimal("100000")) > 0) return null;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return null;

// 2. 猜分类
        String category = guessCategory(text);
// 3. 组装并入库
        return save(userId, amount, category, text);
    }

    private String guessCategory(String text) {
        if (text.matches(".*(饭|吃|外卖|奶茶|咖啡|早餐|午餐|晚餐|食堂).*")) return "餐饮";
        if (text.matches(".*(打车|地铁|公交|高铁|机票|加油).*")) return "交通";
        if (text.matches(".*(买|购物|衣服|鞋|日用|超市).*")) return "购物";
        if (text.matches(".*(电影|游戏|唱歌|演出|娱乐|会员).*")) return "娱乐";
        return "其他";
    }

    private Bill save(long userId, BigDecimal amount, String category, String note) {
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setAmount(amount);
        bill.setCategory(category);
        bill.setNote(note);
        bill.setSource("chat");
        bill.setOccurredAt(LocalDateTime.now());
        billMapper.insert(bill);
        return bill;
    }
}
