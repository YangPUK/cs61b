package deque;

public class LinkedListDeque<Item> {
    private class IntNode{
        public Item item;
        public IntNode next;

        public IntNode(Item i, IntNode n){
            item = i;
            next = n;
        }

        public pNode(IntNode m , IntNode n){
            item = m;
            next = n;
        }
    }

    /* The first item is at sentinel.next*/
    private IntNode sentinelHead;
    private pNode sentinelTail;
    private int size;

    /* Creates new list*/
    public LinkedListDeque(){
        sentinelHead = new IntNode(null, null);
        sentinelTail = new IntNode(null, null);
        size = 0;
    }

    public LinkedListDeque(Item x){
        sentinelHead = new IntNode(null, null);
        sentinelHead.next = new IntNode(x, null);
        sentinelTail = new pNode(sentinelHead.next , null)
        size = 1;
    }

    public void addFirst(Item x){
        sentinelHead.next = new IntNode(x, sentinelHead.next);
        size++;
    }

    public void addLast(Item x){
        IntNode tmp = new IntNode(x, null);
        sentinelTail.next = new IntNode(sentinelTail.next.item, tmp);
        sentinelTail.next = tmp;
    }

    public boolean isEmpty(){
        if (size == 0)return true;
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        IntNode p = sentinelHead.next;
        for (int i = 0; i < size; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    public Item removeFirst(){
        if (size == 0)return null;
        Item result = sentinelHead.next.item;
        sentinelHead.next = sentinelHead.next.next;
        size--;
        return result;
    }

    public Item removeLast(){
        if (size == 0)return null;
        Item result = sentinelTail.next.item;

    }
}
