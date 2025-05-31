package Customthreadpool.executor.rejection;

// Тихий отказ

import Customthreadpool.executor.CustomExecutor;

/* 1) Логирует отказ
2) Игнорирует задачу (не выполняется и не обрабатывается исключение)
*/
public class DiscardPolicy implements RejectionPolicy {
    @Override
    public void reject(Runnable task, CustomExecutor executor) {
        System.out.println("[Rejected] Задача " + task + " проигнорирована");
    }
}
