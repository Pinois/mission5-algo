package m5.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputBitStream
{
	protected int accumulator;
	protected InputStream in;

	public InputBitStream(String paramString)
			throws IOException
	{
		this.in = new FileInputStream(paramString);
		initAccumulator();
	}

	private void initAccumulator() {
		this.accumulator = 128;
	}

	public void close()
			throws IOException
	{
		this.in.close();
	}

	public boolean readBoolean()
			throws IOException
	{
		if ((this.accumulator <<= 1 & 0x100) != 0) {
			this.accumulator = this.in.read();
			if (this.accumulator == -1) throw new IOException();
			this.accumulator = (this.accumulator << 8 | 0x1);
		}
		return (this.accumulator & 0x8000) != 0;
	}

	public byte readByte()
			throws IOException
	{
		int i = 0;
		for (int j = 0; j < 8; j++) {
			i <<= 1;
			if (readBoolean()) {
				i |= 1;
			}
		}
		return (byte) i;
	}

	public char readChar()
			throws IOException
	{
		int i = 0;
		for (int j = 0; j < 16; j++) {
			i <<= 1;
			if (readBoolean()) {
				i |= 1;
			}
		}
		return (char) i;
	}

	public int readShort()
			throws IOException
	{
		int i = 0;
		for (int j = 0; j < 16; j++) {
			i <<= 1;
			if (readBoolean()) {
				i |= 1;
			}
		}
		return i;
	}

	public int readInt()
			throws IOException
	{
		int i = 0;
		for (int j = 0; j < 32; j++) {
			i <<= 1;
			if (readBoolean()) {
				i |= 1;
			}
		}
		return i;
	}

	public long readLong()
			throws IOException
	{
		long l = 0L;
		for (int i = 0; i < 64; i++) {
			l <<= 1;
			if (readBoolean()) {
				l |= 1L;
			}
		}
		return l;
	}
}