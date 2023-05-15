import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * [HuffmanTree.java] Used to compress and decompress data using the Huffman
 * algorithm. It reads a file, and based on the frequencies of the bytes in the
 * file, creates an appropriate huffman tree and assigns huffman codes to each
 * byte. The encoded file is then saved and written into a new file.
 *
 * NOTE: 0 represents [LEFT] and 1 represents [RIGHT]
 *
 * @author Sherwin Okhowat
 * @version 1.0
 * @since 2023-05-10
 */
public class HuffmanTree {
  private HuffmanNode root;
  private String fileName;
  private String[] codes;
  private int[] byteFrequencies;

  // -------------------- User Methods --------------------

  /**
   * Initializes the huffman tree by reading the given file, constructing the
   * tree, and writing the necessary information to a new file.
   *
   * @param file the file the encode
   */
  public void initializeTree(String file) {
    try {
      this.fileName = file;
      this.codes = new String[256];
      this.byteFrequencies = new int[256];
      this.storeByteFrequencies();
      this.constructTree();
      this.writeEncodedFile();
      System.out.println("Initialized!");
    } catch (FileNotFoundException e) {
      System.out.println("File was not found!");
    }

  } // initializeTree method end

  // -------------------- Initializing Byte Frequencies --------------------

  /**
   * Reads the input file and stores the byte frequencies
   *
   * @throws FileNotFoundException for when a file is not found
   */
  private void storeByteFrequencies() throws FileNotFoundException {
    FileInputStream in = null;

    try {
      in = new FileInputStream(this.fileName);
      int b;

      // Increment frequency of bytes
      while ((b = in.read()) != -1) {
        byteFrequencies[b & 0xFF]++;
      }
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw (FileNotFoundException) e;
      }
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  } // storeByteFrequencies method end

  /**
   * Sets up a priority queue for all of the bytes from the input file, using
   * their frequencies as their priorities. Lower frequencies have greater
   * priority than higher frequencies
   *
   * @param byteFrequencies an array of all the bytes and their frequencies
   *                        (frequencies >= 0)
   * @return A PriorityQueue with huffman nodes based on the byte frequencies
   */
  private PriorityQueue<HuffmanNode> initializePriorityQueue(int[] byteFrequencies) {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

    for (int i = 0; i < byteFrequencies.length; i++) {
      int freq = byteFrequencies[i];
      if (freq > 0) {
        HuffmanNode newNode = new HuffmanNode(null, null, (byte) i, freq);
        pq.enqueue(newNode, freq);
      }
    }

    return pq;
  } // initializePriorityQueue method end

  // -------------------- Tree Construction --------------------

  /**
   * Constructs the huffman tree using the byte frequencies
   */
  private void constructTree() {
    PriorityQueue<HuffmanNode> pq = initializePriorityQueue(byteFrequencies);

    while (pq.size() > 1) {
      HuffmanNode left = pq.dequeue();
      HuffmanNode right = pq.dequeue();
      HuffmanNode newNode = new HuffmanNode(left, right, null, left.getFrequency() + right.getFrequency());
      pq.enqueue(newNode, newNode.getFrequency());
    }

    this.root = pq.dequeue();
    this.generateCodes(this.root, new StringBuilder(""));
  } // constructTree method end

  /**
   * Generates unique codes for each byte
   *
   * @param node the current node being looked at
   * @param code the string representation for the current node
   */
  private void generateCodes(HuffmanNode node, StringBuilder code) {
    if (node.getItem() != null) {
      // Convert byte to int representation
      codes[node.getItem() & 0xFF] = code.toString();
      return;
    }
    generateCodes(node.getLeft(), new StringBuilder(code).append("0"));
    generateCodes(node.getRight(), new StringBuilder(code).append("1"));
  } // generateCodes method end

  // -------------------- Encoding --------------------

