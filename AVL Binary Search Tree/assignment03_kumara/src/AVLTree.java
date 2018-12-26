import java.util.Comparator;

public class AVLTree<T> {

	private Node<T> root;
	private int size;

	private final Comparator<T> comparator;

	public AVLTree(Comparator<T> comparator) {
		this.comparator = comparator;
		root = null;
	}

	public boolean isEmpty() {
		return root == null;
	}

	public void clear() {
		root = null;
	}

	public Node<T> getRoot() {
		return root;
	}

	public int size() {
		return size;
	}

	public static int size(Node<?> node) {
		int size = 1;
		if (node.left != null) {
			size += size(node.left);
		}
		if (node.right != null) {
			size += size(node.right);
		}
		return size;
	}

	public void add(T data) {
		Node<T> node = new Node<T>(data);
		if (root == null) {
			setRoot(node);
			makeLeafNode(node);
		} else {
			add(root, node);
			balance(node);
		}
		size++;
	}

	private void add(Node<T> base, Node<T> node) {
		int compare = comparator.compare(node.getData(), base.getData());

		if (compare < 0) {
			if (base.left == null) {
				tieLeft(base, node);
				makeLeafNode(node);
				updateNodeHeightFromChildren(node.parent);
			} else {
				add(base.left, node);
			}
		} else {
			if (base.right == null) {
				tieRight(base, node);
				makeLeafNode(node);
				updateNodeHeightFromChildren(node.parent);
			} else {
				add(base.right, node);
			}
		}
	}

	private void addAllSubNodes(Node<T> base, Node<T> node) {
		if (node == null)
			return;
		addAllSubNodes(base, node.left);
		addAllSubNodes(base, node.right);
		add(base, node);
	}

	public boolean delete(T data) {
		Node<T> node = get(data);
		if (node == null)
			return false;
		deleteNode(node);
		size--;
		return true;
	}

	private void deleteNode(Node<T> node) {
		if (isLeafNode(node)) {
			if (node == root) {
				setRoot(null);
			} else {
				Node<T> parent = node.parent;
				replace(node, null);
				updateNodeHeightFromChildren(parent);

				balance(parent);
			}
		} else if (node.left == null) {
			if (node == root) {
				setRoot(node.right);
			} else {
				// tie parent and right child
				if (node.parent.left == node) {
					tieLeft(node.parent, node.right);
				} else {
					tieRight(node.parent, node.right);
				}
			}
			updateNodeHeightFromChildren(node.right);
			balance(node.parent);
		} else if (node.right == null) {
			if (node == root) {
				setRoot(node.left);
			} else {
				// tie parent and left child
				if (node.parent.left == node) {
					tieLeft(node.parent, node.left);
				} else {
					tieRight(node.parent, node.left);
				}
			}
			updateNodeHeightFromChildren(node.left);
			balance(node.parent);
		} else {
			Node<T> replacement = getLowestNode(node.right);
			deleteNode(replacement);
			replace(node, replacement);
			if (node == root) {
				setRoot(replacement);
			}
			updateNodeHeightFromChildren(replacement);
		}

	}

	public Node<T> getLeastValueNode() {
		return getLowestNode(root);
	}

	private static <T> Node<T> getLowestNode(Node<T> node) {
		Node<T> res = node;
		while (res.left != null) {
			res = res.left;
		}
		return res;
	}

	public boolean contains(T data) {
		return get(data) != null;
	}

	public Node<T> get(T data) {
		return get(data, root);
	}

	private static <T> Node<T> get(T data, Node<T> node) {
		if (node.getData().equals(data))
			return node;
		if (node.left != null) {
			Node<T> res = get(data, node.left);
			if (res != null)
				return res;
		}
		if (node.right != null) {
			Node<T> res = get(data, node.right);
			if (res != null)
				return res;
		}
		return null;
	}

	public int getTreeHeight() {
		return getNodeHeight(root);
	}

	public static int getNodeHeight(Node<?> node) {
		if (node == null)
			return 0;
		return node.height;
	}

	private static void updateNodeHeightFromChildren(Node<?> node) {
		if (node == null)
			return;
		node.height = 1 + Math.max(getNodeHeight(node.left), getNodeHeight(node.right));
		updateNodeHeightFromChildren(node.parent);
	}

	private static boolean isLeafNode(Node<?> node) {
		return node.left == null && node.right == null;
	}

	private static int weighNode(Node<?> node) {
		int leftheight = getNodeHeight(node.left);
		int rightheight = getNodeHeight(node.right);
		return rightheight - leftheight;
	}

	private void balance(Node<T> node) {
		// Main.displayer.refreshTree();
		// Main.displayer.repaint();

		if (node == null)
			return;

		int weight = weighNode(node);

		// if (weight > 1) {
		// // right too heavy, rotate left
		// rotateNormalLeft(node);
		// Node<T> replacement = node.parent;
		// if (weighNode(replacement) < -1) {
		// // counteracted too much, left too heavy, rotate weird right
		// rotateWeirdRight(replacement);
		// replacement = replacement.parent;
		// }
		// balance(replacement);
		// return;
		// } else if (weight < -1) {
		// // left too heavy, rotate right
		// rotateNormalRight(node);
		// Node<T> replacement = node.parent;
		// if (weighNode(replacement) > 1) {
		// // counteracted too much, right too heavy, rotate weird left
		// rotateWeirdLeft(replacement);
		// replacement = replacement.parent;
		// }
		// balance(replacement);
		// return;
		// }
		if (weight > 1) {
			// right too heavy, rotate left
			if (weighNode(node.right) > 0) {
				rotateNormalLeft(node);
			} else {
				rotateWeirdLeft(node);
			}
		} else if (weight < -1) {
			// left too heavy, rotate right
			if (weighNode(node.left) < 0) {
				rotateNormalRight(node);
			} else {
				rotateWeirdRight(node);
			}
		}

		balance(node.parent);

	}

