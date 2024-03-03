package filemonitor;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        File folder = new File("C:\\Users\\Sergiu\\eclipse-workspace\\FileMonitor\\ChangesPath");
        FileChangeDetector detector = new DocumentChangeDetector();
        DocumentFolderMonitor monitor = new DocumentFolderMonitor(folder, detector);
        try {
            monitor.startMonitoring();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}