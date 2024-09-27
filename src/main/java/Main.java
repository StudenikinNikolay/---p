//_author_ = "Studenikin_Nikolay"
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final int CAPACITY = 100;
    private static final int TEXT_LENGTH = 100_000;
    private static final String LETTERS = "abc";
    private static final int NUM = 10_000;

    private static String maxA = "";
    private static String maxB = "";
    private static String maxC = "";

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> countA = new ArrayBlockingQueue<>(CAPACITY);
        BlockingQueue<String> countB = new ArrayBlockingQueue<>(CAPACITY);
        BlockingQueue<String> countC = new ArrayBlockingQueue<>(CAPACITY);

        CountDownLatch latch = new CountDownLatch(NUM);

        Runnable producer = () -> {
            for (int i = 0; i < NUM; i++) {
                String text = generateText(LETTERS, TEXT_LENGTH);

                try {
                    int countIntA = count(text, 'a');
                    int countIntB = count(text, 'b');
                    int countIntC = count(text, 'c');
                    int max = Math.max(Math.max(countIntA, countIntB), countIntC);
                    if (countIntA == max) {
                        countA.put(text);
                    } else if (countIntB == max) {
                        countB.put(text);
                    } else {
                        countC.put(text);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable consumerA = () -> {
            try {
                while (latch.getCount() > 0 || !countA.isEmpty()) {
                    String text = countA.poll();
                    if (text != null) {
                        if (count(text, 'a') > count(maxA, 'a')) {
                            maxA = text;
                        }
                        latch.countDown();
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumerB = () -> {
            try {
                while (latch.getCount() > 0 || !countB.isEmpty()) {
                    String text = countB.poll();
                    if (text != null) {
                        if (count(text, 'b') > count(maxB, 'b')) {
                            maxB = text;
                        }
                        latch.countDown();
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumerC = () -> {
            try {
                while (latch.getCount() > 0 || !countC.isEmpty()) {
                    String text = countC.poll();
                    if (text != null) {
                        if (count(text, 'c') > count(maxC, 'c')) {
                            maxC = text;
                        }
                        latch.countDown();
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread producerThread = new Thread(producer);
        Thread threadA = new Thread(consumerA);
        Thread threadB = new Thread(consumerB);
        Thread threadC = new Thread(consumerC);

        producerThread.start();
        threadA.start();
        threadB.start();
        threadC.start();

        producerThread.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("Так выглядит строка с максимальным количеством символов a:");
        System.out.println(maxA);

        System.out.println("Так выглядит строка с максимальным количеством символов b:");
        System.out.println(maxB);

        System.out.println("Так выглядит строка с максимальным количеством символов c:");
        System.out.println(maxC);
    }

    public static int count(String text, char symbol) {
        return (int) text.chars()
                .filter(c -> c == symbol)
                .count();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}