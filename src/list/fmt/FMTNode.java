package list.fmt;

/**
 * This class represents a node in the FMTList Linked List
 * 
 * @author lucyb
 *
 * @param <T> The type of element stored in the node
 */
public class FMTNode<T> {

	private FMTNode<T> next;
	private T element;

	public FMTNode(T element) {
		this.next = null;
		this.element = element;
	}

	public FMTNode<T> getNext() {
		return next;
	}

	public void setNext(FMTNode<T> next) {
		this.next = next;
	}

	public T getElement() {
		return element;
	}

	public void setElement(T element) {
		this.element = element;
	}

}
