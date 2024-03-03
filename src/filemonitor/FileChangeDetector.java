package filemonitor;

import java.io.*;

abstract class FileChangeDetector {
    public abstract void onChange(File file, String action);
}