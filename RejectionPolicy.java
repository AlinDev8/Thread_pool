package Customthreadpool.executor.rejection;

import Customthreadpool.executor.CustomExecutor;

// Интерфейс политики отказа
// метод вызывается, когда очередь задач заполнены, все потоки заняты
public interface RejectionPolicy {
    void reject(Runnable task, CustomExecutor executor);
}
