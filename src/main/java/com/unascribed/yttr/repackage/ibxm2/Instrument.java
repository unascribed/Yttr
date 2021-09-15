/*
 * IBXM2
 * Copyright (c) 2019, Martin Cameron
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 * 
 *  * Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 * 
 *  * Neither the name of the organization nor the names of
 *    its contributors may be used to endorse or promote
 *    products derived from this software without specific
 *    prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.unascribed.yttr.repackage.ibxm2;

public class Instrument {
	public String name = "";
	public int numSamples = 1;
	public int vibratoType = 0, vibratoSweep = 0, vibratoDepth = 0, vibratoRate = 0;
	public int volumeFadeOut = 0;
	public Envelope volumeEnvelope = new Envelope();
	public Envelope panningEnvelope = new Envelope();
	public int[] keyToSample = new int[ 97 ];
	public Sample[] samples = new Sample[] { new Sample() };

	public void toStringBuffer( StringBuffer out ) {
		out.append( "Name: " + name + '\n' );
		if( numSamples > 0 ) {
			if( vibratoDepth > 0 ) {
				out.append( "Vibrato Type: " + vibratoType + '\n' );
				out.append( "Vibrato Sweep: " + vibratoSweep + '\n' );
				out.append( "Vibrato Depth: " + vibratoDepth + '\n' );
				out.append( "Vibrato Rate: " + vibratoRate + '\n' );
			}
			if( volumeFadeOut > 0 ) {
				out.append( "Volume Fade Out: " + volumeFadeOut + '\n' );
			}
			if( volumeEnvelope.enabled ) {
				out.append( "Volume Envelope:\n" );
				volumeEnvelope.toStringBuffer( out, "   " );
			}
			if( panningEnvelope.enabled ) {
				out.append( "Panning Envelope:\n" );
				panningEnvelope.toStringBuffer( out, "   " );
			}
			out.append( "Num Samples: " + numSamples + '\n' );
			for( int samIdx = 0; samIdx < numSamples; samIdx++ ) {
				out.append( "Sample " + samIdx + ":\n" );
				samples[ samIdx ].toStringBuffer( out, "   " );
			}
			if( numSamples > 1 ) {
				out.append( "Key To Sample:\n" );
				for( int oct = 0; oct < 8; oct++ ) {
					out.append( "   Oct " + oct + ": " );
					for( int key = 0; key < 12; key++ ) {
						out.append( keyToSample[ oct * 12 + key + 1 ] );
						if( key < 11 ) {
							out.append( "," );
						}
					}
					out.append( '\n' );
				}
			}
		}
	}
}
