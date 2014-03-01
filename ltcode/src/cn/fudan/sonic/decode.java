package cn.fudan.sonic;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;


public class decode {
	public int blockSize, fileSize, N, surplus;
	public ArrayList<dePacket> originalPacket = new ArrayList<dePacket>();
	public ArrayList<dePacket> undecodedPacket = new ArrayList<dePacket>();

	public decode(int blockSize, int fileSize) throws FileNotFoundException{

		this.blockSize = blockSize;
		this.fileSize = fileSize;
		this.N = (int) Math.ceil((double)fileSize / (double) blockSize);
		this.surplus = N;
		for (int i=0;i<N;i++){
			originalPacket.add(new dePacket(blockSize));
		}
	}
	
	public int bfs(int x) //using a decoded packet to decode other packets, no recursion
	{	
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(x);
		while (queue.size()>0)
		{
			int y = queue.remove(0);
			while (originalPacket.get(y).link.size()>0)
			{
				Iterator<Integer> iter = originalPacket.get(y).link.keySet().iterator();
				int z = iter.next();
				undecodedPacket.get(z).modifyData(originalPacket.get(y).data);
				originalPacket.get(y).removeLink(z);
				undecodedPacket.get(z).removeLink(y);
				
				if (1 == undecodedPacket.get(z).link.size())
				{
					Iterator<Integer> tempIter = undecodedPacket.get(z).link.keySet().iterator();
					int k = tempIter.next();
					originalPacket.get(k).removeLink(z);
					undecodedPacket.get(z).removeLink(k);
					if (originalPacket.get(k).decode == false)
					{
						originalPacket.get(k).setData(undecodedPacket.get(z).data);
						originalPacket.get(k).decode = true;
						this.surplus = this.surplus - 1;
						queue.add(k);
					}
				}
				
			}
		}
		return 0;
	}


	public int decodePacket(packetStruct packet){
		Random genPackets = new Random(packet.seed);
		//three circumstances
		if (packet.degree == 1) {
			int number = Math.abs(genPackets.nextInt()) % N;
			if (originalPacket.get(number).decode == false) {
				originalPacket.get(number).decode = true;
				this.surplus = this.surplus - 1;
				originalPacket.get(number).setData(packet.data);
				bfs(number);
				
			}
		}
		else
		{
			int[] codeQueue = new int[packet.degree];
			boolean[] codeQueue1 = new boolean[packet.degree]; 
			byte[] data = new byte[blockSize];
			data = Arrays.copyOf(packet.data,packet.data.length);
			int total = packet.degree;
			for (int i = 0; i< packet.degree; i++) //restore packet coding information
			{
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
		
				if (originalPacket.get(x).decode == true) 
				{
					total = total - 1;
					for (int j=0; j<blockSize; j++)
						data[j] = (byte) (originalPacket.get(x).data[j]^data[j]);
					codeQueue1[i] = false; 
				}
				else
					codeQueue1[i] = true;
					
			}

			if (total>0) {
				if (1 == total)
				{
					int x = -1;
					for (int i=0; i<packet.degree;i++)
						if (codeQueue1[i] == true)
						{
							x = codeQueue[i];
							break;
						}
					if (originalPacket.get(x).decode == false) {
						originalPacket.get(x).decode = true;
						this.surplus = this.surplus - 1;
						originalPacket.get(x).setData(data);
						bfs(x);
					}
				}
				else
				{
					undecodedPacket.add(new dePacket(blockSize));
					int length = undecodedPacket.size();
					for (int i=0; i<packet.degree; i++)//build a graph
						if (codeQueue1[i] == true)
						{
							undecodedPacket.get(length-1).addLink(codeQueue[i]);
							originalPacket.get(codeQueue[i]).addLink(length-1);
						}
					undecodedPacket.get(length-1).setData(data);
				}	
			}
		}
		return 0;
		
	}
	

}
