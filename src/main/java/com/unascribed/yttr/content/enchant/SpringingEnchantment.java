package com.unascribed.yttr.content.enchant;

public class SpringingEnchantment extends CoilEnchantment {

	public SpringingEnchantment() {
		super(Rarity.RARE);
	}

	@Override
	public int getMinPower(int level) {
		return 10 * level;
	}

	@Override
	public int getMaxPower(int level) {
		return getMinPower(level) + 30;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

}
