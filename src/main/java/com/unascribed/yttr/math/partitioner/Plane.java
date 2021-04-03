package com.unascribed.yttr.math.partitioner;

import java.util.List;

import net.minecraft.util.math.Vec3d;

//Gems V: Spatial Partitioning of a Polygon by a Plane
//by George Vanecek Jr, Sept. 1994
//https://github.com/erich666/GraphicsGems/tree/9632659c0e3592d8cecf8866fcc34498a85c8d22/gemsv/ch7-4
public class Plane {
	// Provide a minimal 3D plane support sufficine for the Gem.
	//
	// Any point p that is known topologically to lie on a plane pN+d~0
	// is included in the plane by enlarging the epsilon, so the
	// equation |pN+d| <= eps holds.  The point/plane relationship must
	// be established by the application code.

	private Vec3d n;			// unit normal vector
	private double d;			// shortest distance from origin
	private double eps;			// point/plane distance epsilon

	public Plane(List<Vec3d> points) {
		if (points.size() <= 2) throw new IllegalArgumentException();
		n = new Vec3d(0, 0, 0);
		Vec3d avrPnt = new Vec3d(0,0,0);
		for( int i = 0; i < points.size(); ++i ) {
			avrPnt.add(points.get(i));
			n = n.add(points.get(i).crossProduct(points.get((i+1) % points.size())));
		}

		n = n.normalize();
		d = normal().dotProduct(avrPnt.multiply(-1.0 / points.size()));

		for( int i = 0; i < points.size(); ++i )
			updateEpsilon( points.get(i) );
	}

	// Compute the intersection point with the transversal line (p,q).
	Vec3d onPoint( Vec3d p, Vec3d q )
	{
		Vec3d v = q.subtract(p);
		double c = normal().dotProduct(v);
		if (c == 0.0) throw new IllegalArgumentException();
		double t = -sDistance(p) / c;
		return p.add(v.multiply(t));
	}

	void updateEpsilon ( Vec3d p )
	{
		double d = sDistance(p);
		if( d < 0.0 )
			d = -d;
		if( d > eps)
			eps = d;
	}
	
	public Plane(Vec3d v, double x) {
		n = v;
		d = x;
		eps = 0;
	}
	
	public Plane(Plane p) {
		n = p.n;
		d = p.d;
		eps = p.eps;
	}
	
	Vec3d normal( ) { return n; }

	// Signed distance from the point to the plane.
	double      sDistance( Vec3d p ) { return p.dotProduct(n) + d; }

	public Where whichSide(Vec3d p)
	{ double d = sDistance( p );
	return d < -eps ? Where.BELOW : (d > eps ? Where.ABOVE : Where.ON);
	}
}
