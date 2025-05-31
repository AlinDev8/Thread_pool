package Customthreadpool.factory;

import java.util.concurrent.ThreadFactory;  //стандартный интерфейс для создания потоков
import java.util.concurrent.atomic.AtomicInteger;   //потокобезопасный счётчик для генерации уникальных ID потоков

public class CustomThreadFactory implements ThreadFactory {
    //создаём потокообразный счётчик (начальное значение - 1)
    private final AtomicInteger threadCounter = new AtomicInteger(1);
    // хранение префикса для имён протоколов
    private final String threadNamePrefix;

    //принимаем имя пула и формируем префикс для имён протоколов
    public CustomThreadFactory(String poolName) {
        this.threadNamePrefix = poolName + "-worker-";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = threadNamePrefix + threadCounter.getAndIncrement();
        System.out.println("[ThreadFactory] Создан новый поток: " + threadName);
        // Создание нового потока:
        Thread thread = new Thread(r, threadName);  // r - задача, threadName - имя потока
        // Установка обработчика не пойманных исключений (которые могут возникнуть)
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("[Worker] Поток " + t.getName() + " столкнулся с исключением: " + e.getMessage());
        });
        return thread;
    }
}
