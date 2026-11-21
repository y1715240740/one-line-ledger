package io.github.yang.OLL.service;

import java.math.BigDecimal;

public record WeeklyReport(BigDecimal total, BigDecimal lastTotal,
                           String topCategory, BigDecimal topAmount) {
}
