package org.webant.worker.app;

import org.apache.commons.lang3.StringUtils;
import org.webant.worker.console.WorkerJmxClient;

import java.util.Scanner;

/**
 * a worker client demo, to connect worker server and control it
 */
public class WorkerClient {
    public static void main(String[] args) {
        console();
    }

    private static void console() {
        String promotion = "Webant # ";

        while (true) {
            System.out.print(promotion);

            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.nextLine();
            if (cmd == null || cmd.equals(""))
                continue;

            cmd = cmd.trim().toLowerCase();

            if (!(cmd.startsWith("c") || cmd.startsWith("conn") || cmd.startsWith("connect")) && !WorkerJmxClient.isConnected()) {
                System.out.println("lost connection! please connect to server first.");
                continue;
            }

            try {
                if (cmd.startsWith("c") || cmd.startsWith("conn") || cmd.startsWith("connect")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) System.out.println(WorkerJmxClient.connect());
                    else if (items.length == 2) System.out.println(WorkerJmxClient.connect(items[1], "1099"));
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.connect(items[1], items[2]));
                } else if (cmd.startsWith("l") || cmd.startsWith("ls") || cmd.startsWith("ll") || cmd.startsWith("list")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) {
                        String[][] result = WorkerJmxClient.list();
                        for (String[] texts : result) {
                            for (String text : texts) {
                                System.out.println(text);
                            }
                        }
                    }
                    else if (items.length == 2) {
                        String[] result = WorkerJmxClient.list(items[1]);
                        for (String item : result) {
                            System.out.println(item);
                        }
                    }
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.list(items[1], items[2]));
                } else if (cmd.startsWith("start")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) {
                        String[][] result = WorkerJmxClient.start();
                        for (String[] texts : result) {
                            for (String text : texts) {
                                System.out.println(text);
                            }
                        }
                    }
                    else if (items.length == 2) {
                        String[] result = WorkerJmxClient.start(items[1]);
                        for (String item : result) {
                            System.out.println(item);
                        }
                    }
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.start(items[1], items[2]));
                }
                else if (cmd.startsWith("stop")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) {
                        String[][] result = WorkerJmxClient.stop();
                        for (String[] texts : result) {
                            for (String text : texts) {
                                System.out.println(text);
                            }
                        }
                    }
                    else if (items.length == 2) {
                        String[] result = WorkerJmxClient.stop(items[1]);
                        for (String item : result) {
                            System.out.println(item);
                        }
                    }
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.stop(items[1], items[2]));
                }
                else if (cmd.startsWith("pause") || cmd.startsWith("pause")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) {
                        String[][] result = WorkerJmxClient.pause();
                        for (String[] texts : result) {
                            for (String text : texts) {
                                System.out.println(text);
                            }
                        }
                    }
                    else if (items.length == 2) {
                        String[] result = WorkerJmxClient.pause(items[1]);
                        for (String item : result) {
                            System.out.println(item);
                        }
                    }
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.pause(items[1], items[2]));
                }
                else if (cmd.startsWith("reset")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) {
                        String[][] result = WorkerJmxClient.reset();
                        for (String[] texts : result) {
                            for (String text : texts) {
                                System.out.println(text);
                            }
                        }
                    }
                    else if (items.length == 2) {
                        String[] result = WorkerJmxClient.reset(items[1]);
                        for (String item : result) {
                            System.out.println(item);
                        }
                    }
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.reset(items[1], items[2]));
                }
                else if (cmd.startsWith("p") || cmd.startsWith("progress")) {
                    String[] items = cmd.split(" ");
                    if (items.length == 1) System.out.println(WorkerJmxClient.progress());
                    else if (items.length == 2) System.out.println(WorkerJmxClient.progress(items[1]));
                    else if (items.length >= 3) System.out.println(WorkerJmxClient.progress(items[1], items[2]));
                } else if (cmd.startsWith("submit task")) {
                    String path = StringUtils.substringAfter(cmd, "submit task").trim();
                    if (StringUtils.isNotBlank(path)) {
                        WorkerJmxClient.submitTask(path);
                        System.out.println("loading task config...");
                    } else {
                        System.out.println("config file name can not be null!");
                    }
                } else if (cmd.startsWith("submit site")) {
                    String path = StringUtils.substringAfter(cmd, "submit site").trim();
                    if (StringUtils.isNotBlank(path)) {
                        WorkerJmxClient.submitSite(path);
                        System.out.println("loading site config...");
                    } else {
                        System.out.println("config file name can not be null!");
                    }
                }
                else if ("shutdown".equalsIgnoreCase(cmd))
                    System.out.println(WorkerJmxClient.shutdown());
                else if ("exit".equalsIgnoreCase(cmd))
                    break;
                else {
                    System.out.println("unknowned command!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
