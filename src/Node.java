import java.util.HashMap;

public class Node {
	private Integer id;
	private Integer start;
	private Integer end;
	private Node suffixLink;
	private Boolean leaf;
	private Boolean[] type;
	private HashMap<Character, Node> children;

	private static Integer totalNodes = 0;
	private static Integer leafEnd = -1;

	private Node() {
		System.out.println("Private Node Constructor");
	}

	public Node(Integer start, Integer end, Boolean leaf) {
		this.start = start;
		this.end = end;
		this.suffixLink = null;
		this.leaf = leaf;
		this.children = new HashMap<>();
		this.id = totalNodes;
		this.type = new Boolean[2];
		this.type[0] = Boolean.FALSE;
		this.type[1] = Boolean.FALSE;
		totalNodes++;
	}

	public Integer getLength() {
		return getEnd() - getStart() + 1;
	}

	public Boolean isTypeA() {
		return type[0];
	}

	public Boolean isTypeB() {
		return type[1];
	}

	public void setType(Boolean a, Boolean b) {
		this.type[0] = a;
		this.type[1] = b;
	}

	public Boolean[] getType() {
		return this.type;
	}

	public static void extendLeaf() {
		leafEnd++;
	}

	public void setNode(Integer start, Integer end, Boolean leaf) {
		this.start = start;
		this.end = end;
		this.leaf = leaf;
	}

	public Node getChild(Character c) {
		return children.get(c);
	}

	public void addChild(Character c, Node n) {
		children.put(c, n);
	}

	public Boolean containsChild(Character c) {
		return children.containsKey(c);
	}

	public Node getEdge(Character c) {
		return children.get(c);
	}

	public HashMap<Character, Node> getChildren() {
		return children;
	}

	public void setSuffixLink(Node n) {
		this.suffixLink = n;
	}

	public Node getSuffixLink() {
		return suffixLink;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		if(isLeaf()) return leafEnd;
		else return end;
	}

	public Boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public static void main(String[] args) {
		System.out.println("Node unit tests");
	}
}