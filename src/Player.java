import java.util.ArrayList;

public class Player {
	
	public ArrayList<Block> blocks;
	
	float x;
	float y;
	
	public Player(float x, float y) {
		blocks = new ArrayList<Block>();
		this.x = x;
		this.y = y;
	}
	
	
	public String toString() {
		String r = "[" + x + "," + y + "]";
		for(Block b : blocks) {
			r += b.toString();
		}
		return r;
	}
	
	private class Block{
		public int x,y;
		
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}
	
	
	

}
