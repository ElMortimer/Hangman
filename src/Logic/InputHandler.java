package Logic;//Get user input for name,guess,difficulty
import java.util.InputMismatchException;
import java.util.Scanner;

 class InputHandler {
     private final Scanner scanner = new Scanner(System.in);

     public String getPlayerName() {
         System.out.print("Enter your name : ");
         return scanner.nextLine();
     }

     public char getGuess() {
         System.out.println("Guess a letter: ");
         return scanner.next().charAt(0);
     }

     public boolean askPlayerAgain() {
         System.out.print("Do you want to play again? ");

         String answer = scanner.nextLine().trim();

         while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")) {
             System.out.print("Please enter Y or N: ");
             answer = scanner.nextLine().trim();
         }

         return answer.equalsIgnoreCase("Y");
     }

     public String getDifficultyString() {
         System.out.println("Difficulty:");
         System.out.println("1. Easy\n2. Normal\n3. Hard");

         int choice = -1;
         while (true) {
             System.out.print("Enter your choice (1-3): ");
             try {
                 choice = scanner.nextInt();
                 scanner.nextLine();

                 if (choice >= 1 && choice <= 3) break;
                 else System.out.println("Invalid choice. Please enter 1, 2, or 3.");

             } catch (InputMismatchException e) {
                 System.out.println("Invalid input. Please enter a number.");
                 scanner.nextLine();
             }
         }

         return switch (choice) {
             case 1 -> "easy";
             case 2 -> "medium";
             case 3 -> "hard";
             default -> "medium";
         };
     }
 }
