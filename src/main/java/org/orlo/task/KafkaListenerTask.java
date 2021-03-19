package org.orlo.task;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.orlo.task.base.AbstractStoppableTask;
import org.orlo.util.Consumer;
import org.orlo.util.MQDict;
import java.util.concurrent.BlockingQueue;

public class KafkaListenerTask extends AbstractStoppableTask {
    BlockingQueue<String> blockingQueueA;
    BlockingQueue<String> blockingQueueC;
    BlockingQueue<String> blockingQueueD;
    public KafkaListenerTask( BlockingQueue<String> blockingQueueA,  BlockingQueue<String> blockingQueueC,
                              BlockingQueue<String> blockingQueueD) {
        this.blockingQueueA = blockingQueueA;
        this.blockingQueueC = blockingQueueC;
        this.blockingQueueD = blockingQueueD;
    }
    @Override
    public void run() {
        isRunning.set(true);
        while (!stopRequested) {
            try {
                ConsumerRecords<String, String> recordsA = Consumer.consumer.poll(MQDict.CONSUMER_POLL_TIME_OUT);
                recordsA.forEach((ConsumerRecord<String, String> record)->{
                    switch (record.topic()) {
                        case MQDict.CONSUMER_TOPICA:
                            try {
                                blockingQueueA.put(record.value());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MQDict.CONSUMER_TOPICC:
                            try {
                                blockingQueueC.put(record.value());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MQDict.CONSUMER_TOPICD:
                            try {
                                blockingQueueD.put(record.value());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    System.out.println("revice: key ==="+record.key()+" value ===="+record.value()+" topic ==="+record.topic());
                });
            } catch (Exception e) {
                stopRequested = true;
            }
        }
        isRunning.set(false);
    }
}