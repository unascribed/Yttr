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

public class Pattern {
	public int numRows;
	public byte[] data;

	public Pattern( int numChannels, int numRows ) {
		this.numRows = numRows;
		data = new byte[ numChannels * numRows * 5 ];
	}

	public Note getNote( int index, Note note ) {
		int offset = index * 5;
		note.key = data[ offset ] & 0xFF;
		note.instrument = data[ offset + 1 ] & 0xFF;
		note.volume = data[ offset + 2 ] & 0xFF;
		note.effect = data[ offset + 3 ] & 0xFF;
		note.param = data[ offset + 4 ] & 0xFF;
		return note;
	}

	public void toStringBuffer( StringBuffer out ) {
		Note note = new Note();
		char[] chars = new char[ 10 ];
		int numChannels = data.length / ( numRows * 5 );
		for( int row = 0; row < numRows; row++ ) {
			for( int channel = 0; channel < numChannels; channel++ ) {
				getNote( numChannels * row + channel, note );
				note.toChars( chars );
				out.append( chars );
				out.append( ' ' );
			}
			out.append( '\n' );
		}
	}

	@Override
	public String toString() {
		int numChannels = data.length / ( numRows * 5 );
		StringBuffer stringBuffer = new StringBuffer( numRows * numChannels * 11 + numRows );
		toStringBuffer( stringBuffer );
		return stringBuffer.toString();
	}
}
