<img class="infobox" src="../img/item/skeletal_sorter.png">

<span class="aside">Namespaced ID: <span>yttr:skeletal_sorter</span></span><br/>
# Skeletal Sorter *(0.4.0)*
The Skeletal Sorter is a slightly-less-horrifying abomination than the Aware Hopper. It can separate
a certain item out of an unsorted input, including items that cannot be stacked.

In order to work, the Sorter must have three inventories available. One in front of it, one to its
left, and one to its right. Additionally, it needs to know *what* to filter, by putting an item
frame on the same level as its head, one or two blocks in front of it. The item frame must be visible.

If any of these conditions are not met, the Sorter will not work. (This means you can pause sorting
by pistoning a solid block in the way of the item frame's sight line.)

If the Skeleton used to craft the Sorter is right-handed (95% of skeletons), then items that match
the one in the item frame will be placed in the right chest, and anything that doesn't match will
be placed into the left chest. For left-handed skeletons, this is inverted, with matches going into
the left chest.

Every sorting operation can move up to 8 items.

## Screenshots
![](../img/sorter.png)

An example right-handed sorter. Any diamonds found in the open chest will be placed into the chest
marked in green (the right chest), and anything else found in the open chest will be placed into
the chest marked in red (the left chest).

## Video
<video src="../img/sorter.mp4" controls></video>

## Recipe

### <img class="symbolic" title="Crafting Table" src="../img/symbolic/crafting_table.png"/> Skeletal Sorter
<div class="recipe horrible" title="Namespaced ID: yttr:skeletal_sorter_left_handed &amp; yttr:skeletal_sorter_right_handed">
	<a href="#" class="output">
		<img title="Skeletal Sorter" src="../img/item/skeletal_sorter.png"/>
	</a>
	<div class="input small">
		<a href="../snare"><img title="Snare (Skeleton)" src="../img/item/snare_skeleton.png"/></a>
		<div class="blank"></div>
		<a href="../table"><img title="Table" src="../img/item/table.png"/></a>
	</div>
</div>

The snare contains a Skeleton. The skeleton's handedness (right or left) will be remembered.
