import java.util.*;
import java.lang.StringBuilder;

public class SuffixTree {
	private Node activeNode;
	private Node lastNewNode;
	private Integer activeEdge;
	private Integer activeLength;
	private Integer remainder;
	private Integer splitIndex;

	private Integer maxHeight;
	private Integer substrStart;

	private Node root;
	private String string;

	private SuffixTree() {
		System.out.println("Private SuffixTree Constructor");
	}

	public SuffixTree(String string1, String string2) {
		this.splitIndex = string1.length();
		this.string = string1 + "$" + string2 + "#";
		this.activeEdge = -1;
		this.activeLength = 0;
		this.remainder = 0;
		this.root = new Node(-1, -1, Boolean.FALSE);
		this.activeNode = this.root;
	}

	public void killTree() {
		this.root = null;
		System.gc();
	}

	public String getLCS(){
		this.buildTree();
		this.mark();
		return this.findLCS();
	}

	private Node createNode(Integer start, Integer end, Boolean leaf) {
		Node newNode = new Node(start, end, leaf);
		newNode.setSuffixLink(this.root);

		return newNode;
	}

	public void buildTree() {

		/* for each of the characters in this suffix tree ... */
		for(int i = 0; i < string.length(); i++) {

			/* all leaves are automatically append the next character */
			Node.extendLeaf();

			/* we have the next suffix to add (this is done automatically) */
			this.remainder = this.remainder + 1;

			/* rule #2
			 * we reset the last new node and if there are two splits, we connect the
			 * first node to the second node (suffix link)
			 */
			this.lastNewNode = null;

			/* we're going to try and add all of the suffixes needede to be added,
			 * the three rules will change our course of action
			 */

			Boolean showstopper = Boolean.FALSE;
			while(this.remainder > 0) {

				showstopper = Boolean.FALSE;

				/* this means we're at the start of the active node and haven't moved in */
				if(this.activeLength == 0) {
					this.activeEdge = i;
				}

				/* this means there's no branch of this letter at the activeNode (this is
				 * an easy add)
				 */
				if(!this.activeNode.containsChild(this.string.charAt(this.activeEdge))) {
					
					/* we then just add a new branch for it 
					 * note that this will be a leaf node since it won't have any children
					 * so, TRUE is passed as the third argument
					 */
					Node nn = createNode(i, i, Boolean.TRUE);
					this.activeNode.addChild(this.string.charAt(this.activeEdge), nn);

					/* apply rule #2 mentioned earlier: 
					 * if there is a last new node (in this cycle) and we just tried to add a 
					 * character, we can connect a suffix link because this is the second time we
					 * added a letter
					 */
					if(this.lastNewNode != null) {
						this.lastNewNode.setSuffixLink(activeNode);
						this.lastNewNode = null;
					}
				} else {
					/* there's an edge sticking out of the active node, let's grab it */
					Node nn = activeNode.getChild(this.string.charAt(this.activeEdge));

					/* we always check if active length exceeds on this side of the logic since
					 * we'll be looking to split since we're in the middle of an edge.
					 * if this got adjusted, do the check over again to see if the NEW active node
					 * has this branch/edge sticking out of it, else, we want to proceed
					 */
					if(this.activeLength >= nn.getLength()) {
						this.activeEdge = this.activeEdge + nn.getLength();
						this.activeLength = this.activeLength - nn.getLength();
						this.activeNode = nn;
						showstopper = Boolean.TRUE;
					} else {

						/* if the next character we're adding is already there:
						 * Rule 3: If the path from the root labelled S[j..i] ends at non-leaf edge 
						 * (i.e. there are more characters after S[i] on path) and next character is s[i+1]
						 * (already in tree), do nothing. 
						 */
						if(this.string.charAt(i) == this.string.charAt(nn.getStart() + this.activeLength)) {

							/* apply rule #2 again */
							if(this.lastNewNode != null && activeNode != this.root) {
								this.lastNewNode.setSuffixLink(this.activeNode);
								this.lastNewNode = null;
							}
							this.activeLength = this.activeLength + 1;

							/* show stopper rule */
							break;
						}

						/* time for some splitting since the character doesn't exist and we're in an edge */
						Node newNode = createNode(i, i, Boolean.TRUE);
						Node preSplit = createNode(nn.getStart(), nn.getStart() + this.activeLength - 1, Boolean.FALSE);
						this.activeNode.addChild(this.string.charAt(this.activeEdge), preSplit);
						nn.setStart(nn.getStart() + this.activeLength);					
						preSplit.addChild(this.string.charAt(nn.getStart()), nn);
						preSplit.addChild(this.string.charAt(i), newNode);

						if(this.lastNewNode != null) {
							this.lastNewNode.setSuffixLink(preSplit);
						}

						this.lastNewNode = preSplit;
					}
				}

				/* Don't do this if we tried to add on a char that already exists... (showstopper!!) */
				if(!showstopper) {
					/* we added one suffix in this loop!! */
					this.remainder = this.remainder - 1;

					/* if we added a suffix and activeLength > 0, there's a pattern
					 * where we add to the next shortest branch
					 */
					if(this.activeNode == this.root && this.activeLength > 0) {
						this.activeLength = this.activeLength - 1;
						this.activeEdge = i - this.remainder + 1;
					} else if(this.activeNode != this.root) {
						this.activeNode = this.activeNode.getSuffixLink();
					}
				}	
			}
		}
	}

