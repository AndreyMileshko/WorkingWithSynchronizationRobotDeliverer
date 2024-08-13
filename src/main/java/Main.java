import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    static final Integer firstOccurrence = 1;
    static final int routeQuantity = 1000;
    static final String originalString = "RLRFR";
    static final int stringLength = 100;


    public static void main(String[] args) throws InterruptedException {
        Thread printingThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int maxValue = sizeToFreq.entrySet()
                            .stream()
                            .max(Map.Entry.comparingByValue())
                            .get()
                            .getValue();
                    int maxValueKey = sizeToFreq.entrySet()
                            .stream()
                            .max(Map.Entry.comparingByValue())
                            .get()
                            .getKey();
                    System.out.printf("Текущий лидер среди частот %d (встретилось %d раз(а))\n",
                            maxValueKey, maxValue);
                }
            }
        });
        printingThread.start();

        for (int i = 0; i < routeQuantity; i++) {
            Thread thread = new Thread(() -> {
                String str = generateRoute(originalString, stringLength);
                int resultOfTask = (int) str.chars().filter(ch -> ch == 'R').count();
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(resultOfTask)) {
                        int value = sizeToFreq.get(resultOfTask) + 1;
                        sizeToFreq.put(resultOfTask, value);
                    } else {
                        sizeToFreq.put(resultOfTask, firstOccurrence);
                    }
                    sizeToFreq.notify();
                }
            });
            thread.start();
            thread.join();
        }
        printingThread.interrupt();

        System.out.println("Другие размеры: ");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            System.out.printf("- %d (%d раз(a))\n", key, value);
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}