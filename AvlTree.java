/**
 * 
 */
package oop.ex4.data_structures;

import java.util.Iterator;

/**
 * This class represents an AVL tree. It can add, remove and search nodes within the tree.
 * By using the AVL rotation algorithms, every time a node is deleted or added, it ensures 
 * that no two sub nodes have a height difference that is larger than 1.
 * @author alonav11
 *
 */
public class AvlTree implements Iterable<Integer>{
	
	private static final int MAXIMAL_NODE_DIFFERENCE = 1;
	private static final int NULL_MARKER = -1;
	private static final int DEPTH_OF_ROOT = 0;
	private static final int NODE_NOT_FOUND = -1;
	
	private static final int MIN_NODES_FOR_HEIGHT_ZERO = 1;
	private static final int MIN_NODES_FOR_HEIGHT_ONE = 2;
	private static final int MIN_NODE_FORMULA_CONSTANT = 1;
	private static final int PREVIOUS_LEVELS_IN_RECUSRSION = 2;
	
	private static final int RR_ROTATION = 1, 
							 LL_ROTATION = 2, 
							 RL_ROTATION = 3, 
							 LR_ROTATION = 4;
	
	
	
	private int size;
	private AvlNode root;
	
		
	/**
	 * Calculates the minimum number of nodes in an AVL tree of
	 * height h. The minimal number of nodes in a tree would be if the last level had only a single 
	 * node in it. According to a theorem taugh in the DAST course, for height of tree k (k>=2) the 
	 * minimal number of nodes in the tree Nk equals N(k-1) + N(k-2) + 1. Additionally, N0 = 1,
	 * N1 = 2. So we have a recursive formula to calculate Nh.
	 * Note: Since there are many numbers here, 
	 * @param h The height of the tree (a non-negative number) in question.
	 * @return The minimum number of nodes in an AVL tree of the given height.
	 */
	public static int findMinNodes(int h){
		// nh is the symbol for the minimal number of nodes at height h.
		int nhMinusTwo = MIN_NODES_FOR_HEIGHT_ZERO;
		int nhMinusOne = MIN_NODES_FOR_HEIGHT_ONE;
		int nh = NULL_MARKER; //An error marker. If nh is returned, it should always receive a value
					 // within the loop.
		int temp;
		
		if(h == 0){
			return MIN_NODES_FOR_HEIGHT_ZERO;
		}else if(h == 1){
			return MIN_NODES_FOR_HEIGHT_ONE;
		}
		
		for(int i=0;i<=h - PREVIOUS_LEVELS_IN_RECUSRSION;i++){
			nh = nhMinusOne + nhMinusTwo + MIN_NODE_FORMULA_CONSTANT;
			temp = nhMinusOne;
			nhMinusOne = nh;
			nhMinusTwo = temp;
		}
		return nh;
	}
	
	
	/**
	 * The default constructor;
	 */
	public AvlTree(){
		size = 0;	
		root = null;
	}
	
	/**
	 * A constructor that builds the tree by adding the elements
	 * in the input array one by one. If a value appears more than once
	 * in the list, only the first appearance is added.
	 * @param data The values to add to the tree.
	 */
	public AvlTree(int[] data){
		this(); //Calling the default constructor that initializes the data members.
		if(data != null){
			for(int key:data){
				add(key);
			}
		}
		
	}
	
	/**
	 * A copy constructor that creates a deep copy of the given
	 * AvlTree. This means that for every node or any other internal object of the given
	 * tree, a new, identical object, is instantiated for the new tree (the internal object
	 * is not simply referenced from it). The new tree must contain all the values of the given
	 * tree, but not necessarily in the same structure.
	 * @param avlTree An AVL tree.
	 */
	public AvlTree(AvlTree avlTree){
		this(); //Calling the default constructor that initializes the data members.
		if(avlTree != null){
			for(int key:avlTree){
				add(key);
			}
		}
			
	}
	

	
	
	/**
	 * Add a new Node with the given key to the tree.
	 * 
	 * @param newValue the value of the new node to add.
	 * @return true if the value to add is not already in the tree
	 * and it was successfully added, false otherwise.
	 * 
	 */
	
	public boolean add(int newValue){
		AvlNode addedNode;
		
		if(root == null){
			root = new AvlNode(newValue, null); //If the tree was empty, adds the value as the new root.
			size ++;
			return true;
		}else{
			try{
				addedNode = addHelper(newValue, root);
			}
			catch (IllegalArgumentException exeption){
				System.out.println("ERROR!! Illegal Argument Received");
				addedNode = null;
			}
			if(addedNode == null){ // If the added node is null, the node already existed in the tree.
				return false;
			}else{
				try{
					adjustTree(addedNode.getParent()); // The new node has no children, so it's height is 0 
					// and cannot be unbalanced. Therefore adjusting the node's heights and rotating starts 
					//from his parent.
				}
				catch (IllegalArgumentException exeption){
					System.out.println("ERROR!! Illegal Argument Received");
					return false;
				}
				
				size ++;
				return true;
			}
		}
	}
	
