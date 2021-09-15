package com.unascribed.yttr.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.BufferUtils;

import com.unascribed.yttr.repackage.ibxm2.Channel;
import com.unascribed.yttr.repackage.ibxm2.IBXM;
import com.unascribed.yttr.repackage.ibxm2.Module;

import net.minecraft.client.sound.AudioStream;

/**
 * Based on WavInputStream in IBXM
 */
public class IBXMAudioStream implements AudioStream {

	private IBXM ibxm;
	private int[] mixBuf;
	private byte[] outBuf;
	private int outIdx, outLen, remain, fadeLen;

	public IBXMAudioStream(InputStream in) throws IOException {
		this(new IBXM(new Module(in), 48000));
		ibxm.setInterpolation(Channel.LINEAR);
	}
	
	public IBXMAudioStream(IBXM ibxm) {
		this(ibxm, ibxm.calculateSongDuration(), 0);
	}

	/*
	 * Duration is specified in samples at the sampling rate of the IBXM
	 * instance. If fadeOutSeconds is greater than zero, a fade-out will be
	 * applied at the end of the stream.
	 */
	public IBXMAudioStream(IBXM ibxm, int duration, int fadeOutSeconds) {
		this.ibxm = ibxm;
		mixBuf = new int[ibxm.getMixBufferLength()];
		outBuf = new byte[mixBuf.length * 2];
		int dataLen = duration * 2;
		int samplingRate = ibxm.getSampleRate();
		outIdx = 0;
		outLen = 0;
		remain = dataLen;
		fadeLen = samplingRate * 2 * fadeOutSeconds;
	}

	/* Get the number of bytes available before read() returns end-of-file. */
	public int getBytesRemaining() {
		return remain;
	}

	@Override
	public ByteBuffer getBuffer(int size) throws IOException {
		if (remain > 0) {
			ByteBuffer buf = BufferUtils.createByteBuffer(size);
			while (buf.position() < size) {
				if (remain <= 0) break;
				int count = remain;
				if (count > size) {
					count = size;
				}
				int outRem = outLen - outIdx;
				if (count > outRem) {
					count = outRem;
				}
				if (count > buf.remaining()) {
					count = buf.remaining();
				}
				buf.put(outBuf, outIdx, count);
				outIdx += count;
				if (outIdx >= outLen) {
					getAudio();
				}
				remain -= count;
			}
			buf.flip();
			return buf;
		}
		return BufferUtils.createByteBuffer(0);
	}

	private void getAudio() {
		int mEnd = ibxm.getAudio(mixBuf) * 2;
		int gain = 1024;
		if (remain < fadeLen) {
			gain = remain / (fadeLen >> 10);
			gain = (gain * gain * gain) >> 20;
		}
		for (int mIdx = 0, oIdx = 0; mIdx < mEnd; mIdx += 2) {
			int ampl1 = (mixBuf[mIdx] * gain) >> 10;
			int ampl2 = (mixBuf[mIdx + 1] * gain) >> 10;
			int ampl = (ampl1 + ampl2) / 2;
			if (ampl > 32767)
				ampl = 32767;
			if (ampl < -32768)
				ampl = -32768;
			outBuf[oIdx++] = (byte) ampl;
			outBuf[oIdx++] = (byte) (ampl >> 8);
		}
		outIdx = 0;
		outLen = mEnd;
	}
	
	@Override
	public void close() throws IOException {
	}
	
	@Override
	public AudioFormat getFormat() {
		return new AudioFormat(ibxm.getSampleRate(), 16, 1, true, false);
	}

}
