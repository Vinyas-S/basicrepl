import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Shell {
    private static volatile boolean running = true;
    private static volatile boolean interrupted = false;
    private static Path currentDir = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) {
        handleSigInt();
        Scanner scanner = new Scanner(System.in);

        while (running) {
            try {
                System.out.print("$ ");
                String input;
                try {
                    input = scanner.nextLine().trim();
                } catch (NoSuchElementException e) {
                    if (interrupted) {
                        interrupted = false;
                        System.out.println();
                        continue;
                    }
                    break;
                }

                if (input.isEmpty()) {
                    continue;
                }

                String[] parts = input.split(" ", 2);
                String command = parts[0];
                String value = parts.length > 1 ? parts[1] : "";

                executeCommand(command, value);
            } catch (Exception e) {
                System.err.println("shell: error: " + e.getMessage());
            }
        }
    }

    private static void handleSigInt() {
        Signal.handle(new Signal("INT"), new SignalHandler() {
            @Override
            public void handle(Signal sig) {
                interrupted = true;
                System.out.print("\n(Type 'exit' to quit)\n");
            }
        });
    }

    private static void executeCommand(String command, String value) {
        try {
            switch (command) {
                case "pwd":
                    pwd();
                    break;
                case "ls":
                    ls(value);
                    break;
                case "cd":
                    cd(value);
                    break;
                case "echo":
                    echo(value);
                    break;
                case "type":
                    type(value);
                    break;
                case "exit":
                    exit();
                    break;
                default:
                    System.err.println("shell: unknown command: " + command);
            }
        } catch (Exception e) {
            System.err.println("shell: " + command + ": " + e.getMessage());
        }
    }

    private static void pwd() {
        System.out.println(currentDir);
    }

    private static void ls(String value) {
        Path dir = value.isEmpty() ? currentDir : currentDir.resolve(value).normalize();
        try {
            if (!Files.exists(dir)) {
                throw new IOException("Path does not exist: " + dir);
            }
            if (Files.isDirectory(dir)) {
                List<Path> entries;
                try (Stream<Path> stream = Files.list(dir)) {
                    entries = stream.collect(Collectors.toList());
                }

                System.out.println("total " + entries.size());

                try (Stream<Path> stream = Files.list(dir)) {
                    stream.forEach(d -> {
                        try {
                            String type = Files.isDirectory(d) ? "d" : "-";
                            System.out.printf("%s %-20s %6d bytes%n",
                                    type,
                                    d.getFileName(),
                                    Files.size(d));
                        } catch (IOException e) {
                            System.err.println("Error accessing file: " + d);
                        }
                    });
                }
            } else {
                System.out.println("(total 1)");
                System.out.println(dir.getFileName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void cd(String value) {
        if (value.isEmpty() || value.equals("~")) {
            value = System.getProperty("user.home");
        } else if (value.startsWith("~/")) {
            value = System.getProperty("user.home") + value.substring(1);
        }

        try {
            Path newPath = currentDir.resolve(value).normalize();
            if (!Files.exists(newPath)) {
                throw new IOException("Directory does not exist: " + newPath);
            }
            if (!Files.isDirectory(newPath)) {
                throw new IOException("Not a directory: " + newPath);
            }
            currentDir = newPath;
        } catch (InvalidPathException e) {
            throw new RuntimeException("Invalid path: " + value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void echo(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        System.out.println(text);
    }

    private static void type(String filename) {
        if (filename.isEmpty()) {
            throw new RuntimeException("Missing filename");
        }

        Path filePath = currentDir.resolve(filename).normalize();
        try {
            if (Files.notExists(filePath)) {
                throw new IOException("File does not exist");
            }

            String type;
            if (Files.isDirectory(filePath)) {
                type = "directory";
            } else {
                String contentType = Files.probeContentType(filePath);
                type = contentType != null ? contentType : "unknown file type";
            }

            System.out.println(filename + ": " + type);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void exit() {
        System.out.println("Goodbye!");
        running = false;
    }
}