	/**
	 * Removes the node with the given value from the tree,
	 * if it exists. If the found node has one child, he is replaced with his child.
	 * If he has no children, he is simply deleted. If he has two children, he is replaced with his
	 * successor, and the successor (who has 0 or 1 kids) is removed as stated above.
	 * @param toDelete the value to remove from the tree.
	 * @return true if the given value was found and deleted,
	 * false otherwise. 
	 * 
	 */
	public boolean delete(int toDelete){
		AvlNode successor;
		AvlNode nodeToRemove = findNode(toDelete, root);
		if(nodeToRemove == null){
			return false;
			
		}else{
			if(nodeToRemove.getLeftChild() != null && nodeToRemove.getRightChild() != null){
				try{
				successor = getSuccessor(nodeToRemove); //If the node has a right child he has 
														// to have a successor
				}
				catch (IllegalArgumentException exeption){
					System.out.println("ERROR!! Illegal Argument Received");
					successor = null;
				}
				nodeToRemove.setKey(successor.getKey());
				try{
				removeNode(successor);
				}
				catch (IllegalArgumentException exeption){
					System.out.println("ERROR!! Illegal Argument Received");
					return false;
				}
			}else{
				try{
				removeNode(nodeToRemove);
				}
				catch (IllegalArgumentException exeption){
					System.out.println("ERROR!! Illegal Argument Received");
					return false;
				}
				
			}
			size --;
			return true;
			
		}
		
	}
	
	/**
	 * Check whether the tree contains the given input value.
	 * @param searchVal The value to search for.
	 * @return The depth of the node (0 for the root) with the
	 * given value if it was found in the tree, -1 otherwise.
	 */
	public int contains(int searchVal){
		AvlNode currentNode = root;
		int depthCounter = DEPTH_OF_ROOT;
		
		while(currentNode != null){
			if(currentNode.getKey() == searchVal){
				return depthCounter;
			}else if(currentNode.getKey() < searchVal){
				depthCounter ++;
				currentNode = currentNode.getRightChild();
			}else{
				depthCounter ++;
				currentNode = currentNode.getLeftChild();
			}
		}
		return NODE_NOT_FOUND;
		
	}
	
	/**
	 * @return The Number of nodes in the tree
	 */
	public int size(){
		return size;
		
	}
	
	/**
	 * @return An iterator on the Avl Tree. The returned iterator iterates
	 * over the tree nodes in an ascending order, and does not implement
	 * the remove() method. 
	 * 
	 */
	
	public Iterator<Integer> iterator() {
		return new TreeIterator();
	}
	

	
	
	/*
	 * Returns the successor of the given node. If the node has a right child
	 * it will find the successor by getting the minimum of the right child's sub tree.
	 * This is done by going from the right child all the way down to the left. 
	 * If the node dosen't have a right child, it will go up the tree to the left until it arrives
	 * at the first right turn, and return that node. Returns null if there is no successor (if the node
	 * is the maximum node in the tree).
	 * Throws an exception if the given node is null. This Should never happen.
	  */
	
	private AvlNode getSuccessor(AvlNode node) throws IllegalArgumentException{
		AvlNode currentNode;
		AvlNode parent;
		
		if (node == null){
			throw new IllegalArgumentException();
		}
		
		if(node.getRightChild() != null){
				currentNode = node.getRightChild();
			while(currentNode.getLeftChild() != null){
				currentNode = currentNode.getLeftChild();
			}
			return currentNode;
		}else{
			parent = node.getParent();
			while(parent != null && parent.getRightChild() == node){
				node = parent;
				parent = parent.getParent();
				
			}
			return parent;			
		}
	}
	
	
	/*
	 * Returns the node that has the given key. If the node doesn't exist, returns null.
	 * Note that the method will return null if the given currentNode is empty (in the case
	 * the tree is empty).
	 */
	private AvlNode findNode(int key, AvlNode currentNode){
		if(currentNode == null){
			return null;
		}
		
		if(currentNode.getKey() == key){
			return currentNode;
		}else if(currentNode.getKey() < key){
			return findNode(key, currentNode.getRightChild());
		}else{
			return findNode(key,currentNode.getLeftChild());			
		}
	}
	
	
	
