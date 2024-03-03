package filemonitor;

import java.io.File;

class DocumentChangeDetector extends FileChangeDetector {
    @Override
    public void onChange(File file, String action) {
        System.out.println(action + " detected in file: " + file.getAbsolutePath());
    }
}