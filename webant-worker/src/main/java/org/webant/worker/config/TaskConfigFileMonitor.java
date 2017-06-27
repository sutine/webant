package org.webant.worker.config;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.webant.worker.WorkerConsole;

import java.io.File;

public class TaskConfigFileMonitor extends FileAlterationListenerAdaptor {
    private static final Logger logger = Logger.getLogger(TaskConfigFileMonitor.class);

    private static TaskConfigFileMonitor fileMonitor;

    private TaskConfigFileMonitor() {
    }

    public static TaskConfigFileMonitor getFileMonitor() {
        if (fileMonitor == null) {
            synchronized (TaskConfigFileMonitor.class) {
                if (fileMonitor == null) {
                    fileMonitor = new TaskConfigFileMonitor();
                }
            }
        }

        return fileMonitor;
    }

    @Override
    public void onFileCreate(File file) {
        logger.info("config created. path: " + file.getAbsolutePath());
        WorkerConsole.getWorkerConsole().loadTaskConfig(file);
    }

    @Override
    public void onFileChange(File file) {
        logger.info("config changed. path: " + file.getAbsolutePath());
        WorkerConsole.getWorkerConsole().loadTaskConfig(file);
    }

    @Override
    public void onFileDelete(File file) {
        logger.info("config deleted. path: " + file.getAbsolutePath());
    }

    public void monitor(String directory, String suffix, int interval) {
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(directory, FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(suffix)), null);

        fileAlterationObserver.addListener(this);

        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(interval, fileAlterationObserver);

        try {
            fileAlterationMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
