import java.util.Scanner;

public class ThreadTest {
  public static void main(String[] args) throws InterruptedException {
    Runnable runnableTest = () -> {
      for(int i = 0; i < 100; i++) {
        try {
          Thread.sleep(1000);
        } catch (Exception ignored) {

        }
        System.err.println(i);
      }
    };
    Thread threadTest = new Thread(runnableTest);
    threadTest.start();

    Scanner input = new Scanner(System.in);
    System.out.print("Your input: ");
    String userInput = input.nextLine();
    while(!userInput.equals("exit")) {
      System.out.print("Your input: ");
      userInput = input.nextLine();
    }
    System.exit(0);
  }
}
