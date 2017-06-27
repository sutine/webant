package org.webant.worker.app;

import org.webant.worker.WorkerConsole;
import org.webant.worker.config.SiteConfigFileMonitor;
import org.webant.worker.config.TaskConfigFileMonitor;
import org.webant.worker.console.ConsoleOperation;

import java.net.URL;

/**
 * embed worker in an application, and load configuration with different ways
 */
public class StandaloneWorker {
    private static ConsoleOperation console = new ConsoleOperation();

    public static void main(String[] args) {
        console.start();

        URL taskUrl = ClassLoader.getSystemResource("task");
        URL siteUrl = ClassLoader.getSystemResource("site");
        String suffix = ".json";

        WorkerConsole.getWorkerConsole().loadAllSiteConfig(siteUrl.getPath(), suffix);
        WorkerConsole.getWorkerConsole().loadAllTaskConfig(taskUrl.getPath(), suffix);
        SiteConfigFileMonitor.getFileMonitor().monitor(siteUrl.getPath(), suffix, 500);
        TaskConfigFileMonitor.getFileMonitor().monitor(taskUrl.getPath(), suffix, 500);
    }
}
