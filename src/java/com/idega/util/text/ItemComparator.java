package com.idega.util.text;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class ItemComparator implements Comparator<Item> {

	private Collator collator;
	
	public ItemComparator(Locale locale) {
		collator = Collator.getInstance(locale);
	}
	
	@Override
	public int compare(Item item1, Item item2) {
		return collator.compare(item1.getItemLabel(), item2.getItemLabel());
	}

}