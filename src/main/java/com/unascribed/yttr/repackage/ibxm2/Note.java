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

public class Note {
	public int key, instrument, volume, effect, param;

	private static final String b36ToString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String keyToString = "A-A#B-C-C#D-D#E-F-F#G-G#";

	@Override
	public String toString() {
		return new String( toChars( new char[ 10 ] ) );
	}

	public char[] toChars( char[] chars ) {
		keyToChars( key, chars );
		chars[ 3 ] = ( instrument > 0xF && instrument < 0xFF ) ? b36ToString.charAt( ( instrument >> 4 ) & 0xF ) : '-';
		chars[ 4 ] = ( instrument > 0x0 && instrument < 0xFF ) ? b36ToString.charAt( instrument & 0xF ) : '-';
		chars[ 5 ] = ( volume > 0xF && volume < 0xFF ) ? b36ToString.charAt( ( volume >> 4 ) & 0xF ) : '-';
		chars[ 6 ] = ( volume > 0x0 && volume < 0xFF ) ? b36ToString.charAt( volume & 0xF ) : '-';
		if( ( effect > 0 || param > 0 ) && effect < 36 ) {
			chars[ 7 ] = b36ToString.charAt( effect );
		} else if( effect > 0x80 && effect < 0x9F ) {
			chars[ 7 ] = ( char ) ( 96 + ( effect & 0x1F ) );
		} else {
			chars[ 7 ] = '-';
		}
		chars[ 8 ] = ( effect > 0 || param > 0 ) ? b36ToString.charAt( ( param >> 4 ) & 0xF ) : '-';
		chars[ 9 ] = ( effect > 0 || param > 0 ) ? b36ToString.charAt( param & 0xF ) : '-';
		return chars;
	}

	private static void keyToChars( int key, char[] out ) {
		out[ 0 ] = ( key > 0 && key < 118 ) ? keyToString.charAt( ( ( key + 2 ) % 12 ) * 2 ) : '-';
		out[ 1 ] = ( key > 0 && key < 118 ) ? keyToString.charAt( ( ( key + 2 ) % 12 ) * 2 + 1 ) : '-';
		out[ 2 ] = ( key > 0 && key < 118 ) ? ( char ) ( '0' + ( key + 2 ) / 12 ) : '-';
	}
}
