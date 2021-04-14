package com.unascribed.yttr.util.math.partitioner;

import static com.unascribed.yttr.util.math.partitioner.Where.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;

import net.minecraft.util.math.Vec3d;

//Gems V: Spatial Partitioning of a Polygon by a Plane
//by George Vanecek Jr, Sept. 1994
//https://github.com/erich666/GraphicsGems/tree/9632659c0e3592d8cecf8866fcc34498a85c8d22/gemsv/ch7-4
public class Polygon implements Iterable<DEdge> {

	private Plane supportPlane;
	private int     nDEdges;		// Number of DEdges in loop...
	private DEdge      anchor;		// Edge Loop

	public int    nPoints( ) { return nDEdges; }
	public Plane plane( ) { return supportPlane; }
	public DEdge       first( ) { return anchor; }

	// Iterate over all Directed Edges within a Polygon member function:
	public void forEachDEdge(Consumer<DEdge> cb) {
		DEdge last = null;
		for (DEdge d = first(); d != first() || last == null; last = d, d = d.next()) {
			cb.accept(d);
		}
	}

	// Iterate over all Directed Edges of a Polygon *g:
	public static void forEachDEdgeOfPoly(Polygon g, Consumer<DEdge> cb) {
		DEdge last = null;
		for (DEdge d = g.first(); d != g.first() || last == null; last = d, d = d.next()) {
			cb.accept(d);
		}
	}

	public Polygon(Vec3d... pts) {
		this(Arrays.asList(pts));
	}
	
	public Polygon(List<Vec3d> pts)
	{
		supportPlane = new Plane(pts);

		DEdge last = ( anchor = new DEdge( pts.get(0) ) );
		for( int i = 1; i < pts.size();++i )
			last = new DEdge( pts.get(i), last );
		DEdge.closeCycle( anchor, last );
		nDEdges= pts.size();
	}

	// Split Directed-Edge d of this Polygon by cut Plane:
	public void split(Plane cut, DEdge d )
	{
		if (cut.sDistance(d.srcPoint()) * cut.sDistance(d.dstPoint()) >= 0.0) { // same as sgn(a)!=sgn(b)
			throw new IllegalArgumentException();
		}
		Vec3d onP = cut.onPoint( d.srcPoint(), d.dstPoint() );
		d.split( onP );
		++nDEdges;
	}

	// Assign each srcPoint of every DEdge ABOVE, ON, or BELOW depending
	// where they are in relation to the cut plane, and split any DEdges
	// that cross the cut plane.
	public Where classifyPoints(Plane cut,
			List<DEdge> onDEdges)
	{
		if (first() == null) return NOWHERE;
		first().srcWhere(cut.whichSide( first().srcPoint() ));
		Where[] polyW = {first().srcWhere()};
		forEachDEdge( new Consumer<DEdge>() {
			@Override
			public void accept(DEdge d) {
				d.dstWhere(cut.whichSide( d.dstPoint() ));
				polyW[0] = polyW[0].or(d.dstWhere());
				if( d.where() == ABOVEBELOW ) {
					split( cut, d );
					onDEdges.add(( d = d.next() ));
					d.srcWhere(ON);
				} else if( d.srcWhere() == ON )
					onDEdges.add(d);
			}
		});
		return polyW[0];
	}

	public Polygon(DEdge start, Plane sPl)
	{
		supportPlane = sPl;
		anchor  = start;
		nDEdges = 0;
		forEachDEdge( (d) -> {
			d.srcWhere(NOWHERE);
			++nDEdges;
		});
	}

	public void maximize( DEdge d )
	{
		DEdge dN = d.next();
		if( d.srcWhere() == ON && dN.srcWhere() == ON && dN.dstWhere() == ON ) {
			// Merge two adjacent and colinear DEdges:
			DEdge.closeCycle( dN.next(), d );
			anchor = d;
			//delete dN;
			--nDEdges;
		}
	}

	// Insert two new Directed Edges, between srcD.srcPoint() and
	// dstD.srcPoint(); one for the above loop and one for the below loop.
	void addBridge( DEdge leftBelow,
			DEdge rghtAbove )
	{
		assert( leftBelow.srcWhere() == ON );
		assert( rghtAbove.srcWhere() == ON );
		assert( leftBelow != rghtAbove );
		DEdge leftAbove = leftBelow.prev();
		DEdge rghtBelow = rghtAbove.prev();
		DEdge onAbove   = new DEdge( leftBelow.srcPoint(), leftAbove );
		DEdge onBelow   = new DEdge( rghtAbove.srcPoint(), rghtBelow );
		DEdge.closeCycle( rghtAbove, onAbove );
		DEdge.closeCycle( leftBelow, onBelow );
		onAbove.srcWhere(onBelow.srcWhere(ON));
		maximize( onAbove.prev() );
		maximize( onBelow );
	}

	private static <T> void swap(List<T> li, int i, int j) {
		li.set(j, li.set(i, li.get(j)));
	}
	
