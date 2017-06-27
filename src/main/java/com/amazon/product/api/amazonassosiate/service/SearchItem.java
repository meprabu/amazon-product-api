package com.amazon.product.api.amazonassosiate.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.amazon.product.api.amazonassosiate.generated.Item;
import com.amazon.product.api.amazonassosiate.generated.Items;

@Service
public interface SearchItem {
	public List<Items> searchItemByKeyword(String keyword);
	public Item searchById(String idType, String id);
}


