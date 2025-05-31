package Customthreadpool.executor;

/*Импорт стандартных интерфейсов
*Callable<T> - аналог Runnable, но может возвращать результат (Т) и бросать исключения.
*Future<T> - представляет результат асинхронной задачи, который будет получен в будущем  */
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface CustomExecutor extends java.util.concurrent.Executor {
// Future<T> позволяет передавать задачи, возвращающие результат
    <T>Future<T> submit(Callable<T> callable);
// мягкая остановка пула потоков
    void shutdown();
// принудительная остановка пула
    void shutdownNow();
    boolean isShutdown();
}
