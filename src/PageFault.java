//Student: Vinh Le
//Class: COEN283
import java.io.*;
import java.util.Scanner;

public class PageFault {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Frame Size");
        int frameNumber = sc.nextInt();
        sc.close();

        String input_file = "file.txt";
        GenerateInput(input_file, frameNumber);

        int[] frames = new int[frameNumber];
        int[] counter = new int[frameNumber];
        init(frames, counter);
        int pageFault = findPageFault(frames, counter, input_file);
        System.out.println(pageFault);
        WriteOutput(pageFault, "Output.txt");
    }

    private static void init(int[] frames, int[] counter) {
        for (int i = 0; i < frames.length; i++) {
            frames[i] = -1;
            counter[i] = 0;
        }
    }

    // Shift counter right 1 bit every clock tick
    private static void shiftCounter1Bit(int[] counter) {
        for (int k = 0; k < counter.length; k++) {
            counter[k] = counter[k] >> 1;
        }
    }

    private static int findPageFault(int[] frames, int[] counter, String fileName) throws IOException {
        int pageFault = 0;
        boolean isAllocated = false;
        boolean emptyFrame = false;

        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        int i = 0;
        while (line != null) {
            shiftCounter1Bit(counter);
            int pageValue = Integer.parseInt(line);

            for (int j = 0; j < frames.length; j++) {
                // Existing allocated this pageValue
                if (frames[j] == pageValue) {
                    // Set counter the most significant bit
                    // 128 = 10_00_00_00 bit
                    counter[j] = counter[j] | 128;
                    isAllocated = true;
                    break;
                }
            }
            if (!isAllocated) {
                pageFault++;
                for (int k = 0; k < frames.length; k++) {
                    if (frames[k] == -1) {
                        // Allocate memory for this page value
                        frames[k] = pageValue;
                        // Set counter the most significant bit
                        counter[k] = counter[k] | 128;
                        emptyFrame = true;
                        break;
                    }
                }
                if (!emptyFrame) {
                    int lowestCounterIndex = findLowestCounter(counter);
                    frames[lowestCounterIndex] = pageValue;
                    // Start set counter 128 = 10_00_00_00
                    counter[lowestCounterIndex] = 128;
                }
            }
            isAllocated = false;
            emptyFrame = false;
            line = br.readLine();
            i++;
        }
        br.close();
        return pageFault;
    }

    private static int findLowestCounter(int[] counter) {
        int minCounter = (int) Math.pow(2, 8);
        int lowestCounterIndex = 0;
        // Find the lowest counter value
        for (int k = 0; k < counter.length; k++) {
            if (counter[k] < minCounter) {
                minCounter = counter[k];
                lowestCounterIndex = k;
            }
        }
        return lowestCounterIndex;
    }

    private static void GenerateInput(String fileName, int frameNumber) throws IOException {
        File file = new File(fileName);
        if (file.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists");
        }
        PrintWriter writer = new PrintWriter(fileName);
        for (int i = 0; i < 1000; i++) {
            int k = (int) (Math.floor(Math.random() * (frameNumber + 5)));
            writer.println(k + 1);
        }
        writer.close();
    }

    private static void WriteOutput(int pageFault, String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        PrintWriter writer = new PrintWriter(fileName);
        writer.println("Page Fault = " + pageFault);
        writer.close();
    }
}
