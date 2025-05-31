package Customthreadpool.executor.rejection;

// Прерывание с исключением

import Customthreadpool.executor.CustomExecutor;
import java.util.concurrent.RejectedExecutionException;

/* 1) Логирует отказ
2) бросает исключение RejectedExecutionException
3) задача не выполняется
* */
public class AbortPolicy implements RejectionPolicy {
    @Override
    public  void reject(Runnable task, CustomExecutor executor) {
        System.out.println("[Rejected] Задача " + task + " отклонена из-за перегрузки пула!");
        throw new RejectedExecutionException("Задача " + task + " отклонена пулом " + executor);
    }
}
