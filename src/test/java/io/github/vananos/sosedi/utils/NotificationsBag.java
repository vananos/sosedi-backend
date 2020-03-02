package io.github.vananos.sosedi.utils;

import io.github.vananos.sosedi.models.Notifications;

import static io.github.vananos.sosedi.models.NotificationFrequency.ONE_DAY;

public class NotificationsBag {

    public static Notifications getNotifications() {
        Notifications notifications = new Notifications();
        notifications.setNotificationFrequency(ONE_DAY);
        return notifications;
    }
}