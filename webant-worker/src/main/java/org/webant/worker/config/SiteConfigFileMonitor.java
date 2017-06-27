package org.webant.worker.config;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.webant.worker.WorkerConsole;

import java.io.File;

public class SiteConfigFileMonitor extends FileAlterationListenerAdaptor {
    private static final Logger logger = Logger.getLogger(SiteConfigFileMonitor.class);

    private static SiteConfigFileMonitor fileMonitor;

    private SiteConfigFileMonitor() {
    }

    public static SiteConfigFileMonitor getFileMonitor() {
        if (fileMonitor == null) {
            synchronized (SiteConfigFileMonitor.class) {
                if (fileMonitor == null) {
                    fileMonitor = new SiteConfigFileMonitor();
                }
            }
        }

        return fileMonitor;
    }

    @Override
    public void onFileCreate(File file) {
        logger.info("config created. path: " + file.getAbsolutePath());
        WorkerConsole.getWorkerConsole().loadSiteConfig(file);
    }

    @Override
    public void onFileChange(File file) {
        logger.info("config changed. path: " + file.getAbsolutePath());
        WorkerConsole.getWorkerConsole().loadSiteConfig(file);
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