	/*
	 * Traverses down the AVL tree recursively until it gets to the relevant place and
	 * adds the node with the given key, if the key didn't already exist.
	 * @param newValue The new key to be added to the tree
	 * @param currentNode The node to start the recursion from
	 * @return the added node iff the node was added. If it wansn't added since the key already existed,
	 * returns null.
	 * 
	 */
	private AvlNode addHelper (int newValue, AvlNode currentNode) throws IllegalArgumentException{
		int currentKey;
		
		if(currentNode == null){
			throw new IllegalArgumentException();
		}
		
		currentKey = currentNode.getKey();
		
		if(currentKey == newValue){
			return null;
			
		}else if(currentKey < newValue){
			if(currentNode.getRightChild() != null){
				return addHelper(newValue, currentNode.getRightChild());
				
			}else{
				AvlNode newNode = new AvlNode(newValue, currentNode);
				currentNode.setRightChild(newNode);
				return newNode;
			}
			
		}else{
			if(currentNode.getLeftChild() != null){
				return addHelper(newValue, currentNode.getLeftChild());
				
			}else{
				AvlNode newNode = new AvlNode(newValue, currentNode);
				currentNode.setLeftChild(newNode); 
				return newNode;
			}	
		}
	}
	
	
	/*
	 * Traverses the tree recursively from the bottom until it reaches the root. In each run
	 * it updates the height of the current node. It then rotates the tree around that node 
	 * if it is unbalanced.  When it reaches the root, if there is a new root it changes the 
	 * variable accordingly. If no rotation occurred, updates the current node's height. If a 
	 * rotation did occur, the node's height is already taken care of in the rotation algorithm.
	 */
	private void adjustTree (AvlNode currentNode) throws IllegalArgumentException{
		AvlNode originalParent;
		AvlNode newSubtreeRoot;
		
		
		//The recursion keeps going until it reaches the root's parent, which is null.
		if(currentNode != null){
			
			// Since the current node's parent might change, saving it so the recursion can keep 
			// going up the tree.
			originalParent = currentNode.getParent();
			
			newSubtreeRoot =  rotateNode(currentNode);
			if(newSubtreeRoot == currentNode){ //If no rotation happened
				updateHeight(currentNode);
			}
						
			if (currentNode == root){
				root = newSubtreeRoot;
			}
			
			adjustTree(originalParent);
		}
			
	}
	
