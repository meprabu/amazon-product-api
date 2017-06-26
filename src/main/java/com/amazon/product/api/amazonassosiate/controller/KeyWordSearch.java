package com.amazon.product.api.amazonassosiate.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazon.product.api.amazonassosiate.generated.Item;
import com.amazon.product.api.amazonassosiate.generated.Items;
import com.amazon.product.api.amazonassosiate.service.SearchItem;

@RestController("/search")
public class KeyWordSearch {
	
	@Autowired
	SearchItem searchItem;
	
	@RequestMapping("/search/{keyword}")
	public List<Item> getItemsByKeyWord(@PathVariable String keyword){
		
		List<Items>  itemsList = searchItem.searchItemByKeyword(keyword);
		
		List<Item> itemList = new ArrayList<Item>();
		
		for(Items items: itemsList){
			for (Item item: items.getItem()) {
				itemList.add(item);
			}
		}
		
		return itemList;
	}

}