  /**
   * Writes the encoded/compressed file to a new file of MZIP type.
   */
  private void writeEncodedFile() {
    FileInputStream in = null;
    FileOutputStream out = null;
    StringBuilder message = new StringBuilder();

    try {
      in = new FileInputStream(this.fileName);
      out = new FileOutputStream(this.fileName.substring(0, fileName.indexOf(".") + 1).toUpperCase() + "MZIP");

      // Write the name of the original file in all caps
      for (char c : fileName.toUpperCase().toCharArray()) {
        out.write((byte) c);
      }
      out.write((byte) '\r'); // 0D
      out.write((byte) '\n'); // 0A

      // Write the huffman tree in a string format
      for (char c : this.convertTreeToString().toCharArray()) {
        out.write((byte) c);
      }
      out.write((byte) '\r'); // 0D
      out.write((byte) '\n'); // 0A

      // Encode the file being read using the generated huffman codes
      int b;
      while ((b = in.read()) != -1) {
        for (char bit : codes[b & 0xFF].toCharArray()) {
          message.append(bit);
        }
      }

      // Write the extra bits used
      int extraBits = 8 - (message.length() % 8);
      for (char c : Integer.toString(extraBits).toCharArray()) {
        out.write((byte) c);
      }

      out.write((byte) '\r'); // 0D
      out.write((byte) '\n'); // 0A

      // Add the extra bits to the encoded message
      for (int i = 0; i < extraBits; i++) {
        message.append('0');
      }

      // Write the econded message using the bytes generated using huffman codes
      for (int i = 0; i < message.length(); i += 8) {
        int byteValue = Integer.parseInt(message.substring(i, i + 8), 2);
        out.write(byteValue);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  } // writeEncodedFile method end

  /**
   * Turns the built tree into a string representation
   * 
   * @return a string representation of the huffman tree
   */
  private String convertTreeToString() {
    return convertNodeToString(this.root);
  } // convertTreeToString method end

  /**
   * Recursive helper. Groups the huffman nodes and turns it into a string
   * representation
   * 
   * @param the current huffman node
   * @return the string representation of the huffman nodes
   */
  private String convertNodeToString(HuffmanNode currNode) {
    if (currNode == null) {
      return "";
    }

    if (currNode.getItem() != null) {
      return Integer.toString((int) currNode.getItem() & 0xFF);
    }

    String left = convertNodeToString(currNode.getLeft());
    String right = convertNodeToString(currNode.getRight());

    return "(" + left + " " + right + ")";
  } // convertTreeToString method end

  // -------------------- Private HuffmanNode Class --------------------

  /**
   * Private inner class representing a huffman node. Contains a left and right
   * node, a frequency which consists of the left node and right nodes sum of
   * frequencies, and an item which is the byte being stored.
   *
   * @author Sherwin Okhowat
   * @version 1.0
   * @since 2023-05-08
   */
  private class HuffmanNode {
    private int frequency;
    private Byte item;
    private HuffmanNode left;
    private HuffmanNode right;

    /**
     * Constructor for a huffman node
     * 
     * @param left      the left child node
     * @param right     the right child node
     * @param item      the byte being stored in this node
     * @param frequency the frequency associated with this node
     */
    public HuffmanNode(HuffmanNode left, HuffmanNode right, Byte item, int frequency) {
      this.left = left;
      this.right = right;
      this.item = item;
      this.frequency = frequency;
    }

    /**
     * Getter for the frequency of the node
     *
     * @return the frequency of this node
     */
    public int getFrequency() {
      return this.frequency;
    }

    /**
     * Getter for the left child node
     * 
     * @return the left child node
     */
    public HuffmanNode getLeft() {
      return this.left;
    }

    /**
     * Getter for the right child node
     * 
     * @return the right child node
     */
    public HuffmanNode getRight() {
      return this.right;
    }

    /**
     * Getter for the item/byte stored in this node
     * 
     * @return the byte stored in this node
     */
    public Byte getItem() {
      return this.item;
    }
  }
}