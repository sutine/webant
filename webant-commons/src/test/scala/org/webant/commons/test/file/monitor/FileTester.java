package org.webant.commons.test.file.monitor;

import org.apache.log4j.Logger;

public class FileTester {
    private static final Logger LOG = Logger.getLogger(FileTester.class);

    private static String directory = "D:\\workspace\\webant\\webant-site\\src\\main\\resources\\site";

    public static void main(String[] args) {
        // Create directory if it does not exist
/*
        try {
            Files.createDirectory(Paths.get(directory));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
*/

        LOG.info("File Monitor...");
        // Start to monitor file event per 500 milliseconds
        FileMonitor.getFileMonitor().monitor(directory, 500);

/*
        LOG.info("File Generater...");
        // New runnable instance
        FileGenerator fileGeneratorRunnable = new FileGenerator(directory);

        // Start to multi-thread for generating file
        for (int i = 0; i < 10; i++) {
            Thread fileGeneratorThread = new Thread(fileGeneratorRunnable);
            fileGeneratorThread.start();
        }
*/
    }

}