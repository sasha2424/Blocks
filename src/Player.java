import java.util.ArrayList;

public class Player {

	public ArrayList<Block> blocks;

	final static float size = 50;

	float x;
	float y;

	boolean isDead = false;

	public Player(float x, float y) {
		blocks = new ArrayList<Block>();
		this.x = x;
		this.y = y;
	}

	public void addBlock(Player p) {
		if (blocks.isEmpty()) {
			addBlockToCenter(p);
		} else {
			Block min = blocks.get(0);
			float minDist = dist(min, p);
			for (Block b : blocks) {
				float dist = dist(b, p);
				if (dist < minDist) {
					min = b;
					minDist = dist;
				}
			}

			if (dist(this, p) < minDist) {
				addBlockToCenter(p);
			} else {
				addBlockToBlock(p, min);
			}
		}
	}

	private void addBlockToBlock(Player p, Block b) {
		Block addedBlock = new Block(this, b.x, b.y);
		float dx = p.x - b.getX();
		float dy = p.y - b.getY();
		if (dx > dy) {
			if (b.getX() < p.x) {
				addedBlock.x++;
			} else {
				addedBlock.x--;
			}
		} else {
			if (b.getY() < p.y) {
				addedBlock.y++;
			} else {
				addedBlock.y--;
			}
		}
		blocks.add(addedBlock);
	}

	private void addBlockToCenter(Player p) {
		float dx = p.x - x;
		float dy = p.y - y;
		if (dx > dy) {
			if (x < p.x) {
				blocks.add(new Block(this, 1, 0));
			} else {
				blocks.add(new Block(this, -1, 0));
			}
		} else {
			if (y < p.y) {
				blocks.add(new Block(this, 0, 1));
			} else {
				blocks.add(new Block(this, 0, -1));
			}
		}
	}

	private static float dist(Player p1, Player p2) {
		return (float) (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
	}

	private static float dist(Block b, Player p) {
		if (b == null) {
			return 0;
		}
		return (float) (Math.sqrt((b.getX() - p.x) * (b.getX() - p.x) + (b.getY() - p.y) * (b.getY() - p.y)));
	}

	public boolean collideBlock(Player p) {
		for (int i = 0; i < blocks.size(); i++) {
			Block bi = blocks.get(i);
			if (hit(p.x, p.y, bi.getX(), bi.getY())) {
				addBlock(p);
				return true;
			}
		}
		if (hit(x, y, p.x, p.y)) {
			addBlock(p);
			return true;
		}
		return false;
	}

	public boolean collidePlayer(Player p) {
		for (int i = 0; i < blocks.size(); i++) {
			Block bi = blocks.get(i);
			for (int j = 0; j < p.blocks.size(); j++) {

				Block bj = p.blocks.get(j);
				if (hit(bi.getX(), bi.getY(), bj.getX(), bj.getY())) {
					blocks.remove(i);
					p.blocks.remove(j);
					return true;
				}

				if (hit(x, y, bj.getX(), bj.getY())) {
					isDead = true;
					return true;
				}
			}
			if (hit(p.x, p.y, bi.getX(), bi.getY())) {
				p.isDead = true;
				return true;
			}
		}
		if (hit(x, y, p.x, p.y)) {
			isDead = true;
			p.isDead = true;
			return true;
		}
		return false;
	}

	public boolean hit(float x1, float y1, float x2, float y2) {
		if (Math.abs(x1 - x2) > size || Math.abs(y1 - y2) > size)
			return false;
		return true;
	}

	public String toString() {
		String r = "[" + x + "," + y + "]";
		for (Block b : blocks) {
			r += b.toString();
		}
		return r;
	}

	public String toStringBlock() {
		return "<" + x + "," + y + ">";
	}

	private class Block {

		Player p;
		public int x, y;

		public Block(Player p, int x, int y) {
			this.p = p;
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return p.x + size * x;
		}

		public float getY() {
			return p.y + size * y;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

}
