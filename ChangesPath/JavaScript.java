public class RandomNumberGenerator {
    public static void main(String[] args) {
        int randomNumber = generateRandomNumber(1, 10);
        System.out.println("Random number between 1 and 10: " + randomNumber);
    }

    public static int generateRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
class