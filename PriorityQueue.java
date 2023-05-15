/**
 * [PriorityQueue.java]
 * Class representing a custom simple priority queue.
 *
 * @param <E> the object type for the priority queue
 * @author Sherwin Okhowat
 * @version 1.0
 * @since 2023-05-10
 */
public class PriorityQueue<E> {
  private PriorityQueueNode<E> head;
  private PriorityQueueNode<E> tail;

  /**
   * Dequeues the lowest priority item in the priority queue
   *
   * @return the item dequeued
   */
  public E dequeue() {
    if (this.head == null) {
      return null;
    }
    E item = head.getItem();
    head = head.getNext();
    return item;
  }

  /**
   * Enqueues an item with a specific priority to the queue in its correct
   * position. Ensures that same priority is inserted to the end of the queue,
   * so that equivalent frequencies merge with the least depth prior to the less
   * depth nodes.
   *
   * @param item     the item to enqueue
   * @param priority the priority associated with the item
   */

  public void enqueue(E item, int priority) {
    PriorityQueueNode<E> newNode = new PriorityQueueNode<E>(item, priority);
    if (head == null) {
      head = newNode;
      tail = newNode;
      return;
    }

    if (priority < head.getPriority()) {
      newNode.setNext(head);
      head = newNode;
      return;
    }

    if (priority >= tail.getPriority()) {
      tail.setNext(newNode);
      tail = newNode;
      return;
    }

    PriorityQueueNode<E> currNode = head;
    while (currNode.getNext() != null && currNode.getNext().getPriority() <= priority) {
      currNode = currNode.getNext();
    }

    newNode.setNext(currNode.getNext());
    currNode.setNext(newNode);
  }

  /**
   * Returns the number of items in the priority queue
   *
   * return the number of items in the queue
   */
  public int size() {
    int count = 0;
    PriorityQueueNode<E> tempNode = this.head;
    while (tempNode != null) {
      tempNode = tempNode.getNext();
      count++;
    }

    return count;
  }

  /**
   * Inner class representing a node in a priority queue
   *
   * @param <T> the object type used as nodes
   * @author Sherwin Okhowat
   * @version 1.0
   */
  private class PriorityQueueNode<T> {
    private T item;
    private PriorityQueueNode<T> next;
    private int priority;

    /**
     * Constructor for a priority queue node
     *
     * @param item     the item associated with this node
     * @param priority the priority associated with this node
     */
    public PriorityQueueNode(T item, int priority) {
      this.item = item;
      this.priority = priority;
      this.next = null;
    }

    /**
     * Getter method for the item in this node
     *
     * @return the item in this node
     */
    public T getItem() {
      return this.item;
    }

    /**
     * Getter method for the next node
     *
     * @return the next node
     */
    public PriorityQueueNode<T> getNext() {
      return this.next;
    }

    /**
     * Getter method for the priority associated with this node
     *
     * @return the priority associated with this node
     */
    public int getPriority() {
      return this.priority;
    }

    /**
     * Sets the next node to the next node which is specified in the parameters
     *
     * @param next the next node
     */
    public void setNext(PriorityQueueNode next) {
      this.next = next;
    }
  }
}