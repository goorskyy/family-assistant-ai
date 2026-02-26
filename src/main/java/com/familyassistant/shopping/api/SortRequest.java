package com.familyassistant.shopping.api;

import com.familyassistant.shopping.domain.ShoppingItem;

import java.util.List;

public record SortRequest(List<ShoppingItem> items, String storeProfile) {}
