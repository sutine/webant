package org.webant.worker.test.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SystemConfigManagement {
    private static final int DEFAULT_NO_THREADS = 10;
    private static final String DEFAULT_SCHEMA = "default";

    public static void main(String[] args) throws Exception {
        int rmiPort = 2099;
        String jmxServerName = "jmxrmi";

//        System.setProperty("java.rmi.server.hostname", "localhost");
//        System.setProperty("com.sun.management.jmxremote", "true");
//        System.setProperty("com.sun.management.jmxremote.port", "2099");
//        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
//        System.setProperty("com.sun.management.jmxremote.ssl", "false");

        // Get the MBean server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        // register the MBean
        SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
        ObjectName name = new ObjectName("xman:type=SystemConfig");
        mbs.registerMBean(mBean, name);

//        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:" + rmiPort + "/" + jmxServerName);
//        JMXConnectorServer jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
//        jmxConnServer.start();

        do {
            Thread.sleep(3000);
            System.out.println("Thread Count=" + mBean.getThreadCount() + ":::Schema Name=" + mBean.getSchemaName());
        } while (mBean.getThreadCount() != 0);
    }

}
