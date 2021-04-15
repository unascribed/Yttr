# Void Diving *(0.4.0)*

Void Diving is a form of fast travel between Void Geysers. With Void Diving, you can move up to
400m/s to any discovered location. You can also explore in the Void at 80m/s for geysers you've not
discovered yet.

In order to Dive, you need a complete Diving Suit that has been fueled and filled with
oxygen in a Suit Station. You'll also need an open Void Geyser with a Diving Plate placed within
range.

## Getting Fueled Up
There are three resources that need to be managed when Diving. Fuel, oxygen, and hull integrity.
Without fuel, you can only attempt to move under your own power in a bulky suit. Without oxygen,
you will asphyxiate. Without integrity, the Void will burst into your suit and dissolve you
instantly.

A newly crafted suit comes with no fuel, no oxygen, and full integrity. To refill fuel and oxygen,
the suit must be placed into a Suit Station.

![The Suit Station GUI.](../img/station1.png)

The right pane shows the current status of the suit placed in the station. The suit we've added
has no fuel, no oxygen, and full integrity, as it's been newly crafted. The orange dot on the
helmet indicates that it has an orange HUD.

Oxygen will slowly fill automatically, just by putting the suit into the station. Fuel is melted
glowstone, created by placing glowstone dust in the top left slot and any furnace fuel in the
bottom left. Integrity can be repaired by adding a metallurgical flux to the bottom right slot to
be used for welding. If the integrity is below 75%, you must additionally supply armor plates in
the top right slot; each armor plate can repair 25% integrity.

(Metallurgical fluxes are defined in the yttr:fluxes item tag. Yttr defines vanilla charcoal and
honeycomb as fluxes, but other mods or datapacks can add more.)

For convenience, your armor slots are visible to the left of your inventory. This area is preferred
when shift-clicking suit pieces, making unequipping and equipping the suit into/out of the station
easy.

The diving suit is extremely heavy and moving around in it on land is exceedingly difficult. It's
suggested you keep your suit station near your geyser and only put on the suit when ready to begin
a dive. If you can stand using it on land, however, it provides incredible protection.

## Claiming a Geyser
See [Bedrock Smasher](../bedrock_smasher) for how to open a Void Geyser.

Once you have a Void Geyser, you can place a Diving Plate near it to make Diving possible. If you
place a plate that has been renamed on an Anvil, the Geyser's name will change to the plate's name.

Standing on the plate, assuming it's close enough to the Geyser, will cause the suit HUD to show up,
letting you do a quick pre-dive check on the state of your suit.

![The HUD that shows before starting a dive.](../img/dive_hud.png)

## Diving

Should you be satisfied with the state of your suit, simply sneak for 2 seconds to Dive.

<video src="../img/dive.mp4" controls></video>

There are a number of on-screen instruments to help you get your bearings when in the suffocating
blackness of the Void. The gauges in the bottom right are self-explanatory. The bottom left shows
you distance from your target, and the pressure effectively indicates how close you are to a geyser.

In the center is the map. The very center is where you are, with directional indicators on every
cardinal side. The bottom right "1600M" indicator is showing the map scale; that bar's width is
equivalent to 1600M of distance.

When the Pressure is low, no integrity will be consumed; just oxygen. Higher pressures will use up
more integrity. Moving will use up Fuel, of course. You can move manually, using WASD, or you can
click on any visible geyser marker to quickly travel in a straight line there. Should you change
your mind about Diving, just click on the geyser you entered from that's overlapping your marker to
exit.

Hovering over a geyser will fill in parts of your resource bars with stripes, indicating how much
of each resource it will take to reach what you're hovering over.

<video src="../img/travel.mp4" controls></video>

If you emerge from a geyser that has a Diving Plate, you will appear right on top of the diving
plate. If the geyser you emerge from for whatever reason *doesn't* have one, you'll be launched out
of the geyser in a random direction. (Hope you brought your own.)

If any of your resources drop below 50%, the suit will begin alerting you by repeating "Danger"
every few seconds. As your resources drop further, the repetition of "Danger" will become more and
more frequent. Fuel and Oxygen are used quite quickly, so it's suggested you have a Suit Station
ready to refuel and refill oxygen with at every geyser you intend to travel between.

Should you not have enough resources to reach a destination, attempting to go there simply won't
work, telling you why at the top of the screen.

<video src="../img/danger.mp4" controls></video>

As such, if you let the suit do all the navigation instead of taking fate into your own hands, it's
pretty safe to Dive.

However, let's say there's a geyser you know the rough location of, but for one reason or another,
you can't just waltz in the front door and discover it? Then you can use WASD to manually
swim to where you think it is, use the Pressure meter to figure out how close you are, and once
you're within range, the geyser will magically appear on your map, as if it were always there. Keep
a close eye on your resources if you do that; after all, if you die in the Void, there's nobody to
save you, and no way to retrieve your items...

## Recipes

<span class="aside">Namespaced ID: <span>yttr:armor_plating</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Bedrock-Reinforced Armor Plating
<div class="recipe" title="Namespaced ID: yttr:armor_plating">
	<a href="#" class="output">
		<img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/>
		<span class="quantity">4</span>
	</a>
	<div class="input">
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../bedrock_shard"><img title="Bedrock Shard" src="../img/item/bedrock_shard.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:suit_station</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Suit Station
<div class="recipe" title="Namespaced ID: yttr:suit_station">
	<a href="#" class="output">
		<img title="Suit Station" src="../img/item/suit_station.png"/>
	</a>
	<div class="input">
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="../chute"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="../chute"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="../chute"><img title="Chute" src="../img/item/chute.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Furnace"><img title="Furnace" src="../img/item/furnace.png"/></a>
		<a href="../yttrium"><img title="Yttrium Ingot" src="../img/item/yttrium_ingot.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:suit_helmet</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Helmet
<div class="recipe" title="Namespaced ID: yttr:suit_helmet">
	<a href="#" class="output">
		<img title="Diving Helmet" src="../img/item/suit_helmet.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="../glassy_void"><img title="Glassy Void" src="../img/item/glassy_void.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="../fixture"><img title="[any fixture]" src="../img/item/fixture_cycle_all.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
	</div>
</div>
The color of the fixture decides the color of the HUD.

<span class="aside">Namespaced ID: <span>yttr:suit_chestplate</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Chestpiece
<div class="recipe" title="Namespaced ID: yttr:suit_chestplate">
	<a href="#" class="output">
		<img title="Diving Chestpiece" src="../img/item/suit_chestplate.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="https://minecraft.fandom.com/wiki/Compass"><img title="Compass" src="../img/item/compass.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:suit_leggings</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Leggings
<div class="recipe" title="Namespaced ID: yttr:suit_leggings">
	<a href="#" class="output">
		<img title="Diving Leggings" src="../img/item/suit_leggings.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:suit_boots</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Boots
<div class="recipe" title="Namespaced ID: yttr:suit_boots">
	<a href="#" class="output">
		<img title="Diving Boots" src="../img/item/suit_boots.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img src="../img/item/air.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
	</div>
</div>

<span class="aside">Namespaced ID: <span>yttr:diving_plate</span></span>
### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Diving Plate
<div class="recipe" title="Namespaced ID: yttr:diving_plate">
	<a href="#" class="output">
		<img title="Diving Plate" src="../img/item/diving_plate.png"/>
	</a>
	<div class="input">
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
		<a href="#"><img title="Bedrock-Reinforced Armor Plating" src="../img/item/armor_plating.png"/></a>
	</div>
</div>