	// ori = original
	private void rotateNormalRight(Node<T> orinode) {
		Node<T> orileft = orinode.left;
		Node<T> orileftright = orileft.right;

		if (orinode.parent != null) {
			if (orinode.parent.right == orinode) {
				tieRight(orinode.parent, orileft);
			} else if (orinode.parent.left == orinode) {
				tieLeft(orinode.parent, orileft);
			}
		} else {
			setRoot(orileft);
		}

		tieRight(orileft, orinode);

		tieLeft(orinode, orileftright);

		updateNodeHeightFromChildren(orinode);
	}

	private void rotateNormalLeft(Node<T> orinode) {
		Node<T> oriright = orinode.right;
		Node<T> orirightleft = oriright.left;

		if (orinode.parent != null) {
			if (orinode.parent.left == orinode) {
				tieLeft(orinode.parent, oriright);
			} else if (orinode.parent.right == orinode) {
				tieRight(orinode.parent, oriright);
			}
		} else {
			setRoot(oriright);
		}

		tieLeft(oriright, orinode);

		tieRight(orinode, orirightleft);

		updateNodeHeightFromChildren(orinode);
	}

	private void rotateWeirdRight2(Node<T> orinode) {
		Node<T> orileft = orinode.left;
		Node<T> orileftright = orileft.right;

		Node<T> leftadd = orileftright.left;
		Node<T> rightadd = orileftright.right;

		if (orinode.parent != null) {
			if (orinode.parent.right == orinode) {
				tieRight(orinode.parent, orileftright);
			} else if (orinode.parent.left == orinode) {
				tieLeft(orinode.parent, orileftright);
			}
		} else {
			setRoot(orileftright);
		}
		tieLeft(orileftright, orileft);
		orileft.right = null;

		tieRight(orileftright, orinode);
		orinode.left = null;

		addAllSubNodes(orileftright, leftadd);
		addAllSubNodes(orileftright, rightadd);

		updateNodeHeightFromChildren(orinode);
		updateNodeHeightFromChildren(orileft);
	}

	private void rotateWeirdRight(Node<T> orinode) {
		rotateNormalLeft(orinode.left);
		rotateNormalRight(orinode);
	}

	private void rotateWeirdLeft2(Node<T> orinode) {
		Node<T> oriright = orinode.right;
		Node<T> orirightleft = oriright.left;

		Node<T> rightadd = orirightleft.right;
		Node<T> leftadd = orirightleft.left;

		if (orinode.parent != null) {
			if (orinode.parent.left == orinode) {
				tieLeft(orinode.parent, orirightleft);
			} else if (orinode.parent.right == orinode) {
				tieRight(orinode.parent, orirightleft);
			}
		} else {
			setRoot(orirightleft);
		}
		tieRight(orirightleft, oriright);
		oriright.left = null;

		tieLeft(orirightleft, orinode);
		orinode.right = null;

		addAllSubNodes(orirightleft, rightadd);
		addAllSubNodes(orirightleft, leftadd);

		updateNodeHeightFromChildren(orinode);
		updateNodeHeightFromChildren(oriright);
	}

	private void rotateWeirdLeft(Node<T> orinode) {
		rotateNormalRight(orinode.right);
		rotateNormalLeft(orinode);
	}

	private void setRoot(Node<T> node) {
		root = node;
		if (node != null) {
			node.parent = null;
		}
	}

	private static void makeLeafNode(Node<?> node) {
		node.left = null;
		node.right = null;
		node.height = 1;
	}

	private void replace(Node<T> oldnode, Node<T> newnode) {
		if (oldnode == null)
			throw new RuntimeException("ERROR LOOK 1");
		if (oldnode == root) {
			setRoot(newnode);
		} else if (oldnode.parent.left == oldnode) {
			tieLeft(oldnode.parent, newnode);
		} else if (oldnode.parent.right == oldnode) {
			tieRight(oldnode.parent, newnode);
		}
		tieLeft(newnode, oldnode.left);
		tieRight(newnode, oldnode.right);
	}

	private static <T> void tieRight(Node<T> parent, Node<T> child) {
		if (parent != null) {
			parent.right = child;
		}
		if (child != null) {
			child.parent = parent;
		}
	}

	private static <T> void tieLeft(Node<T> parent, Node<T> child) {
		if (parent != null) {
			parent.left = child;
		}
		if (child != null) {
			child.parent = parent;
		}
	}

	public boolean isProperlyTied() {
		if (root.parent != null)
			return false;
		return isProperlyTied(root);
	}

	private boolean isProperlyTied(Node<T> node) {
		if (node.left != null) {
			if (node.left.parent != node)
				return false;
			if (!isProperlyTied(node.left))
				return false;
		}
		if (node.right != null) {
			if (node.right.parent != node)
				return false;
			if (!isProperlyTied(node.right))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(root);
	}
}

class Node<T> {

	private final T data;

	public Node<T> parent;
	public Node<T> left;
	public Node<T> right;

	public int height;

	public Node(T data) {
		this(data, null, null, null, 1);
	}

	public Node(T data, Node<T> parent, Node<T> left, Node<T> right, int height) {
		this.data = data;
		this.parent = parent;
		this.left = left;
		this.right = right;
		this.height = height;
	}

	public T getData() {
		return data;
	}

	@Override
	public String toString() {
		String s = "";
		if (left != null) {
			s += "{" + left + "} ";
		}
		s += data.toString();
		if (right != null) {
			s += " [" + right + "]";
		}
		return s;
	}
}
