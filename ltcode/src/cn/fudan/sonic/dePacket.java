package cn.fudan.sonic;

import java.util.HashMap;
import java.util.Map;

public class dePacket {
	public byte[] data;
	public boolean decode = false;
	public Map<Integer, Integer> link = new HashMap<Integer, Integer>();//may employ a TreeMap.
	public int blockSize;
	
	public dePacket(int blockSize){
			this.decode = false;
			this.blockSize = blockSize;
			this.data = new byte[blockSize];
			for (int i=0;i<blockSize;i++)
				this.data[i] = 0;
	}
	public int addLink(int x){
		link.put(x,1);
		return 0;
	}
	public int removeLink(int x){
		link.remove(x);
		return 0;
	}
	public int setData(byte[] data){
		for (int i=0; i<blockSize;i++)
		{
			this.data[i] = data[i];
		}
		return 0;
	}
	public int modifyData(byte[] data) {
		for (int i = 0; i<blockSize;i++)
			this.data[i] = (byte) (data[i]^this.data[i]);
		return 0;
	}

}
