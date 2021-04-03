package com.unascribed.yttr.math.partitioner;

import net.minecraft.util.math.Vec3d;

//Gems V: Spatial Partitioning of a Polygon by a Plane
//by George Vanecek Jr, Sept. 1994
//https://github.com/erich666/GraphicsGems/tree/9632659c0e3592d8cecf8866fcc34498a85c8d22/gemsv/ch7-4
public class DEdge {

	DEdge      nxt;		// Next DEdge on cycle
	DEdge      prv;		// Previous DEdge on cycle
	Vec3d sP;		// Source Point
	Where       sPW;		// Where is Source Point?
	double      t;		// Related to sP. Used in complexCut(...)
	
	public DEdge(Vec3d srcP) {
		nxt = this;
		prv = this;
		sP = srcP;
		sPW = Where.NOWHERE;
	}
	
	public DEdge(Vec3d srcP, DEdge last) {
		nxt = null;
		sP = srcP;
		sPW = Where.NOWHERE;
		last.nxt = this;
		prv = last;
	}
	public static void closeCycle( DEdge first, DEdge last )
	{
		first.prv = last;
		last.nxt  = first;
	}

	void split( Vec3d p )
	{
		DEdge n = next();
		DEdge d = new DEdge( p, this );
		closeCycle( n, d );
	}

	public DEdge           next( ) { return nxt; }
	public DEdge           prev( ) { return prv; }
	public Vec3d srcPoint( ) { return sP; }
	public Vec3d dstPoint( ) { return nxt.sP; }
	public Where       srcWhere( ) { return sPW; }
	public Where       dstWhere( ) { return nxt.sPW; }
	public Where           where( ) { return sPW.or(nxt.sPW); }
	public double  distFromRefP( ) { return t; }
	
	public double  distFromRefP( double v ) { return t = v; }
	public Where       srcWhere( Where v ) { return sPW = v; }
	public Where       dstWhere( Where v ) { return nxt.sPW = v; }

}
