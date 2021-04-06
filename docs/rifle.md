<img class="infobox" src="../img/item/rifle.png">

<span class="aside">Namespaced ID: <span>yttr:rifle</span></span>
# Yttric Rifle
The Yttric Rifle is a high-tech weapon that is sort of like a cross between a centrifuge and a
railgun. Given some kinds of dusts, it can shoot various beams with varying effects.

It has multiple modes, which can be cycled through by left-clicking. Sneaking reverses the mode
cycle order. Switching modes empties the rifle's internal buffer, wasting whatever shots may have
been left. For example, if you load Redstone Dust, shoot it once, and then switch modes, you'll
waste 11 shots.

Shot power on the Yttric Rifle is measured in kilojoules. You can measure this in-game and get a
feel for the timing on the rifle via the [Power Meter](/power_meter). The maximum power for a
"normal" shot is 500kJ, and a perfectly timed overcharged shot is 650kJ.

Charging the rifle for too long will cause it to backfire.

## Firing Modes

### Beam
The primary firing mode. Consumes Redstone Dust, which is worth 12 shots each. Deals damage
depending on how long it's charged and has no secondary effects. The minimum damage it can deal is
½ a heart at 50kJ, and it maxes out at 7 hearts for a 500kJ shot. Overcharged shots above 600kJ
cause explosions, and a direct hit from a perfectly charged 650kJ shot deals 9½ hearts.

This is the only firing mode safe enough to be measured by the [Power Meter](/power_meter).

This mode takes 3½ seconds to backfire.

### Explode
This mode consumes Gunpowder, worth 1 shot each. Explosion power starts at 0.3 for 50kJ, and maxes
out at 3 for 500kJ. 600kJ shots or higher cause an explosion with a power of 5, and are strong
enough to activate a [Bedrock Smasher](/bedrock_smasher)

This mode takes 7 seconds to backfire.

### Teleport
This mode consumes Chorus Fruit, worth 3 shots each. Firing at least a 400kJ shot will teleport you
to where the shot hit. As the rifle is a laser weapon, this gives you extremely high accuracy long
range teleportation.

A shot of 550kJ or above creates an explosion where you were, damaging anything that may have been
nearby. A shot of 600kJ or above creates an explosion both where you were and where you teleport
to, hurting you.

This mode takes 4⅔ seconds to backfire.

### Fire
Fire mode is similar to Beam mode, but additionally lights things on fire and deals less instant
damage. Consumes Blaze Powder, worth 2 shots each. The minimum damage it can deal is less than half
a heart with 1 second of fire at 50kJ, and it maxes out at 3 hearts with 10 seconds of fire for a
500kJ shot. Overcharged shots above 600kJ cause explosions, and a direct hit
from a perfectly charged 650kJ shot deals 4 hearts and 13 seconds of fire.

If wool or a cobweb is hit with a Fire shot above 250kJ, the block will be instantly broken. An
overcharged shot will break all wool or cobwebs in a 3x3x3.

This mode takes 3½ seconds to backfire.

### Void
Void mode is extremely destructive and dangerous, but can be used to clear land for building. It
consumes Void Buckets, worth 1 shot each. A Void Ball is created where the shot lands, deleting all
blocks in a radius dependent on the shot power. 50kJ isn't enough for even one block, and 500kJ is
an 8 block radius. Overcharged shots can get a 9 block radius, and a backfire creates a 12 radius
ball where you're standing.

Due to its extreme destructive power and griefing potential, Void shots are logged to the console
and can be undone by admins with the `/yttr:void_undo` command.

This mode takes 9⅓ seconds to backfire.

## Upgrades
There are two available upgrades to the Yttric Rifle.

<span class="aside">Namespaced ID: <span>yttr:rifle_reinforced</span></span>
### Bedrock-Reinforced Rifle

The Bedrock-Reinforced Rifle fires 15% slower, but its increased stability grants it a much simpler
and more predictable power curve, visible below. However, its stability also means it cannot make
650kJ shots and tops out at 620kJ.

#### <img class="symbolic" title="Smithing Table" src="../img/symbolic/smithing_table.png"/> Recipe
<div class="recipe" title="Namespaced ID: yttr:rifle_reinforced">
	<a href="#" class="output">
		<img title="Bedrock-Reinforced Rifle" src="../img/item/rifle_reinforced.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Yttric Rifle" src="../img/item/rifle.png"/></a>
		<div class="add"></div>
		<a href="/bedrock_shard"><img title="Bedrock Shard" src="../img/item/bedrock_shard.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:rifle_overclocked</span></span>
### Void-Enhanced Rifle

Using Void to dissolve chambered dusts increases the firing rate of the rifle by 65%, but causes
double ammo usage from losing some of the loaded dust.

#### <img class="symbolic" title="Smithing Table" src="../img/symbolic/smithing_table.png"/> Recipe
<div class="recipe" title="Namespaced ID: yttr:rifle_overclocked">
	<a href="#" class="output">
		<img title="Void-Enhanced Rifle" src="../img/item/rifle_overclocked.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Yttric Rifle" src="../img/item/rifle.png"/></a>
		<div class="add"></div>
		<a href="/glassy_void"><img title="Glassy Void" src="../img/item/glassy_void.png"/></a>
	</div>
</div>

## Power Curves

### Yttric Rifle
![](/img/rifle_power.png)

### Bedrock-Reinforced Rifle
![](/img/rifle_power_reinforced.png)

### Void-Enhanced Rifle
![](/img/rifle_power_overclocked.png)
