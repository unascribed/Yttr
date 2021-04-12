package com.unascribed.yttr;

import java.util.Set;
import java.util.UUID;

public interface DiverPlayer {

	boolean yttr$isDiving();
	void yttr$setDiving(boolean b);
	
	boolean yttr$isInvisibleFromDiving();
	boolean yttr$isNoGravityFromDiving();
	
	Set<UUID> yttr$getKnownGeysers();
	
}