	public void print() {
		printTree(this.root);
	}

	private void printTree(Node n) {
		if(n.getStart() != -1) {
			System.out.print(this.string.substring(n.getStart(), n.getEnd() + 1) + " ");
		}

		if(n.isLeaf()) {
			System.out.println("LEAF");
			return;
		}

		for(Character c : n.getChildren().keySet()) {
			printTree(n.getChild(c));
		}
	}

	public void mark() {
		traverse(this.root);
	}

	private Boolean[] traverse(Node n) {
		if(n.isLeaf()) {
			Boolean[] ret = new Boolean[2];
			if(n.getStart() <= this.splitIndex) {
				ret[0] = Boolean.TRUE;
				ret[1] = Boolean.FALSE;
			} else {
				ret[0] = Boolean.FALSE;
				ret[1] = Boolean.TRUE;
			}
			return ret;
		} else {
			ArrayList<Boolean[]> rets = new ArrayList<>();
			for(Character c : n.getChildren().keySet()) {
				rets.add(traverse(n.getChild(c)));
			}

			Boolean[] ret = new Boolean[2];
			ret[0] = Boolean.FALSE;
			ret[1] = Boolean.FALSE;

			for(int i = 0; i < rets.size(); i++) {
				if(rets.get(i)[0]) {
					ret[0] = Boolean.TRUE;
				}
				if(rets.get(i)[1]) {
					ret[1] = Boolean.TRUE;
				}
			}

			n.setType(ret[0], ret[1]);
			return ret;
		}
	}

	public String findLCS() {
		this.maxHeight = 0;
		this.substrStart = 0;
		findLCSHelper(this.root, 0);

		StringBuilder sb = new StringBuilder();

		for(int i = this.substrStart; i < this.substrStart + maxHeight; i++) {
			sb.append(this.string.charAt(i));
		}

		return sb.toString();
	}

	private void findLCSHelper(Node n, Integer h) {
		if(n == null) {
			return;
		}

		/* This will only occur when there's legitimately no similarities even at the root */
		if(!n.isTypeA() || !n.isTypeB()) {
			return;
		} else {
			for(Character c : n.getChildren().keySet()) {
				Node nn = n.getChild(c);
				if(nn.isTypeA() && nn.isTypeB()) {
					if(this.maxHeight < h + nn.getLength()) {
						this.maxHeight = h + nn.getLength();
						this.substrStart = nn.getEnd() - h - nn.getLength() + 1;
					}
					findLCSHelper(nn, h + nn.getLength());
				}
			}
		}
		return;
	}

	public static void main(String[] args) {
		SuffixTree tree = new SuffixTree("somemassivestring withspaces", "another massivestring we mayget");
		tree.buildTree();
		tree.mark();
		System.out.println(tree.findLCS());
	}
}