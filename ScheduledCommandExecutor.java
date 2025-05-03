import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class ScheduledCommandExecutor {

    private static final String COMMAND_FILE = "/tmp/commands.txt";
    private static final List<Integer> VALID_INTERVALS = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60);

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        try {
            String currentDir = System.getProperty("user.dir");
            List<String> lines = Files.readAllLines(Paths.get(currentDir+COMMAND_FILE));
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("*/")) {
                    scheduleRecurringCommand(line);
                } else {
                    scheduleOneTimeCommand(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading command file: " + e.getMessage());
        }
    }

    private static void scheduleOneTimeCommand(String line) {
        try {
            String[] parts = line.split(" ", 6);
            if (parts.length < 6) {
                System.err.println("Invalid one-time command format: " + line);
                return;
            }

            int minute = Integer.parseInt(parts[0]);
            int hour = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            int month = Integer.parseInt(parts[3]);
            int year = Integer.parseInt(parts[4]);
            String command = parts[5];
            LocalDateTime targetTime = LocalDateTime.of(year, month, day, hour, minute);
            long delay = Duration.between(LocalDateTime.now(), targetTime).toMillis();

            if (delay > 0) {
                scheduler.schedule(() -> executeCommand(command, "One-time"), delay, TimeUnit.MILLISECONDS);
                System.out.println("Scheduled one-time command at " + targetTime + ": " + command);
            } else {
                System.out.println("Skipped past one-time command: " + command);
            }

        } catch (Exception e) {
            System.err.println("Error scheduling one-time command: " + e.getMessage());
        }
    }

    private static void scheduleRecurringCommand(String line) {
        try {
            String[] parts = line.split(" ", 2);
            int interval = Integer.parseInt(parts[0].replace("*/", ""));
            String command = parts[1];

            if (!VALID_INTERVALS.contains(interval)) {
                System.err.println("Invalid interval: " + interval);
                return;
            }

            scheduler.scheduleAtFixedRate(
                    () -> executeCommand(command, "Recurring (every " + interval + " min)"),
                    0, interval, TimeUnit.MINUTES
            );
            System.out.println("Scheduled recurring command every " + interval + " minutes: " + command);

        } catch (Exception e) {
            System.err.println("Error scheduling recurring command: " + e.getMessage());
        }
    }

    private static void executeCommand(String command, String type) {
        try {
            System.out.println("[" + type + "] Executing: " + command);

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            reader.lines().forEach(System.out::println);

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            errorReader.lines().forEach(System.err::println);

            List<String> outputLines = new ArrayList<>();
            reader.lines().forEach(outputLines::add);
            errorReader.lines().forEach(line -> outputLines.add("[ERR] " + line));

            process.waitFor();

            // Write to output.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("sample-output.txt", true))) {
                writer.write("==== " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ====\n");
                writer.write("[" + type + "] Command: " + command + "\n");
                for (String line : outputLines) {
                    writer.write(line + "\n");
                }
                writer.write("\n");
            }

            // Also print to console
            outputLines.forEach(System.out::println);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }
}
