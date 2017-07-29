package org.webant.worker.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.webant.worker.WorkerConsole;
import org.webant.worker.config.ConfigManager;
import org.webant.worker.config.SiteConfigFileMonitor;
import org.webant.worker.config.TaskConfigFileMonitor;
import org.webant.worker.console.WorkerJmxServer;

import java.net.URL;

/**
 * run worker as a server on single node, and control it by a worker client
 */
public class WorkerServerTest {
    private static Logger logger = LogManager.getLogger(WorkerServerTest.class);

    public static void main(String[] args) {
        try {
            ConfigManager.loadWorkerConfig("worker.xml");

            WorkerJmxServer.start();

            URL taskUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().taskMonitor().dir());
            URL siteUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().siteMonitor().dir());
            String suffix = ".json";

            if (taskUrl == null) {
                logger.error("invalid task config directory!");
                return;
            }
            if (siteUrl == null) {
                logger.error("invalid site config directory!");
                return;
            }
            WorkerConsole.getWorkerConsole().loadAllSiteConfig(siteUrl.getPath(), suffix);
            WorkerConsole.getWorkerConsole().loadAllTaskConfig(taskUrl.getPath(), suffix);
            SiteConfigFileMonitor.getFileMonitor().monitor(siteUrl.getPath(), suffix, ConfigManager.getWorkerConfig().siteMonitor().interval());
            TaskConfigFileMonitor.getFileMonitor().monitor(taskUrl.getPath(), suffix, ConfigManager.getWorkerConfig().taskMonitor().interval());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
