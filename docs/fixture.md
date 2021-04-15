<img class="infobox" src="../img/item/fixture_cycle_all.png">

<span class="aside">Namespaced ID: <span>yttr:fixture</span></span><br/>
# Fixture

Fixtures are smaller versions of [Lamps](/lamp) which can be placed on any of the 6 block faces.

See the [Lamp](/lamp) page for more information on how lamps work, including these.

## Screenshots
![A few fixtures hanging around.](../img/fixture.png)
 
## Recipes

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/>  Split Lamp
<div class="recipe" title="Namespaced ID: yttr:fixtures_from_lamp">
	<a href="#" class="output">
		<img title="Fixture" src="../img/item/fixture_cycle_all.png"/>
		<span class="quantity">4</span>
	</a>
	<div class="input">
		<a href="../lamp"><img title="Lamp" src="../img/item/lamp_cycle_all.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/>  Cage Lamp to Fixture
<div class="recipe" title="Namespaced ID: yttr:fixture_from_cage_lamp">
	<a href="#" class="output">
		<img title="Fixture" src="../img/item/fixture_cycle_all.png"/>
	</a>
	<div class="input">
		<a href="../cage_lamp"><img title="Cage Lamp" src="../img/item/cage_lamp_cycle_all.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/>  Inversion

<div class="recipe" title="Namespaced ID: yttr:fixture_invert">
	<a href="#" class="output">
		<img title="Fixture" src="../img/item/fixture_cycle.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Inverted Fixture" src="../img/item/fixture_inverted_cycle.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Redstone_Torch"><img title="Redstone Torch" src="../img/item/redstone_torch.png"/></a>
	</div>
</div>

<div class="recipe" title="Namespaced ID: yttr:fixture_invert">
	<a href="#" class="output">
		<img title="Inverted Fixture" src="../img/item/fixture_inverted_cycle.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Redstone_Torch"><img title="Redstone Torch" src="../img/item/redstone_torch.png"/></a>
	</div>
</div>

## Recipe Usages

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/>  Fixture to Cage Lamp
<div class="recipe" title="Namespaced ID: yttr:cage_lamp_from_fixture">
	<a href="../cage_lamp" class="output">
		<img title="Cage Lamp" src="../img/item/cage_lamp_cycle_all.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle_all.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/>  Recombine Fixtures
<div class="recipe" title="Namespaced ID: yttr:fixtures_to_lamp">
	<a href="../lamp" class="output">
		<img title="Lamp" src="../img/item/lamp_cycle_all.png"/>
	</a>
	<div class="input small">
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle_all.png"/></a>
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle_all.png"/></a>
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle_all.png"/></a>
		<a href="#"><img title="Fixture" src="../img/item/fixture_cycle_all.png"/></a>
	</div>
</div>

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Helmet
<div class="recipe" title="Namespaced ID: yttr:suit_helmet">
	<a href="../diving" class="output">
		<img title="Diving Helmet" src="../img/item/suit_helmet.png"/>
	</a>
	<div class="input">
		<a href="../diving"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="../diving"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="../diving"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="../diving"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="../glassy_void"><img title="Glassy Void" src="../img/item/glassy_void.png"/></a>
		<a href="../diving"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="[any fixture]" src="../img/item/fixture_cycle_all.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
	</div>
</div>
The color of the fixture decides the color of the HUD.
