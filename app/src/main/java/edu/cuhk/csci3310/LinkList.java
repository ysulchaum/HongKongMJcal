package edu.cuhk.csci3310;

public class LinkList {
    private Node head;
    private Node tail;

    private static class Node {
        RoundRecord data;
        Node next;

        public Node(RoundRecord data) {
            this.data = data;
            this.next = null;
        }
    }

    // Add to end (O(1))
    public void add(RoundRecord record) {
        Node newNode = new Node(record);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    // Remove from end (O(n))
    public RoundRecord pop() {
        if (head == null) {
            throw new IllegalStateException("Cannot pop from empty list");
        }

        RoundRecord poppedRecord = tail.data;

        if (head == tail) {
            head = null;
            tail = null;
        } else {
            Node current = head;
            while (current.next != tail) {
                current = current.next;
            }
            current.next = null;
            tail = current;
        }

        return poppedRecord;
    }

    @Override
    public String toString() {
        if (head == null) {
            return "Empty list";
        }

        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.data);  // Requires RoundRecord.toString()
            if (current.next != null) {
                sb.append("\n-> ");  // Newline separator for records
            }
            current = current.next;
        }
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        LinkList records = new LinkList();

        // Add records (requires constructor in RoundRecord)
        records.add(new RoundRecord(3, 10, 1, "Alice", "Bob"));
        records.add(new RoundRecord(5, 8, 2, "Carol", "Dave"));

        System.out.println("Current records:");
        System.out.println(records);

        // Pop records
        System.out.println("\nPopped: " + records.pop());
        System.out.println("Popped: " + records.pop());
    }
}