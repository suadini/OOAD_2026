package OO.Assignment_2B;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface for manually exercising a {@link CircularBuffer}.
 *
 * <p>Delegates all buffer logic to {@link CircularBuffer} and {@link Subscriber}.
 * This class is responsible only for input parsing and output formatting.
 */
public class BufferTerminal {

    private static CircularBuffer<String> buffer;
    private static final List<Subscriber<String>> subscribers = new ArrayList<>();
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        printHelp();
        runLoop();
    }

    // -------------------------------------------------------------------------
    // Command dispatch
    // -------------------------------------------------------------------------

    private static void runLoop() {
        while (true) {
            System.out.print(">> ");
            String line = input.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+", 2);
            String cmd  = tokens[0].toLowerCase();
            String arg  = tokens.length > 1 ? tokens[1].trim() : "";

            switch (cmd) {
                case "init":       cmdInit(arg);        break;
                case "write":      cmdWrite(arg);       break;
                case "subscribe":  cmdSubscribe();      break;
                case "read":       cmdRead(arg);        break;
                case "info":       cmdInfo();           break;
                case "help":       printHelp();         break;
                case "exit":
                case "quit":
                    System.out.println("Exiting.");
                    return;
                default:
                    System.out.println("Unrecognised command. Type 'help' for a list.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Command implementations
    // -------------------------------------------------------------------------

    /** Initialises a new buffer, discarding any existing buffer and subscribers. */
    private static void cmdInit(String arg) {
        try {
            int cap = Integer.parseInt(arg);
            if (cap < 1) { System.out.println("Capacity must be >= 1."); return; }
            buffer = new CircularBuffer<>(cap);
            subscribers.clear();
            System.out.println("Initialised buffer  capacity=" + cap);
        } catch (NumberFormatException e) {
            System.out.println("Usage: init <capacity>");
        }
    }

    /** Writes a single string item to the buffer. */
    private static void cmdWrite(String item) {
        if (!guardBuffer()) return;
        if (item.isEmpty()) { System.out.println("Usage: write <item>"); return; }

        long seq   = buffer.getWriteHead();
        int  index = (int) (seq % buffer.getCapacity());
        buffer.write(item);
        System.out.printf("Written    seq=%-4d  index=%-3d  value=\"%s\"%n", seq, index, item);
    }

    /** Creates a new subscriber starting at the current write head. */
    private static void cmdSubscribe() {
        if (!guardBuffer()) return;
        subscribers.add(buffer.subscribe());
        System.out.println("Subscriber #" + subscribers.size()
                + " registered  start-position=" + buffer.getWriteHead());
    }

    /** Advances subscriber N by one position and prints the item read. */
    private static void cmdRead(String arg) {
        if (!guardBuffer()) return;
        try {
            int idx = Integer.parseInt(arg) - 1;
            if (idx < 0 || idx >= subscribers.size()) {
                System.out.println("No subscriber #" + (idx + 1)
                        + ". Valid range: 1–" + subscribers.size());
                return;
            }
            Subscriber<String> sub  = subscribers.get(idx);
            long               pos  = sub.getReadHead();
            String             item = sub.read();

            if (item == null) {
                System.out.println("Subscriber #" + (idx + 1)
                        + "  position=" + pos + "  (nothing written here yet)");
            } else {
                System.out.printf("Subscriber #%-3d  position=%-4d  value=\"%s\"%n",
                        idx + 1, pos, item);
            }
        } catch (NumberFormatException e) {
            System.out.println("Usage: read <subscriberNumber>");
        }
    }

    /** Prints current buffer state and all subscriber positions. */
    private static void cmdInfo() {
        if (buffer == null) { System.out.println("No buffer. Use: init <capacity>"); return; }
        System.out.println("--- Buffer Info ---");
        System.out.println("  Capacity   : " + buffer.getCapacity());
        System.out.println("  Write head : " + buffer.getWriteHead());
        System.out.println("  Subscribers: " + subscribers.size());
        for (int i = 0; i < subscribers.size(); i++) {
            System.out.printf("    #%-3d  read-head=%d%n", i + 1, subscribers.get(i).getReadHead());
        }
        System.out.println("-------------------");
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------

    private static boolean guardBuffer() {
        if (buffer == null) {
            System.out.println("No buffer initialised. Use: init <capacity>");
            return false;
        }
        return true;
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Circular Buffer Terminal");
        System.out.println("  init <N>        Create buffer with capacity N (resets all subscribers)");
        System.out.println("  write <item>    Write item to buffer");
        System.out.println("  subscribe       Register a new subscriber");
        System.out.println("  read <N>        Subscriber #N reads its next item");
        System.out.println("  info            Show buffer and subscriber state");
        System.out.println("  help            Show this message");
        System.out.println("  exit            Quit");
        System.out.println();
    }
}
