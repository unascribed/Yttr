package com.unascribed.yttr.init;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class YTrades {

	public static void init() {
		TradeOffers.Factory[] clericOffers = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CLERIC).get(2);
		clericOffers = ArrayUtils.add(clericOffers, new TradeOffers.SellItemFactory(YItems.QUICKSILVER, 8, 1, 2));
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CLERIC).put(2, clericOffers);
	}
	
}
