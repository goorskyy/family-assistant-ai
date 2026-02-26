package com.familyassistant.shopping.api;

import com.familyassistant.shopping.domain.ShoppingItem;

import java.util.List;

public record SortResponse(List<ShoppingItem> sortedItems) {}
