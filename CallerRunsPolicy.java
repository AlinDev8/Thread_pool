package Customthreadpool.executor.rejection;

// Выполнение в вызывающем потоке

import Customthreadpool.executor.CustomExecutor;

/* 1) Логирует отказ
2) Если пул не завершён, задача выполняется в потоке, который вызвал execute()
3) Замедляет вызывающий поток, но гарантирует выполнение задачи
*/
public class CallerRunsPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, CustomExecutor executor) {
        System.out.println("[Rejected] Задача " + task + " выполняется в вызывающем потоке (caller thread)");
        if (!executor.isShutdown()) {
            task.run();
        }
    }
}
