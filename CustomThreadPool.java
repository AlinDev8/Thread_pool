package Customthreadpool.executor;

import Customthreadpool.executor.rejection.RejectionPolicy;
import Customthreadpool.factory.CustomThreadFactory;
import Customthreadpool.worker.WorkerThread;
import Customthreadpool.queue.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Реализация пула потоков

public class CustomThreadPool implements CustomExecutor {
    // параметры
    private final int corePoolSize;         // Минимальное количество потоков
    private final int maxPoolSize;          // Максимальное число потоков
    private final long keepAliveTime;       // Время простоя временных потоков
    private  final TimeUnit timeUnit;
    private final int queueSize;            // Максимальный размер очереди зада
    private final int minSpareThreads;      // Минимальное число свободных потоков

    // состояние
    private final AtomicInteger activeThreads = new AtomicInteger(0);   //текущее число рабочих потоков
    private final List<WorkerThread> workers = new ArrayList<>();                 //список всех потоков
    private final BlockingQueue<Runnable> globalQueue;                            //очередь задач
    private final CustomThreadFactory threadFactory;
    private final RejectionPolicy rejectionPolicy;
    private volatile boolean isShutdown = false;                                  //флаг завершения работы пула

    public CustomThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit, int queueSize,
                            int minSpareThreads, CustomThreadFactory threadFactory, RejectionPolicy rejectionPolicy) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.queueSize = queueSize;
        this.minSpareThreads = minSpareThreads;
        this.globalQueue = new LinkedBlockingQueue<>(queueSize);
        this.threadFactory = threadFactory;
        this.rejectionPolicy = rejectionPolicy;

        //Инициализация core потоков
        for (int i = 0; i < corePoolSize; i++) {
            createWorker();
        }
    }

    // создание и запуск нового WorkerThread и добавляет его в список workers
    private void createWorker() {
        WorkerThread worker = new WorkerThread(globalQueue, threadFactory, keepAliveTime, timeUnit, this);
        workers.add(worker);
        worker.start();
        activeThreads.incrementAndGet();
    }

    @Override
    public void execute(Runnable command) {
        //Проверка, не завершён ли пул
        if (isShutdown) {
            throw new IllegalStateException("Пул потоков завершил работу");
        }

        // создаёт новые потоки если есть свободные слоты / очередь наполовину заполнена / не хватает резервных потоков
        if (activeThreads.get() < maxPoolSize && (globalQueue.size() > queueSize / 2 || activeThreads.get() < minSpareThreads)) {
            synchronized (this) {
                if (activeThreads.get() < maxPoolSize) {
                    createWorker();
                }
            }
        }

        if (!globalQueue.offer(command)) {
            rejectionPolicy.reject(command, this);
        }
    }

    //Оборачивает Callable в FutureTask и передаёт в execute()
    @Override
    public <T>Future<T> submit(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        execute(futureTask);
        return futureTask;
    }

    // 	Мягкое завершение
    @Override
    public void shutdown() {
        isShutdown = true;
        workers.forEach(WorkerThread::interrupt);
    }

    // Жёсткое завершение
    @Override
    public void shutdownNow() {
        isShutdown = true;
        globalQueue.clear();
        workers.forEach(WorkerThread::interrupt);
    }

    // обработчик завершения потока
    // Удаляет поток из списка и уменьшает счётчик
    public void onWorkerExit(WorkerThread worker) {
        workers.remove(worker);
        activeThreads.decrementAndGet();
    }

    // проверка состояния пула
    public boolean isShutdown() {
        return this.isShutdown;
    }

    // Get и Set для доступа к приватным полям
    public int getActiveThreads() {
        return activeThreads.get(); // атомарный счётчик
    }

    public int getCorePoolSize() {
        return corePoolSize;    // фиксированное значение из конструктора
    }
}

