package org.webant.commons.test.file.monitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class FileMonitor extends FileAlterationListenerAdaptor {
    private static final Logger LOG = Logger.getLogger(FileMonitor.class);

    private static FileMonitor fileMonitor;

    private FileMonitor() {
    }

    // Get singleton object instance
    static FileMonitor getFileMonitor() {
        if (fileMonitor == null) {
            synchronized (FileMonitor.class) {
                if (fileMonitor == null) {
                    fileMonitor = new FileMonitor();
                }
            }
        }

        return fileMonitor;
    }

    // Create file event
    @Override
    public void onFileCreate(File file) {
        LOG.info("[Create]: " + file.getAbsolutePath());

        String fileAbsolutePath = file.getAbsolutePath();
        String fileAbsoluteParentPath = file.getParent();
        String fileBaseName = FilenameUtils.getBaseName(fileAbsolutePath);

        File destFile = new File(fileAbsoluteParentPath + File.separator + fileBaseName + ".xml");

        try {
            FileUtils.moveFile(file, destFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Change file event
    @Override
    public void onFileChange(File file) {
        LOG.info("[Change]: " + file.getAbsolutePath());
    }

    // Delete file event
    @Override
    public void onFileDelete(File file) {
        LOG.info("[Delete]: " + file.getAbsolutePath());
    }

    void monitor(String directory, int interval) {
        // Observer file whose suffix is pm
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(directory, FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".json")), null);

        // Add listener for event (file create & change & delete)
        fileAlterationObserver.addListener(this);

        // Monitor per interval
        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(interval, fileAlterationObserver);

        try {
            // Start to monitor
            fileAlterationMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
