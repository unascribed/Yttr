<img class="infobox" src="../img/item/chute.png">

<span class="aside">Namespaced ID: <span>yttr:chute</span></span><br/>
# Chute
The Chute is a simple vertical tube. Items that find themselves on its top will fall through to its
bottom, as will items inserted via dropper, hopper, pipes from another mod, etc.

Items falling out of a chute always drop in the exact same place with no velocity in the exact
center of the block. This can be quite useful.

Right-clicking a Chute with an iron ingot gives it a plate, which is useful to stop items from
getting caught in the gap between the chute and the edge of the next block, e.g. in water streams.

Chutes do not have any internal inventory. A clogged chute will simply not accept items. You can
use this to e.g. shut off a chute drop with a sticky piston attached to a solid block.
Additionally, items fall through chutes instantly. (Let's pretend it's because the inside is coated
with something extremely slick...)

Chutes may also be used in tandem with a [Levitation Chamber](../levitation_chamber) to send items
upward.

A stair may be placed at the end of a chute run to cause items to fall in the direction of the
stair. Items spawned in a stair will have a very small and completely consistent amount of velocity
just barely sufficient to allow them to leave the block space of the stair.

## Videos

### Using a chute as an item passthrough
<video src="../img/chute_passthru.mp4" controls></video>

### Using a chute as a dropper aligner
<video src="../img/chute_aligner.mp4" controls></video>

### Using stairs to redirect chutes
<video src="../img/chute_stairs.mp4" controls></video>

## Recipes

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Chute
<div class="recipe" title="Namespaced ID: yttr:chute">
	<div class="output">
		<img title="Chute" src="../img/item/chute.png"/>
		<span class="quantity">8</span>
	</div>
	<div class="input">
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Nugget" src="../img/item/yttrium_nugget.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Iron_Ingot"><img title="Iron Ingot" src="../img/item/iron_ingot.png"/></a>
	</div>
</div>

## Recipe Usages

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Flopper
<div class="recipe" title="Namespaced ID: yttr:flopper">
	<a href="../flopper" class="output">
		<img title="Flopper" src="../img/item/flopper.png"/>
	</a>
	<div class="input small">
		<a href="https://minecraft.fandom.com/wiki/Hopper"><img title="Hopper" src="../img/item/hopper.png"/></a>
		<div class="blank"></div>
		<a href="#"><img title="Chute" src="../img/item/chute.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Dopper
<div class="recipe" title="Namespaced ID: yttr:dopper">
	<a href="../dopper" class="output">
		<img title="Dopper" src="../img/item/dopper.png"/>
	</a>
	<div class="input small">
		<a href="https://minecraft.fandom.com/wiki/Hopper"><img title="Hopper" src="../img/item/hopper.png"/></a>
		<a href="#"><img title="Chute" src="../img/item/chute.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Suit Station
<div class="recipe" title="Namespaced ID: yttr:suit_station">
	<a href="../diving" class="output">
		<img title="Suit Station" src="../img/item/suit_station.png"/>
	</a>
	<div class="input">
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="#"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="#"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="#"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Furnace"><img title="Furnace" src="../img/item/furnace.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
	</div>
</div>
