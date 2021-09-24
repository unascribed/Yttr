package com.unascribed.yttr.init;

import com.unascribed.yttr.network.MessageC2SAttack;
import com.unascribed.yttr.network.MessageC2SDivePos;
import com.unascribed.yttr.network.MessageC2SDiveTo;
import com.unascribed.yttr.network.MessageC2SRifleMode;
import com.unascribed.yttr.network.MessageC2SShifterMode;
import com.unascribed.yttr.network.MessageS2CAnimateFastDive;
import com.unascribed.yttr.network.MessageS2CBeam;
import com.unascribed.yttr.network.MessageS2CDiscoveredGeyser;
import com.unascribed.yttr.network.MessageS2CDive;
import com.unascribed.yttr.network.MessageS2CDiveEnd;
import com.unascribed.yttr.network.MessageS2CDiveError;
import com.unascribed.yttr.network.MessageS2CDivePos;
import com.unascribed.yttr.network.MessageS2CDivePressure;
import com.unascribed.yttr.network.MessageS2CEffectorHole;
import com.unascribed.yttr.network.MessageS2CVoidBall;
import com.unascribed.yttr.network.concrete.NetworkContext;

public class YNetwork {

	public static final NetworkContext CONTEXT = NetworkContext.forChannel("yttr:main");
	
	public static void init() {
		CONTEXT.register(MessageC2SAttack.class);
		CONTEXT.register(MessageC2SDivePos.class);
		CONTEXT.register(MessageC2SDiveTo.class);
		CONTEXT.register(MessageS2CAnimateFastDive.class);
		CONTEXT.register(MessageS2CBeam.class);
		CONTEXT.register(MessageS2CDiscoveredGeyser.class);
		CONTEXT.register(MessageS2CDive.class);
		CONTEXT.register(MessageS2CDiveEnd.class);
		CONTEXT.register(MessageS2CDiveError.class);
		CONTEXT.register(MessageS2CDivePos.class);
		CONTEXT.register(MessageS2CDivePressure.class);
		CONTEXT.register(MessageS2CEffectorHole.class);
		CONTEXT.register(MessageS2CVoidBall.class);
		CONTEXT.register(MessageC2SRifleMode.class);
		CONTEXT.register(MessageC2SShifterMode.class);
	}
	
}