	// Sort directed edges that have srcPoints ON the cut plane
	// left to right (in direction of cutDir) by their source points.
	//void sortDEdges(  int nOnDs, DEdge onDs[],  Vec3d cutDir )
	void sortDEdges(  List<DEdge> onDs,  Vec3d cutDir )
	{
		if (onDs.size() < 2) return;
		 Vec3d refP = onDs.get(0).srcPoint();
		for( int i = 0; i < onDs.size(); ++i )
			onDs.get(i).distFromRefP(cutDir.dotProduct(onDs.get(i).srcPoint().subtract(refP) ));
			for(int i = onDs.size()-1; i > 0; --i )
				for( int j = 0, k = 1; k <= i; j = k++ )
					if( onDs.get(j).distFromRefP() > onDs.get(k).distFromRefP() ||
							(onDs.get(j).distFromRefP() == onDs.get(k).distFromRefP() &&
							onDs.get(j).dstWhere() == ABOVE) )
						swap( onDs, j, k );
	}

	static DEdge useSrc = null;

	// Get the next directed edge that starts a cut.
	// This assumes all vertices on the cut Plane have manifold sectors.
	//static DEdge getSrcD( DEdge onDs[], int& start,  int nOnDs )
	static DEdge getSrcD( List<DEdge> onDs, int start )
	{
		if( useSrc != null ) {
			DEdge gotIt = useSrc;
			useSrc = null;
			return gotIt;
		}
		while( start < onDs.size() ) {
			 Where prevW = onDs.get(start).prev().srcWhere();
			 Where nextW = onDs.get(start).dstWhere();
			if( (prevW == ABOVE && nextW == BELOW) ||
					(prevW == ABOVE && nextW == ON &&
					onDs.get(start).next().distFromRefP() < onDs.get(start).distFromRefP()) ||
					(prevW == ON && nextW == BELOW &&
					onDs.get(start).prev().distFromRefP() < onDs.get(start).distFromRefP()) )
				return onDs.get(start++);
			++start;
		}
		return null;
	}

	// Get the next directed edge that ends a cut.
	//static DEdge getDstD( DEdge onDs[], int& start,  int nOnDs )
	static DEdge getDstD( List<DEdge> onDs, int start )
	{
		while( start < onDs.size() ) {
			 Where prevW = onDs.get(start).prev().srcWhere();
			 Where nextW = onDs.get(start).dstWhere();
			if( (prevW == BELOW && nextW == ABOVE) ||
					(prevW == BELOW && nextW == BELOW) ||
					(prevW == ABOVE && nextW == ABOVE) ||
					(prevW == BELOW && nextW == ON &&
					onDs.get(start).distFromRefP() < onDs.get(start).next().distFromRefP()) ||
					(prevW == ON && nextW == ABOVE &&
					onDs.get(start).distFromRefP() < onDs.get(start).prev().distFromRefP()) )
				return onDs.get(start++);
			++start;
		}
		return null;
	}

	void complexCut(  Plane cut,
			// int nOnDs, DEdge onDs[],
			List<DEdge>onDs,
			List<Polygon> above, List<Polygon> below)
	{
		sortDEdges( onDs, cut.normal().crossProduct(plane().normal()) );
		int startOnD = 0;
		DEdge srcD = null;
		while( (srcD = getSrcD( onDs, startOnD )) != null ) {
			DEdge dstD = getDstD( onDs, startOnD );
			assert( dstD != null );
			addBridge( srcD, dstD );
			if( srcD.prev().prev().srcWhere() == ABOVE )
				useSrc = srcD.prev();
				else if( dstD.dstWhere() == BELOW )
					useSrc = dstD;
		}
		// Collect new Polygons:
			for( int i = 0; i < onDs.size(); ++i ) {
				if( onDs.get(i).srcWhere() == ON ) {
					if( onDs.get(i).dstWhere() == ABOVE )
						above.add(new Polygon( onDs.get(i), plane() ));
					else if( onDs.get(i).dstWhere() == BELOW )
						below.add(new Polygon( onDs.get(i), plane() ));
				}
			}
	}

	public static void split( Polygon g,  Plane cut,
			List<Polygon> above,
			List<Polygon> on,
			List<Polygon> below )
	{
		List<DEdge> onDEdges = Lists.newArrayListWithCapacity(g.nPoints());
		//DEdge  onDEdges[g.nPoints()];
		switch( g.classifyPoints( cut, onDEdges ) ) {
			case ONABOVE:
			case ABOVE:
				above.add(g);
				break;
			case ON:
				on.add(g);
				break;
			case ONBELOW:
			case BELOW:
				on.add(g);
				break;
			default: /* case CROSS */
			g.complexCut( cut, onDEdges, above, below );
			g.anchor  = null;
			g.nDEdges = 0;
			//delete g;
		}
		g = null;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder();
		forEachDEdge((de) -> {
			b.append(de.srcPoint());
		});
		return b.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Polygon that = (Polygon) obj;
		if (this.nDEdges != that.nDEdges)
			return false;
		List<Vec3d> ours = Lists.newArrayList();
		List<Vec3d> theirs = Lists.newArrayList();
		this.forEachDEdge((de) -> ours.add(de.srcPoint()));
		that.forEachDEdge((de) -> theirs.add(de.srcPoint()));
		return ours.equals(theirs);
	}
	@Override
	public String toString() {
		List<Vec3d> li = Lists.newArrayList();
		forEachDEdge((de) -> li.add(de.srcPoint()));
		return li.toString();
	}
	@Override
	public Iterator<DEdge> iterator() {
		return new Iterator<DEdge>() {
			DEdge first = first();
			DEdge last = null;
			DEdge cur = first();
			
			@Override
			public boolean hasNext() {
				return cur != first || last == null;
			}
			
			@Override
			public DEdge next() {
				last = cur;
				cur = cur.next();
				return cur;
			}
			
		};
	}

}
