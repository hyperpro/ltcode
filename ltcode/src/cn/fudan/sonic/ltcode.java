package cn.fudan.sonic;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class ltcode {
	public int blockSize, fileSize, N;
	private RandomAccessFile input;

	public ltcode(RandomAccessFile input, int blockSize){
		this.input = input;
		this.blockSize = blockSize;
		try{
			this.N = (int) Math.ceil((double) input.length()/(double) blockSize);
			this.fileSize = (int) input.length();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	public packetStruct getcoded(int genSeed) throws IOException{
		byte[] content=new byte[blockSize];
		byte[] codedContent = new byte[blockSize];
		for (int i=0;i<blockSize;i++) codedContent[i] = 0;
		int seed,degree;
		seed = genSeed;
		degree = genDegree();
		
		Random genPackets = new Random(seed);
		int[] codeQueue = new int[degree];
		for (int i=0; i<degree; i++){ 
			boolean flag = true;
			int x;
			while (true)
			{
				x = Math.abs((genPackets.nextInt())) % N;
				for (int j=0; j<i; j++)
				{
					if (codeQueue[j] == x){
						flag = false;
						break;
					}
				}
				if (flag) break;
				flag = true;
			}
			codeQueue[i] = x;
		}


		for (int i=0; i<degree; i++) //make a coded packet
		{
			input.seek(codeQueue[i]*blockSize);
			for (int j=0;j<blockSize;j++) content[j] = 0;
			input.read(content);
			for (int j=0; j<blockSize;j++)
			{
				codedContent[j] = (byte) (codedContent[j]^content[j]);
			}
		}
		packetStruct packet = new packetStruct(seed, degree, codedContent, blockSize);
		return packet;
	}
	
	private int genDegree() { //degree distribution
		int degree;
		double x = Math.random();
		degree = (int) Math.ceil(Math.floor((1.0/x)));
		if (degree > this.N) degree = 1;
		return degree;//
	}
	
}
