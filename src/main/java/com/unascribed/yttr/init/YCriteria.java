package com.unascribed.yttr.init;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.minecraft.advancement.criterion.Criterion;
import com.unascribed.yttr.BasicCriterion;
import com.unascribed.yttr.BlockCriterion;
import com.unascribed.yttr.Yttr;

public class YCriteria {

	public static final BlockCriterion BROKE_BLOCK = new BlockCriterion("yttr:broke_block");
	public static final BlockCriterion EFFECT_BLOCK = new BlockCriterion("yttr:effect_block");
	public static final BlockCriterion CLEAVE_BLOCK = new BlockCriterion("yttr:cleave_block");
	public static final BlockCriterion OPEN_GEYSER = new BlockCriterion("yttr:open_geyser");
	public static final BlockCriterion BREAK_BEDROCK = new BlockCriterion("yttr:break_bedrock");
	public static final BlockCriterion SHIFT_BLOCK = new BlockCriterion("yttr:shift_block");
	public static final BlockCriterion NAME_GEYSER = new BlockCriterion("yttr:name_geyser");

	public static final BasicCriterion RIFLE_SCOPE = new BasicCriterion("yttr:rifle_scope");
	public static final BasicCriterion BURN_DROP_OF_CONTINUITY = new BasicCriterion("yttr:burn_drop_of_continuity");
	public static final BasicCriterion PROJECT = new BasicCriterion("yttr:project");
	public static final BasicCriterion PROJECT_WITH_LONG_FALL = new BasicCriterion("yttr:project_with_long_fall");
	public static final BasicCriterion SNARE_BLOCK_ENTITY = new BasicCriterion("yttr:snare_block_entity");
	public static final BasicCriterion SNARE_LIVING_ENTITY = new BasicCriterion("yttr:snare_living_entity");
	public static final BasicCriterion KILL_WITH_EFFECTOR = new BasicCriterion("yttr:kill_with_effector");
	public static final BasicCriterion SQUEEZE_LEAVES = new BasicCriterion("yttr:squeeze_leaves");
	public static final BasicCriterion DIVE = new BasicCriterion("yttr:dive");
	public static final BasicCriterion SHOOT_SOMETHING_FAR_AWAY = new BasicCriterion("yttr:shoot_something_far_away");
	
	public static void init() {
		Yttr.autoRegister(CriterionRegistry::register, YCriteria.class, Criterion.class);
	}
	
}
