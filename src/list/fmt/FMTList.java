package list.fmt;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is a custom implementation of a sorted Linked List that sorts
 * elements based on a provided comparator
 * 
 * @author lucyb
 *
 * @param <T>
 */
public class FMTList<T> implements Iterable<T> {

	private int size;
	private FMTNode<T> head;
	private Comparator<T> comparator;

	public FMTList(Comparator<T> comparator) {
		this.size = 0;
		this.head = null;
		this.comparator = comparator;

	}

	public Comparator<T> getComparator() {
		return comparator;
	}

	/**
	 * Returns the number of elements currently stored inside the list
	 * 
	 * @return
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * This function clears out the contents of the list
	 */
	public void clear() {
		this.head = null;
		this.size = 0;
	}

	/**
	 * Gets node at a specific index
	 * 
	 * @param index
	 * @return
	 */
	private FMTNode<T> getNodeAtIndex(int index) {

		FMTNode<T> current = this.head;

		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}
		return current;
	}

	/**
	 * Adds an element to the sorted list. The new element is inserted into the list
	 * at the correct position based on the order of the comparator provided.
	 * 
	 * @param <T>     element
	 * 
	 * @param element
	 */
	public void add(T element) {

		FMTNode<T> newNode = new FMTNode<T>(element);

		if (this.head == null || this.comparator.compare(element, this.head.getElement()) < 0) {
			newNode.setNext(this.head);
			this.head = newNode;
		} else {

			FMTNode<T> current = this.head;

			while (current.getNext() != null && this.comparator.compare(element, current.getNext().getElement()) > 0) {
				current = current.getNext();
			}
			newNode.setNext(current.getNext());
			current.setNext(newNode);
		}
		this.size++;
	}

	/**
	 * Removes an element at the given index
	 * 
	 * @param index
	 * @return
	 */
	public T remove(int index) {

		if (index < 0 || index >= this.size) {
			throw new IllegalArgumentException("Index out of range");
		} else if (index == 0) {
			T element = head.getElement();
			this.head = this.head.getNext();
			this.size--;
			return element;
		} else {
			
			FMTNode<T> prev = this.getNodeAtIndex(index - 1);
			FMTNode<T> current = prev.getNext();
			FMTNode<T> next = current.getNext();
			
			prev.setNext(next);
			this.size--;
			return current.getElement();
		}
	}

	/**
	 * Returns an element at a given index
	 * 
	 * @param index
	 * @return
	 */
	public T getElementAtIndex(int index) {

		if (index < 0 || index >= this.size) {
			throw new IllegalArgumentException("Index out of range");
		} else {
			return this.getNodeAtIndex(index).getElement();
		}
	}

	/**
	 * Prints list of elements to the standard output
	 */
	public String toString() {

		StringBuilder sb = new StringBuilder();
		FMTNode<T> current = this.head;
		while (current != null) {
			sb.append(current.getElement());
			current = current.getNext();
		}
		return sb.toString();
	}

	/**
	 * Iterates over the elements in the LinkedList
	 * 
	 * @return
	 */
	@Override
	public Iterator<T> iterator() {

		return new Iterator<T>() {
			private FMTNode<T> current = head;

			@Override
			public boolean hasNext() {
				if (current != null) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public T next() {
				if (current != null) {
					T element = current.getElement();
					current = current.getNext();
					return element;
				} else {
					throw new NoSuchElementException("No more elements in the list.");
				}
			}
		};
	}
}