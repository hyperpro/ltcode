package cn.fudan.sonic;


public class packetStruct {
	public int seed, degree;
	public byte[] data;
	public packetStruct(int seed, int degree, byte[] data, int blockSize){
		this.data = new byte[blockSize]; //Arrays.copyOf(data, data.length);
		for (int i=0;i<data.length;i++)
		{
			this.data[i] = data[i];
		}
		for (int i=data.length;i<blockSize;i++) this.data[i] = 0;
		this.seed = seed;
		this.degree = degree;
	}
	public packetStruct(int blockSize){
		this.data = new byte[blockSize];
		this.seed = 0;
		this.degree = 0;
	}
}
