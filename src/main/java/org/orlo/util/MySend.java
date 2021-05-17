package org.orlo.util;

import org.orlo.task.SocketClientTask;
import org.orlo.task.base.TaskConfig;

public class MySend {

    public static void sendMsgToController(String info, String msg) {
        String msgToController = String.format("{\"info\":\"%s\", \"msg\":\"%s\"}",
                info, msg);
        SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
//                System.out.println("send ok");
        },
                TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
        socketClientTask.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketClientTask.stop();
    }




    public static void sendMsgToController(String info, String loginMac, String switcher) {
        String msgToController = String.format("{\"info\":\"%s\", \"loginMac\":\"%s\", \"switcher\":\"%s\"}",
                info, loginMac, switcher);
        SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
//                System.out.println("send ok");
        },
                TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
        socketClientTask.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketClientTask.stop();
    }

    public static void sendMsgToController(String srcMac, String dstIP, String switcher,
                                           String srcPort, String dstPort, String protocol) {

        String msgToController = String.format("{\"info\":\"1\", \"srcMac\":\"%s\", \"dstIP\":\"%s\", \"switcher\":\"%s\", " +
                        "\"srcPort\":\"%s\", \"dstPort\":\"%s\", \"protocol\":\"%s\"}",
                srcMac, dstIP, switcher, srcPort, dstPort, protocol);
        SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
//                System.out.println("send ok");
        },
                TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
        socketClientTask.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketClientTask.stop();
    }
}
