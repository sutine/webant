package org.webant.worker.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.webant.worker.WorkerConsole;
import org.webant.worker.config.ConfigManager;
import org.webant.worker.config.SiteConfigFileMonitor;
import org.webant.worker.config.TaskConfigFileMonitor;
import org.webant.worker.config.WorkerConfig;
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

            if (WorkerConfig.WORKER_RUN_MODE_SERVER().equalsIgnoreCase(ConfigManager.getWorkerConfig().getMode())) {
                URL taskUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().standalone().taskMonitor().dir());
                URL siteUrl = ClassLoader.getSystemResource(ConfigManager.getWorkerConfig().standalone().siteMonitor().dir());
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
                SiteConfigFileMonitor.getFileMonitor().monitor(siteUrl.getPath(), suffix, ConfigManager.getWorkerConfig().standalone().siteMonitor().interval());
                TaskConfigFileMonitor.getFileMonitor().monitor(taskUrl.getPath(), suffix, ConfigManager.getWorkerConfig().standalone().taskMonitor().interval());
            } else if (WorkerConfig.WORKER_RUN_MODE_NODE().equalsIgnoreCase(ConfigManager.getWorkerConfig().getMode())) {
//                String url = "4028b8815da9502a015da951105a0000";
//                String url = ConfigManager.getWorkerConfig().node().queen().getUrl() + "/task/get?id=4028b8815da0ed7e015da0ee05340000";
                WorkerConsole.getWorkerConsole().loadHttpTaskConfigList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
