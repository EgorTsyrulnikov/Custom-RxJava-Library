package com.customrx;

import com.customrx.core.Observable;
import com.customrx.core.Observer;
import com.customrx.schedulers.Schedulers;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Запуск примера использования Custom RxJava ===");
        
        Observable.<String>create(emitter -> {
            System.out.println("[Источник] Генерация данных в потоке: " + Thread.currentThread().getName());
            emitter.onNext("RxJava");
            emitter.onNext("Custom");
            emitter.onNext("Implementation");
            emitter.onComplete();
        })
        .subscribeOn(Schedulers.io()) // Источник работает в IO пуле
        .map(String::toUpperCase) // Перевод в верхний регистр
        .filter(s -> s.length() > 6) // Фильтрация коротких слов
        .observeOn(Schedulers.computation()) // Обработка результата в Computation пуле
        .subscribe(new Observer<String>() {
            @Override
            public void onNext(String item) {
                System.out.println("[Подписчик] Получено: " + item + " (в потоке " + Thread.currentThread().getName() + ")");
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[Подписчик] Ошибка: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("[Подписчик] Поток данных успешно завершен (в потоке " + Thread.currentThread().getName() + ")");
            }
        });

        // Даем время асинхронным потокам завершить работу
        Thread.sleep(1000);
        System.out.println("=== Завершение программы ===");
    }
}
