package filemonitor;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.imageio.ImageIO;

class DocumentFolderMonitor {
    private final File folder;
    private final FileChangeDetector detector;
    private final ScheduledExecutorService scheduler;
    private Map<String, Long> statusSnapshot;
    private Map<String, Long> previousPollSnapshot;

    public DocumentFolderMonitor(File folder, FileChangeDetector detector) {
        this.folder = folder;
        this.detector = detector;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startMonitoring() throws IOException {
        statusSnapshot = readSnapshot();
        previousPollSnapshot = statusSnapshot;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                detectChanges();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);

        try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
			    System.out.print("> ");
			    if (!scanner.hasNextLine()) break;
			    String command = scanner.nextLine().trim();

			    if (command.equalsIgnoreCase("commit")) {
			        updateStatusSnapshot();
			    } else if (command.equalsIgnoreCase("info")) {
			        System.out.print("Enter file name: ");
			        if (!scanner.hasNextLine()) break;
			        String fileName = scanner.nextLine().trim();
			        printFileInfo(fileName);
			    } else if (command.equalsIgnoreCase("status")) {
			        checkFileStatus();
			    } else if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
			        break;
			    } else {
			        System.out.println("Invalid command. Please enter 'commit', 'info', 'status', or 'exit'.");
			    }
			}
		} finally {
            scheduler.shutdownNow();
        }
    }

    private Map<String, Long> readSnapshot() throws IOException {
        File[] files = folder.listFiles();
        if (files == null) {
            throw new IOException("Cannot read monitored directory: " + folder);
        }
        Map<String, Long> snapshot = new HashMap<>();
        for (File file : files) {
            snapshot.put(file.getName(), file.lastModified());
        }
        return snapshot;
    }

    private synchronized void updateStatusSnapshot() {
        try {
            statusSnapshot = readSnapshot();
            System.out.println("Snapshot updated.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private synchronized void detectChanges() throws IOException {
        Map<String, Long> currentSnapshot = readSnapshot();
        for (Map.Entry<String, Long> entry : previousPollSnapshot.entrySet()) {
            String fileName = entry.getKey();
            Long lastModified = currentSnapshot.get(fileName);
            if (lastModified == null) {
                detector.onChange(new File(folder, fileName), "Deleted");
            } else if (!lastModified.equals(entry.getValue())) {
                detector.onChange(new File(folder, fileName), "Changed");
            }
        }

        for (String fileName : currentSnapshot.keySet()) {
            if (!previousPollSnapshot.containsKey(fileName)) {
                detector.onChange(new File(folder, fileName), "New File");
            }
        }

        previousPollSnapshot = currentSnapshot;
    }

	private void printFileInfo(String fileName) {
        if (fileName.isEmpty()) {
            System.out.println("Please specify a file name.");
            return;
        }

        File fileToPrint = new File(folder, fileName);
        if (!fileToPrint.exists()) {
            System.out.println("File not found.");
            return;
        }

        System.out.println("File: " + fileName);
        System.out.println("Extension: " + getFileExtension(fileToPrint));
        System.out.println("Created: " + new Date(fileToPrint.lastModified()));
        System.out.println("Updated: " + new Date(fileToPrint.lastModified()));

        if (fileToPrint.getName().toLowerCase().endsWith(".txt")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToPrint))) {
                int lineCount = 0;
                int wordCount = 0;
                int charCount = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    charCount += line.length();
                    if (lineCount > 1) {
                        charCount++;
                    }
                    lineCount++;
                    String[] words = line.split("\\s+");
                    wordCount += words.length;
                }
                System.out.println("Line count: " + lineCount);
                System.out.println("Word count: " + wordCount);
                System.out.println("Character count: " + charCount);
            } catch (IOException e) {
                System.out.println("Error reading text file.");
            }
        } else if (fileToPrint.getName().toLowerCase().endsWith(".png") || fileToPrint.getName().toLowerCase().endsWith(".jpg")) {
            try {
                BufferedImage img = ImageIO.read(fileToPrint);
                System.out.println("Image Size: " + img.getWidth() + "x" + img.getHeight());
            } catch (IOException e) {
                System.out.println("Error reading image size.");
            }
        } else if (fileToPrint.getName().toLowerCase().endsWith(".java")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToPrint))) {
                int lineCount = 0;
                int classCount = 0;
                int methodCount = 0;
                boolean inComment = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    if (line.trim().startsWith("/*")) {
                        inComment = true;
                    }
                    if (inComment) {
                        if (line.trim().endsWith("*/")) {
                            inComment = false;
                        }
                        continue;
                    }
                    if (line.trim().startsWith("//")) {
                        continue;
                    }
                    if (line.contains("class ")) {
                        classCount++;
                    }
                    if (line.trim().startsWith("public") || line.trim().startsWith("private")
                            || line.trim().startsWith("protected") || line.trim().startsWith("static")
                            || line.trim().startsWith("abstract") || line.trim().startsWith("final")) {
                        if (line.trim().endsWith("{") && !line.trim().endsWith("}") && !line.contains("class ")) {
                            methodCount++;
                        }
                    }
                }
                System.out.println("Line count: " + lineCount);
                System.out.println("Class count: " + classCount);
                System.out.println("Method count: " + methodCount);
            } catch (IOException e) {
                System.out.println("Error reading Java file.");
            }
        } else if (fileToPrint.getName().toLowerCase().endsWith(".py")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToPrint))) {
                int lineCount = 0;
                int classCount = 0;
                int methodCount = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    if (line.contains("\"\"\"") || line.contains("'''")) {
                        while ((line = reader.readLine()) != null) {
                            lineCount++;
                            if (line.contains("\"\"\"") || line.contains("'''")) {
                                break;
                            }
                        }
                        continue;
                    }
                    if (line.trim().startsWith("#")) {
                        continue;
                    }
                    if (line.contains("class ")) {
                        classCount++;
                    }
                    if (line.trim().startsWith("def ")) {
                        methodCount++;
                    }
                }
                System.out.println("Line count: " + lineCount);
                System.out.println("Class count: " + classCount);
                System.out.println("Method count: " + methodCount);
            } catch (IOException e) {
                System.out.println("Error reading Python file.");
            }
        }
    }

    private synchronized void checkFileStatus() {
        Map<String, Long> currentSnapshot;
        try {
            currentSnapshot = readSnapshot();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Map.Entry<String, Long> entry : statusSnapshot.entrySet()) {
            String fileName = entry.getKey();
            Long lastModified = currentSnapshot.get(fileName);
            if (lastModified == null) {
                System.out.println(fileName + " Deleted");
            } else if (!lastModified.equals(entry.getValue())) {
                System.out.println(fileName + " Changed");
            } else {
                System.out.println(fileName + " No Change");
            }
        }

        for (String fileName : currentSnapshot.keySet()) {
            if (!statusSnapshot.containsKey(fileName)) {
                System.out.println(fileName + " New File");
            }
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}
