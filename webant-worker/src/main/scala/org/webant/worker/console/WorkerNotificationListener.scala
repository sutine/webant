package org.webant.worker.console

import javax.management.{AttributeChangeNotification, Notification, NotificationListener}

class WorkerNotificationListener extends NotificationListener {
  override def handleNotification(notification: Notification, handback: scala.Any): Unit = {
    val notify = notification.asInstanceOf[AttributeChangeNotification]
    val value = notify.getNewValue.asInstanceOf[Array[java.lang.Long]]
    val total = value(0)
    val init = value(1)
    val pending = value(2)
    val success = value(3)
    val fail = value(4)

    val gauge = f"${(success + fail).toFloat * 100 / total.toFloat}%2.2f"
    val progress = s"crawl progress $gauge/%. total: $total. init: $init. " +
      s"pending: $pending. success: $success. fail: $fail."

    System.out.println(progress)
  }
}
