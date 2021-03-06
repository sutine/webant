package org.webant.worker;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.webant.commons.entity.SiteConfig;
import org.webant.commons.entity.TaskConfig;
import org.webant.commons.utils.HttpUtils;
import org.webant.worker.config.*;
import org.webant.worker.console.ConsoleOperation;

import java.io.File;
import java.util.HashMap;

public class WorkerConsole {
    private static WorkerConsole workerConsole;
    private static ConsoleOperation console = new ConsoleOperation();

    private WorkerConsole() {
    }

    public static WorkerConsole getWorkerConsole() {
        if (workerConsole == null) {
            synchronized (WorkerConsole.class) {
                if (workerConsole == null) {
                    workerConsole = new WorkerConsole();
                }
            }
        }

        return workerConsole;
    }

    public void loadAllSiteConfig(String directory, String suffix) {
        if (StringUtils.isBlank(directory) || StringUtils.isBlank(suffix))
            return;

        File[] files = FileFilterUtils.filter(FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(suffix)), new File(directory).listFiles());

        loadSiteConfig(files);
    }

    public void loadAllTaskConfig(String directory, String suffix) {
        if (StringUtils.isBlank(directory) || StringUtils.isBlank(suffix))
            return;

        File[] files = FileFilterUtils.filter(FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(suffix)), new File(directory).listFiles());

        loadTaskConfig(files);
    }

    public void loadSiteConfig(File... files) {
        if (files == null || files.length == 0)
            return;

        for (File file : files) {
            SiteConfig siteConfig = loadSiteConfig(file.getPath());
            console.submit(siteConfig);
        }
    }

    public void loadTaskConfig(File... files) {
        if (files == null || files.length == 0)
            return;

        for (File file : files) {
            TaskConfig taskConfig = loadTaskConfig(file.getPath());
            console.submit(taskConfig);
        }
    }

    public void loadHttpTaskConfigList() {
        TaskConfig[] taskConfigs = HttpUtils.getTaskConfigList();

        for (TaskConfig taskConfig : taskConfigs) {
            console.submit(taskConfig);
        }
    }

    public void loadHttpTaskConfig(String... urls) {
        if (urls == null || urls.length == 0)
            return;

        for (String url : urls) {
            TaskConfig taskConfig = loadHttpTaskConfig(url);
            console.submit(taskConfig);
        }
    }

    private TaskConfig loadHttpTaskConfig(String url) {
        return new HttpTaskConfigBuilder()
                .loadTaskConfig(url)
                .build();
    }

    private TaskConfig loadTaskConfig(String configPath) {
        return new TaskConfigBuilder()
                .loadTaskConfig(configPath)
                .build();
    }

    private SiteConfig loadSiteConfig(String configPath) {
        return new SiteConfigBuilder()
                .loadSiteConfig(configPath)
                .build();
    }

    private static TaskConfig buildTaskConfig() {
        SiteConfig siteConfig = buildSiteConfig();
        console.submit(siteConfig);

        return new TaskConfigBuilder()
                .id("task_mahua")
                .name("麻花笑话网")
                .description("麻花笑话网")
                .priority(4)
                .sites(new SiteConfig[] {siteConfig})
                .build();
    }

    private static SiteConfig buildSiteConfig() {
        return new SiteConfigBuilder().id("site_config")
                .description("desc1")
                .name("name1")
                .interval(3000)
                .seeds(new String[]{"http://www.mahua.com"})
                .priority(4)
                .linkProvider(new LinkProviderBuilder()
                        .className("")
                        .params(new HashMap<>())
                        .build())
                .http(new HttpConfigBuilder()
                        .connectTimeout(3000)
                        .socketTimeout(5000)
                        .encoding("UTF-8")
                        .contentType("text/html")
                        .retryTimes(3)
                        .cycleRetryTimes(2)
                        .proxy(false)
                        .headers(new HashMap<>())
                        .build())
                .processors(new SiteConfig.ProcessorConfig[]{
                        new PageProcessorBuilder().regex("http://www.mahua.com/newjokes/index_\\d*.htm")
                                .build(),
                        new PageProcessorBuilder().regex("http://www.mahua.com/xiaohua/\\d*.htm")
                                .className("org.webant.workerConsole.mahua.processor.JokeDetailProcessor")
                                .build()
//                .stores(new HashMap<String, String>[] {
//                })
                })
                .build();
    }
}
