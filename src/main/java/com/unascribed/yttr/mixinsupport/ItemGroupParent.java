package com.unascribed.yttr.mixinsupport;

import java.util.List;

import com.unascribed.yttr.ItemSubGroup;

public interface ItemGroupParent {

	List<ItemSubGroup> yttr$getChildren();
	ItemSubGroup yttr$getSelectedChild();
	void yttr$setSelectedChild(ItemSubGroup group);
	
}
