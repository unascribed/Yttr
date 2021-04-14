package com.unascribed.yttr.util.math.partitioner;

//Gems V: Spatial Partitioning of a Polygon by a Plane
//by George Vanecek Jr, Sept. 1994
// https://github.com/erich666/GraphicsGems/tree/9632659c0e3592d8cecf8866fcc34498a85c8d22/gemsv/ch7-4
public enum Where { // Point/Plane Classification
	NOWHERE,
	ABOVE,
	ON,
	ONABOVE, // ON | ABOVE
	BELOW,
	ABOVEBELOW, // ABOVE | BELOW
	ONBELOW, // ON | BELOW
	CROSS, // ABOVE | ON | BELOW
	;
	
	private static final Where[] VALUES = values();
	
	public Where or(Where that) {
		return VALUES[this.ordinal() | that.ordinal()];
	}
}
