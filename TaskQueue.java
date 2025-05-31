package Customthreadpool.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueue {
    private final BlockingQueue<Runnable> queue;                                    // Хранение задач
    private final AtomicInteger pendingTasks = new AtomicInteger(0);      // атомарный счётчик
    private final int maxSize;                                                      // максимальный размер очереди

    public TaskQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedBlockingQueue<>(maxSize);
    }

    // проверка, что не достигнута максимальная вместимость,
    public boolean offer(Runnable task) {
        if (pendingTasks.get() >= maxSize) {
            return false;
        }
        // попытка добавить задачу в очередь
        boolean added = queue.offer(task);
        // если задача добавлена увеличивает счётчик
        if (added) {
            pendingTasks.incrementAndGet();
        }
        return added;
    }

    /*  Блокирует поток на время timeout, пока не появится задача в очереди.
        Если задача извлечена (task != null), уменьшает счётчик pendingTasks.
        Используется в WorkerThread для получения задач.
     */
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable task = queue.poll(timeout, unit);
        if (task != null) {
            pendingTasks.decrementAndGet();
        }
        return task;
    }

    // Возвращает актуальное число задач в очереди (через атомарный счётчик).
    public int size() {
        return pendingTasks.get();
    }

    // Проверяет, пуста ли очередь
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Полностью очищает очередь
    public void clear() {
        queue.clear();
        pendingTasks.set(0);
    }
}
