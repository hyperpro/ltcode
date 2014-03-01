package cn.fudan.sonic;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;

public class udp {
	public static void main(String args[]) throws IOException {
		int blockSize, fileSize, N;
		blockSize = 504; //data payloads per packet 
		RandomAccessFile input = null, output = null;
		Random genSeed = new Random();
		long startTime=System.currentTimeMillis();
		try{   //path of input and output
			input = new RandomAccessFile("/home/zhangxu/下载/video1.mp4","r"); 
			output = new RandomAccessFile("/home/zhangxu/下载/video2.mp4","rw");
		} catch (IOException e){
			e.printStackTrace();
		}
		fileSize = (int) input.length();  //file size
		N = (int) Math.ceil((double) fileSize/(double) blockSize); //number of blocks
		ltcode genPackets = new ltcode(input,blockSize);
		decode decodePackets = new decode(blockSize, fileSize);
		while (decodePackets.surplus>0) 
		{
			packetStruct codePacket = genPackets.getcoded(Math.abs(genSeed.nextInt()));//get a coded packet 
			decodePackets.decodePacket(codePacket);//decode a coded packet
		}
		for (int i=0;i<N-1;i++) //output a file
		{
			output.write(decodePackets.originalPacket.get(i).data);
		}

		byte[] tempData = Arrays.copyOfRange(decodePackets.originalPacket.get(N-1).data,0,fileSize-blockSize*(N-1));//deal with the last packet
		output.write(tempData);
		input.close();
		output.close();
		long endTime=System.currentTimeMillis();
		System.out.println("Filesize:  "+fileSize);
		System.out.println("Running Time: "+ (endTime-startTime)+"ms");
	}

}
