package oop.ex4.data_structures;

/**
 * This class defines a node within an AVL search tree. Each node in the tree has a key
 * and holds the nodes objects of his father and left and right son.
 * Additionally the node holds his height - the distance from the farthest leaf
 * (with the height of a node that is a leaf being 0).
 * @author alonav11
 *
 */
public class AvlNode {
	
	public static final int NODE_STARTING_HEIGHT = 0;
	
	private int key;
	private int height;
	private AvlNode parent;
	private AvlNode leftChild;
	private AvlNode rightChild;
	
	/**
	 * Constructor. Creates a new node under the given parent, holding the given key. 
	 * The height of a newly create node is always 0, is it has no children yet. 
	 * @param key
	 * @param parent
	 */
	
	
	public AvlNode(int key, AvlNode parent){
		this.key = key;
		this.parent = parent;
		height = NODE_STARTING_HEIGHT;
		leftChild = null;
		rightChild = null;
		
	}
	
	
	/**
	 * @return Node's key
	 */
	public int getKey(){
		return key;
	}
	
	/**
	 * @param newKey Number to use as the node's key.
	 */
	public void setKey(int newKey){
		key = newKey;
	}
	
	/**
	 * @return current height of node.
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * @param height The number to set as the height.
	 */
	public void setHeight(int height){
		this.height = height;
	}
	
	/**
	 * @return Node object of current parent
	 */
	public AvlNode getParent(){
		return parent;
	}
	
	/**
	 * @param newParent Node to set as new parent
	 */
	public void setParent(AvlNode newParent){
		parent = newParent;
	}
	
	/**
	 * @return Node object of left child
	 */
	public AvlNode getLeftChild(){
		return leftChild;
	}
	
	/**
	 * @param newLeftChild Node to set as new left child
	 */
	public void setLeftChild(AvlNode newLeftChild){
		leftChild = newLeftChild;
		
	}
	
	/**
	 * Removes the left child by pointing its reference to null.
	 */
	public void removeLeftChild(){
		leftChild = null;
	}
	
	
	/**
	 * @return Node object of right child
	 */
	public AvlNode getRightChild(){
		return rightChild;
	}
	
	/**
	 * @param newLRightChild Node to set as new right child
	 */
	public void setRightChild(AvlNode newRightChild){
		rightChild = newRightChild;
		
	}
	
	/**
	 * Removes the right child by pointing its reference to null.
	 */
	public void removeRightChild(){
		rightChild = null;
	}

}