	/*
	 * This method receives a node and checks if it is unbalanced (according to the height 
	 * of its two children). If so, it checks where the imbalance is (according to the height of its
	 * children and grandchildren. The node and his relevant child (in the case of LR/RL imbalances) 
	 * are then rotated according to the AVL algorithm. A node cannot rotate if it has no children, or 
	 * if it doesn't have at least one grandson from his taller child. In these cases an exception will 
	 * be thrown as this must be a programming error Returns the new root of the subtree if there was a 
	 * change, and returns the given node otherwise.
	 */
	private AvlNode rotateNode(AvlNode node) throws IllegalArgumentException{
		
		int rightChildHeight = getNodeHeight(node.getRightChild());		
		int leftChildHeight = getNodeHeight(node.getLeftChild());
		


		// Note: if both children are null, they both have a height of -1. Since -1 - (-1) = 0,
		// There will be no rotation in this case
		
		if (Math.abs(rightChildHeight - leftChildHeight) >  MAXIMAL_NODE_DIFFERENCE){		
			
			// Note: If the right/left child is higher then his sibling, then it must have children.
			// Therefore when the right/left child is respectively called, we can be sure it isn't null.

			if(rightChildHeight > leftChildHeight){
				if(node.getRightChild().getLeftChild() == null && 
									node.getRightChild().getRightChild() == null){
					throw new IllegalArgumentException();
				}
				
				// If only one grandchild exists - RR for right one, RL for left one.
				if(node.getRightChild().getLeftChild() != null ^ 
									node.getRightChild().getRightChild() != null){
					if(node.getRightChild().getLeftChild() != null){
						rotateByScenario(node, RL_ROTATION);
					}else{
						rotateByScenario(node, RR_ROTATION);
					}
				// If both grandchildren exist - choose rotation scenario according to the higher one.
				// If they are at the same height choose RR
				}else{
					if(node.getRightChild().getLeftChild().getHeight() >
								node.getRightChild().getRightChild().getHeight()){
						rotateByScenario(node, RL_ROTATION);
						
					}else{
						rotateByScenario(node, RR_ROTATION);
					}
				}
				
			// If the left child is taller (they can't be the same height - as this was checked earlier)
			}else{
				
				if(node.getLeftChild().getLeftChild() == null &&
									node.getLeftChild().getRightChild() == null){
					throw new IllegalArgumentException();
				}
				
				// If only one grandchild exists - LL for left one, LR for right one.
				if(node.getLeftChild().getLeftChild() != null ^ 
									node.getLeftChild().getRightChild() != null){
					if(node.getLeftChild().getRightChild() != null){
						rotateByScenario(node, LR_ROTATION);
					}else{
						rotateByScenario(node, LL_ROTATION);
					}
				// If both grandchildren exist - choose rotation scenario according to the higher one.
				// If they are at the same height choose LL
				}else{
					if(node.getLeftChild().getRightChild().getHeight() >
								node.getLeftChild().getLeftChild().getHeight()){
						rotateByScenario(node, LR_ROTATION);
						
					}else{
						rotateByScenario(node, LL_ROTATION);
					}
				}
			}
			return node.getParent();
		}else{
			return node;
		}
	}
				
				
	/*
	 * This method receives a node and rotates its tree according to the given scenario (RR, LL, RL, LR).
	 * If the relevant child or grandchild doesn't exist as required by the scenarios 
	 * (for instance a right child and a right-left grandchild for RL), an exception is 
	 * thrown as this should never happen.
	 * 	
	 */
	private void rotateByScenario(AvlNode node, int rotationScenario) throws IllegalArgumentException{
		
		switch(rotationScenario){
		
		case RR_ROTATION:	if(node.getRightChild() == null){throw new IllegalArgumentException();}
							rotateLeft(node);
							break;
							
		case LL_ROTATION:	if(node.getLeftChild() == null){throw new IllegalArgumentException();}
							rotateRight(node);
							break;
							
		case RL_ROTATION:	if(node.getRightChild() == null || 
												node.getRightChild().getLeftChild() == null){
															throw new IllegalArgumentException();}
							rotateRight(node.getRightChild());
							rotateLeft(node);
							break;
							
		case LR_ROTATION:	if(node.getLeftChild() == null || 
													node.getLeftChild().getRightChild() == null ){
															throw new IllegalArgumentException();}
							rotateLeft(node.getLeftChild());
							rotateRight(node);
							break;
							
		// Shouldn't happen that there is a different scenario code. 						
		default:			throw new IllegalArgumentException();
		}
		
		
	}
	
	
	/*
	 * This method receives an unbalanced node and shifts its sub tree to
	 * the right. This means it puts his left child as his parent, and becomes his left child's right child.
	 * At the end the original node and his left child's heights are updated.
	 * Throws an exception the given node doesn't have a left child. Rotation isn't possible in this case.
	 * An exception is thrown in this case since in a correct operation of the tree, this method shouldn't
	 * be called if the node doesn't have a  left child. If it happens, it must be a programming error. 
	 */
	private void rotateRight(AvlNode node) 
			throws IllegalArgumentException {
		
		AvlNode leftChild = node.getLeftChild();
		AvlNode parent = node.getParent();
		
		if(leftChild == null){
			throw new IllegalArgumentException();
		}
		
		leftChild.setParent(parent);
		
		//If the given node has a parent, that parent's
		//The node's left child needs to take his place. 
		//For this we need to determine if the given node was the parent's 
		//left or right child.
		
		if(parent != null){
			if(parent.getLeftChild() == node){
				parent.setLeftChild(leftChild);
			}
			else{
				parent.setRightChild(leftChild);
			}
		}
		
		node.setParent(leftChild);
		
		// If the left child has a right child, it becomes the given node's left child.
		// This frees up the right child, so the given node can be put in its place. If the left 
		//child doen't have a right child, the given node's left child becomes null.
		// After this switch the tree remains a valid BST, according to the AVL rotation algorithm.
		
		AvlNode leftRightChild = leftChild.getRightChild();
		if (leftRightChild != null){
			leftRightChild.setParent(node);
		}
		node.setLeftChild(leftRightChild);
		
		leftChild.setRightChild(node);	
		
		updateHeight(node);
		updateHeight(leftChild);
		
	}
	
	
	
	/*
	 * This method receives an unbalanced node and shifts its sub tree to
	 * the left. This means it puts his right child as his parent, and becomes his right child's left child. 
	 * At the end the original node and his right child's heights are updated.
	 * Throws an exception the given node doesn't have a right child. Rotation isn't possible in this case.
	 * An exception is thrown in this case since in a correct operation of the tree, 
	 * this method shouldn't be called if the node doesn't have a right child.
	 * If it happens, it must be a programming error. */
	
