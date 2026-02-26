package com.familyassistant.shopping;

import java.util.List;

public record SortRequest(List<ShoppingItem> items, String storeProfile) {}
