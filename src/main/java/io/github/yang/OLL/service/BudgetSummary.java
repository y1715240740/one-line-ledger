package io.github.yang.OLL.service;

import java.math.BigDecimal;

public record BudgetSummary(BigDecimal spent, BigDecimal remaining, BigDecimal daily) {
}
//record 只是“写类”的一种简写方式。