	private void rotateLeft(AvlNode node) throws IllegalArgumentException{
		
		AvlNode rightChild = node.getRightChild();
		AvlNode parent = node.getParent();
		
		if (rightChild == null){
			throw new IllegalArgumentException();
		}
		
		
		rightChild.setParent(parent);
		
		//If the given node has a parent, that parent's
		//The node's left child needs to take his place. 
		//For this we need to determine if the given node was the parent's 
		//left or right child.
		
		if(parent != null){
			if(parent.getLeftChild() == node){
				parent.setLeftChild(rightChild);
			}
			else{
				parent.setRightChild(rightChild);
			}
		}
		
		node.setParent(rightChild);
		
		// If the right child has a left child, it becomes the given node's right child.
		// This frees up the left child, so the given node can be put in its place.
		//If the right child doen't have a left child, the given node's right child becomes null.
		// After this switch the tree remains a valid BST, according to the AVL rotation algorithm.
		
		AvlNode rightLeftChild = rightChild.getLeftChild();
		if(rightLeftChild != null){
			rightLeftChild.setParent(node);
		}
		node.setRightChild(rightLeftChild);
		
		rightChild.setLeftChild(node);
		
		updateHeight(node);
		updateHeight(rightChild);
	
	}
	
	
	/*
	 * Returns the node's height if it exists, and returns -1 if the node is null.
	 */
	private int getNodeHeight(AvlNode node){
		if(node != null){
			return node.getHeight();
		}else{
			return NULL_MARKER;
		}
	}
	
	/*
	 * Updates the height of the node as the maximum height of its children + 1.
	 * 
	 */
	private void updateHeight(AvlNode node) throws IllegalArgumentException{
		int leftChildHeight;
		int rightChildHeight;
		
		if(node == null){
			throw new IllegalArgumentException();
		}
		
		leftChildHeight= getNodeHeight(node.getLeftChild());
		rightChildHeight= getNodeHeight(node.getRightChild());
		node.setHeight(Math.max(leftChildHeight, rightChildHeight) + 1);
	}
	
	
	/*
	 * Receives a node without children or with a single child and updates and rotates 
	 * the tree to maintain the AVL requirements. Throws an exception if the node has two children.
	 * If the removed node was the root, his child will be assigned as the new root.
	 */
	private void removeNode(AvlNode node) throws IllegalArgumentException{
		
		AvlNode replacementNode; // The node that will come instead of the
								 // deleted node.
		
		if(node.getLeftChild() != null && node.getRightChild() != null){
			throw new IllegalArgumentException();
		}
		
		if(node.getLeftChild() != null){
			replacementNode = node.getLeftChild();
			replacementNode.setParent(node.getParent());
			
		}else if(node.getRightChild() != null){
			replacementNode = node.getRightChild();
			replacementNode.setParent(node.getParent());
			
		}else{ // If there are no children, the current node is "replaced" with null.
			replacementNode = null;
		}
		
		//If the node's parent was null, its replacement is now the root.
		if(node.getParent() == null){
			root = replacementNode;
		}else{
			if(node.getParent().getLeftChild() == node){
				node.getParent().setLeftChild(replacementNode);
			}else{
				node.getParent().setRightChild(replacementNode);
			}
			adjustTree(node.getParent());
			
		}
	}
	
	
	/*
	 * Returns the minimal node in the key by going all the way down left from the root.
	 */
	private AvlNode getMin(){
		AvlNode currentNode = root;
		if(currentNode == null){
			return null;
		}
		
		while(currentNode.getLeftChild() != null){
			currentNode = currentNode.getLeftChild();
			
		}
		return currentNode;
	}
	

	/**
	 * Iterates over an AvlTree, from the smallest key to the largest. This is implemented
	 * by staring with the minimal node and finding it's successor each iteration.
	 * For each iteration, the key of the node is returned.
	 * @author alonav11
	 *
	 */
	private class TreeIterator implements Iterator<Integer>{
		
		private AvlNode nextNode;
		int currentNodeKey;
		
		
		/**
		 * Constructor. Saves the Avl tree that is to be iterated and 
		 * @param tree
		 */
		public TreeIterator(){
			nextNode = getMin();
			
			
		}
		
		public boolean hasNext(){
			return nextNode != null;
			
		}

		public Integer next() {
			currentNodeKey = nextNode.getKey();
			try{
				nextNode = getSuccessor(nextNode);
			}
			catch (IllegalArgumentException exeption){
				System.out.println("ERROR!! Illegal Argument Received");
				nextNode = null;
			}
			return currentNodeKey;
			
		}

		public void remove() {
			throw new UnsupportedOperationException();
			
		}

	}

}
	 	
	

	


