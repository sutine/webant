package org.webant.worker.app;

import org.webant.worker.WorkerConsole;
import org.webant.worker.config.ConfigManager;
import org.webant.worker.config.SiteConfigFileMonitor;
import org.webant.worker.config.TaskConfigFileMonitor;
import org.webant.worker.console.WorkerJmxServer;

import java.net.URL;

/**
 * run worker as a server on single node, and control it by a worker client
 */
public class WorkerServer {
    public static void main(String[] args) {
        try {
            ConfigManager.loadWorkerConfig("worker.json");

            WorkerJmxServer.start();

            URL taskUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().taskMonitor().dir());
            URL siteUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().siteMonitor().dir());
            String suffix = ".json";

            WorkerConsole.getWorkerConsole().loadAllSiteConfig(siteUrl.getPath(), suffix);
            WorkerConsole.getWorkerConsole().loadAllTaskConfig(taskUrl.getPath(), suffix);
            SiteConfigFileMonitor.getFileMonitor().monitor(siteUrl.getPath(), suffix, ConfigManager.getWorkerConfig().siteMonitor().interval());
            TaskConfigFileMonitor.getFileMonitor().monitor(taskUrl.getPath(), suffix, ConfigManager.getWorkerConfig().taskMonitor().interval());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
