import java.util.Scanner;

/**
 * [Main.java] Responsible for interacting with the user and creating a
 * HuffmanTree instance for the user's desired file. Continues to ask for a file
 * name and encodes it, until the user decides to quit. Works with files of any
 * type (e.g. txt, jpeg, png, mp3, etc).
 *
 * @author Sherwin Okhowat
 * @version 1.0
 * @since 2023-05-14
 */
class Main {
  public static void main(String[] args) {
    HuffmanTree tree = new HuffmanTree();
    Scanner input = new Scanner(System.in);
    String file = "";
    System.out.print("File name (type nothing to quit): ");
    file = input.nextLine();
    while (!file.trim().equals("")) {
      tree.initializeTree(file);

      System.out.println();
      System.out.print("File name (type nothing to quit): ");
      file = input.nextLine();
    }
    input.close();
  }
}
