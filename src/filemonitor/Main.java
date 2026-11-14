package filemonitor;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        File folder = new File(args.length > 0 ? args[0] : "ChangesPath");
        if (!folder.isDirectory() || !folder.canRead()) {
            System.err.println("Provide a readable directory: java -cp out filemonitor.Main [folder]");
            System.exit(1);
        }
        System.out.println("Monitoring: " + folder.getAbsolutePath());
        FileChangeDetector detector = new DocumentChangeDetector();
        DocumentFolderMonitor monitor = new DocumentFolderMonitor(folder, detector);
        try {
            monitor.startMonitoring();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
