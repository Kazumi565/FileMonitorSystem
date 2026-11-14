package filemonitor;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

class DocumentFolderMonitor {
    private File folder;
    private FileChangeDetector detector;
    private Map<String, Long> fileSnapshots;
    private Set<String> filesInSnapshot;
    private ScheduledExecutorService scheduler;

    public DocumentFolderMonitor(File folder, FileChangeDetector detector) {
        this.folder = folder;
        this.detector = detector;
        this.fileSnapshots = new HashMap<>();
        this.filesInSnapshot = new HashSet<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startMonitoring() throws IOException {
        updateSnapshot();

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
			        updateSnapshot();
			        System.out.println("Snapshot updated.");
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

    private synchronized void updateSnapshot() {
        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("Cannot read monitored directory: " + folder);
            return;
        }
        fileSnapshots.clear();
        filesInSnapshot.clear();
        for (File file : files) {
            fileSnapshots.put(file.getName(), file.lastModified());
            filesInSnapshot.add(file.getName());
        }
    }

    private synchronized void detectChanges() throws IOException {
        File[] currentFiles = folder.listFiles();
        if (currentFiles == null) {
            throw new IOException("Cannot read monitored directory: " + folder);
        }
        for (String fileName : filesInSnapshot) {
            File file = new File(folder, fileName);
            long snapshotTime = fileSnapshots.getOrDefault(fileName, 0L);
            if (file.exists() && file.lastModified() != snapshotTime) {
                detector.onChange(file, "Changed");
            } else if (!file.exists()) {
                detector.onChange(file, "Deleted");
            }
        }

        for (File file : currentFiles) {
            if (!filesInSnapshot.contains(file.getName())) {
                detector.onChange(file, "New File");
            }
        }

        updateSnapshot();
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
        File[] currentFiles = folder.listFiles();
        if (currentFiles == null) {
            System.out.println("Cannot read monitored directory: " + folder);
            return;
        }
        Set<String> currentFileNames = Arrays.stream(currentFiles).map(File::getName).collect(Collectors.toSet());

        for (Map.Entry<String, Long> entry : fileSnapshots.entrySet()) {
            String fileName = entry.getKey();
            long snapshotTime = entry.getValue();
            File currentFile = new File(folder, fileName);

            if (currentFile.exists()) {
                long lastModified = currentFile.lastModified();
                if (lastModified != snapshotTime) {
                    System.out.println(fileName + " Changed");
                } else {
                    System.out.println(fileName + " No Change");
                }
                currentFileNames.remove(fileName);
            } else {
                System.out.println(fileName + " Deleted");
            }
        }

        for (String newFileName : currentFileNames) {
            System.out.println(newFileName + " New File");
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
