package Customthreadpool.demo;

import Customthreadpool.executor.CustomThreadPool;
import Customthreadpool.executor.rejection.AbortPolicy;
import Customthreadpool.factory.CustomThreadFactory;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomThreadFactory factory = new CustomThreadFactory("MyPool");
        CustomThreadPool pool = new CustomThreadPool(
                2,
                4,
                5,
                TimeUnit.SECONDS,
                10,
                1,
                factory,
                new AbortPolicy()
        );

        // Отправка задач
        for (int i = 0; i < 15; i++) {
            final int taskID = i;
            try {
                pool.execute(() -> {
                    System.out.println("Задача " + taskID + " Запущена в потоке " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Задача " + taskID + " завершена");
                });
                System.out.println("Задача " + taskID + " добавлена в очередь");
            } catch (Exception e) {
                System.out.println("Задача " + taskID + " отклонена. Причина: " + e.getMessage());
            }
        }

        Thread.sleep(10000);

        //Завершение пула
        pool.shutdown();
    }
}