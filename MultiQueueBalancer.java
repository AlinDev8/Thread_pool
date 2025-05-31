package Customthreadpool.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiQueueBalancer {
    private final List<BlockingQueue<Runnable>> queues;                             // список очередей задач
    private final AtomicInteger currentIndex = new AtomicInteger(0);      // атомарный счётчик для Round Robin
    private final int maxQueues;                                                    // общее количество очередей

    public MultiQueueBalancer(int maxQueues, int queueCapacity) {
        this.maxQueues = maxQueues;
        this.queues = new ArrayList<>(maxQueues);

        // Инициализация очередей
        for (int i = 0; i < maxQueues; i++) {
            queues.add(new LinkedBlockingQueue<>(queueCapacity));
        }
    }

    // Round Robin распределение (циклически перебирает очереди)
    public BlockingQueue<Runnable> getNextQueue() {
        int index = currentIndex.getAndIncrement() % maxQueues;
        return queues.get(index);
    }

    // Least Loaded распределение (выбирает очередь с наименьшим количеством задач)
    public BlockingQueue<Runnable> getLeastLoadedQueue() {
        BlockingQueue<Runnable> leastLoaded = queues.get(0);
        for (BlockingQueue<Runnable> queue : queues) {
            if (queue.size() < leastLoaded.size()) {
                leastLoaded = queue;
            }
        }
        return leastLoaded;
    }

    // Возвращает неизменяемый список всех очередей (для мониторинга)
    public List<BlockingQueue<Runnable>> getAllQueues() {
        return Collections.unmodifiableList(queues);
    }
}
