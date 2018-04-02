package org.webant.worker.app;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.webant.worker.WorkerConsole;
import org.webant.worker.config.ConfigManager;
import org.webant.worker.config.SiteConfigFileMonitor;
import org.webant.worker.config.TaskConfigFileMonitor;
import org.webant.worker.config.WorkerConfig;
import org.webant.worker.console.ConsoleOperation;
import org.webant.worker.console.WorkerJmxServer;

import java.net.URL;

/**
 * run a webant worker
 */
public class WebantWorker {
    private static Logger logger = LogManager.getLogger(WebantWorker.class);

    public static void main(String[] args) {
        try {
            ConfigManager.loadWorkerConfig("worker.xml");

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

            String mode = ConfigManager.getWorkerConfig().getMode();
            if (mode.equalsIgnoreCase(WorkerConfig.WORKER_RUN_MODE_STANDALONE())) {
                new ConsoleOperation().start();
            } else if (mode.equalsIgnoreCase(WorkerConfig.WORKER_RUN_MODE_SERVER())) {
                WorkerJmxServer.start();
            } else if (mode.equalsIgnoreCase(WorkerConfig.WORKER_RUN_MODE_NODE())) {
                new ConsoleOperation().start();
            } else {
                new ConsoleOperation().start();
            }

            logger.info("the webant worker is running on " + mode + " mode.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
