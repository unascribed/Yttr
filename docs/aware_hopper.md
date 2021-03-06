<img class="infobox" src="../img/item/aware_hopper.png">

<span class="aside">Namespaced ID: <span>yttr:aware_hopper</span></span><br/>
# Aware Hopper
The Aware Hopper is a horrifying abomination, consisting of an enchanted Enderman and Zombie bound
together along with a Hopper and Crafting Table, that happens to be able to craft items.

Should you make this thing and place it in the world, you'll notice it watches you when you are
close to it. <s>It is questioning why you have done this and begg-</s> It is watching you, waiting
for you to craft something. If you do craft something, green particles will appear around its head
indicating it has learned the recipe.

Giving the Aware Hopper all the necessary ingredients for the learned recipe will result in it
crafting it, and outputting it to the side it is pointed at. Remainder items can be pulled out of 
any side of the Aware Hopper that isn't the top or the side its output is pointing. The vanilla way 
is to point the Aware Hopper to the side and put a normal Hopper underneath it; alternatively, you
can use another mod's pipes or Yttr's own [Flopper](../flopper) to extract from a non-output side.

*(0.4.0)*
Remainder items that are still valid inputs for their slot will remain in the crafting grid instead
of being kicked out to the remainder buffer to be piped out. This means that recipes involving
tools that take damage, or similar situations, don't need a loop piping remainder items back into
the Aware Hopper.

If the Aware Hopper learns a new recipe while there are still items in its buffer, it will dump the
remaining inputs into an alternate storage that can be extracted from, in order to avoid clogging
the buffer.

For more control over which Aware Hoppers can see you <s>and to silence their horrid tortured
noises</s>, you can right-click while holding a Carved Pumpkin. Aware Hoppers wearing a pumpkin
won't learn new recipes or watch nearby players.

An Aware Hopper with a solid block in its head location cannot craft things <s>as it is busy
suffocating to death but it cannot die for it is enchanted</s>, which may be useful if you cannot
control your item input.

Once an Aware Hopper has all necessary inputs to craft something, it will take 5 seconds to craft,
followed by a 1 second "rest" if it still has enough ingredients. It cannot craft while there is
anything in its output slot, so make sure to extract the output or point it at a chest.

Aware Hoppers make an attempt to round-robin items inserted into them to ensure recipes can be
crafted without needing to buffer full stacks, but it will not split input stacks. For example, if
your input is a Skeletal Sorter, input items will likely come in groups of 8, so the Aware Hopper
will insert the entire 8 stack into its first round-robin slot, meaning it won't seem to craft
anything until it receives 8x what it needs, but it will then craft 8 items. To avoid this, you can
send your inputs through a Hopper or Levitation Chamber to split the stack up into singles.

## Video
<video src="../img/aware_hopper.mp4" controls></video>

## Recipe

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Aware Hopper
<div class="recipe horrible" title="Namespaced ID: yttr:aware_hopper">
	<a href="#" class="output">
		<img title="Aware Hopper" src="../img/item/aware_hopper.png"/>
	</a>
	<div class="input">
		<a href="../snare"><img title="Snare (Zombie)" src="../img/item/snare_zombie.png"/></a>
		<a href="../shears"><img title="Surgical Shears" src="../img/item/shears.png"/></a>
		<a href="../snare"><img title="Snare (Enderman)" src="../img/item/snare_enderman.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Lapis_Lazuli"><img title="Lapis Lazuli" src="../img/item/lapis_lazuli.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Crafting_Table"><img title="Crafting Table" src="../img/item/crafting_table.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Lapis_Lazuli"><img title="Lapis Lazuli" src="../img/item/lapis_lazuli.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Hopper"><img title="Hopper" src="../img/item/hopper.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
	</div>
</div>
The snares contain a Zombie and an Enderman.

## Why?
The inner machinations of my mind are an enigma.
