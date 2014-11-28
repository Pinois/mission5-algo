package m5.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputBitStreams
{
	protected int accumulator;
	protected OutputStream out;
	private boolean reset;

	public OutputBitStreams(String paramString)
			throws IOException
	{
		this.out = new FileOutputStream(paramString);
		initAccumulator();
	}

	private void initAccumulator() {
		this.accumulator = 1;
		this.reset = true;
	}

	public void close()
			throws IOException
	{
		if (!this.reset) {
			while ((this.accumulator & 0x100) != 256) {
				this.accumulator <<= 1;
			}
			this.out.write(this.accumulator);
		}
		this.out.close();
	}

	public void write(boolean paramBoolean)
			throws IOException
	{
		int i = paramBoolean ? 1 : 0;
		this.accumulator = (this.accumulator << 1 | i);
		this.reset = false;
		if ((this.accumulator & 0x100) != 0) {
			this.out.write(this.accumulator);
			initAccumulator();
		}
	}

	public void write(byte paramByte)
			throws IOException
	{
		for (byte b = (byte) 128; b != 0; b >>>= 1)
			write((b & paramByte) != 0);
	}

	public void write(char paramChar)
			throws IOException
	{
		for (char c = 32768; c != 0; c >>>= '\001')
			write((c & paramChar) != 0);
	}

	public void write(short paramShort)
			throws IOException
	{
		for (short s = (short) 32768; s != 0; s >>>= 1)
			write((s & paramShort) != 0);
	}

	public void write(int paramInt)
			throws IOException
	{
		for (int i = -2147483648; i != 0; i >>>= 1)
			write((i & paramInt) != 0);
	}

	public void write(long paramLong)
			throws IOException
	{
		for (long l = -9223372036854775808L; l != 0L; l >>>= 1)
			write((l & paramLong) != 0L);
	}
}