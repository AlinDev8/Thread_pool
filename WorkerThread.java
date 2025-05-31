package Customthreadpool.worker;

import Customthreadpool.executor.CustomThreadPool;
import Customthreadpool.factory.CustomThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class WorkerThread extends Thread {
    private final BlockingQueue<Runnable> taskQueue;    // Очередь задач, из которых поток берёт задания
    private final long keepAliveTime;                   // Время простоя потока до завершения
    private final TimeUnit timeUnit;                    // Единица измерения времени для keepAliveTime
    private final CustomThreadPool pool;                // Ссылка на пул потоков для взаимодействия

    public WorkerThread(BlockingQueue<Runnable> taskQueue, CustomThreadFactory factory,
                        long keepAliveTime, TimeUnit timeUnit, CustomThreadPool pool) {
        // Передаёт пустой Runnable, так как реальная работа будет в методе run()
        super(factory.newThread(() ->{}));  // Инициализация потока с именем от фабрики
        this.taskQueue = taskQueue;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.pool = pool;
    }

    // Главный метод потока
    @Override
    public void run() {
        Runnable task;
        try {
            // Основной цикл работы потока
            while (!pool.isShutdown()) {
                // Получение задачи из очереди с таймаутом
                task = taskQueue.poll(keepAliveTime, timeUnit);

                if (task != null) {
                    // Выполнение задачи
                    System.out.println("[Worker] Поток " + getName() + " выполняет задачу: " + task);
                    task.run();
                } else if (shouldTerminate()) {
                    // Завершение потока при простое
                    System.out.println("[Worker] Поток " + getName() + " долго бездействовал, завершение");
                    break;
                }
            }
        } catch (InterruptedException e) {
            // Обработка прерывания при shutdown
        } finally {
            // Уведомление пула о завершении потока
            pool.onWorkerExit(this);
            System.out.println("[Worker] Поток " + getName() + " завершил работу");
        }
    }

    // Проверка условий завершения потока
    private boolean shouldTerminate() {
        return pool.getActiveThreads() > pool.getCorePoolSize();
    }

}
