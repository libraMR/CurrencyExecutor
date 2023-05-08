package com.example.currency;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntRange;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @E-Mail mh2593661531@gmail.com
 * <p>
 * 通用线程池库
 * <p>
 * 支持按任务的优先级去执行，可以根据需要调整不同任务的优先级。
 * 支持线程池暂停和恢复功能，适用于批量文件下载、上传等场景。
 * 支持异步结果主动回调主线程，方便处理异步任务的结果。
 * 支持线程池能力监控，可以查看线程池的使用情况和性能瓶颈。
 * 支持耗时任务检测，可以识别并处理耗时任务，避免对性能的影响。
 * 支持定时和延迟功能，可以根据需要设置任务的执行时间，提高应用的灵活性。
 */
public class CurrencyExecutor {
    private static String TAG = "CurrencyExecutor";
    private Boolean isPaused = false;

    private final ThreadPoolExecutor currencyExecutor;
    private final ReentrantLock currencyLock = new ReentrantLock();
    private final Condition pauseCondition;

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private CurrencyExecutor() {
        pauseCondition = currencyLock.newCondition();

        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount + 1;
        int maxPoolSize = cpuCount * 2 + 1;
        PriorityBlockingQueue<Runnable> blockingQueue = new PriorityBlockingQueue<>();
        long keepAliveTime = 30L;
        TimeUnit unit = TimeUnit.SECONDS;
        AtomicLong seq = new AtomicLong();


        ThreadFactory threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("currency-executor-" + seq.getAndIncrement());
                return thread;
            }
        };

        currencyExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                blockingQueue,
                threadFactory
        ) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                if (isPaused) {
                    currencyLock.lock();
                    try {
                        pauseCondition.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        currencyLock.unlock();
                    }
                }
            }

            /**
             * 监控线程池耗时的任务
             * 线程创建的数量
             * 正在运行数量
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                Log.e(TAG, "已执行完的任务的优先级是：" + ((CurrencyPriorityRunnable) r).mPriority);
            }
        };
    }


    private static class CurrencyExecutorHolder{
        private static final CurrencyExecutor INSTANCE= new CurrencyExecutor();
    }

    public static CurrencyExecutor getInstance(){
        return CurrencyExecutorHolder.INSTANCE;
    }

    /**
     * 处理无需返回结果的任务
     * @param priority 线程优先级
     * @param runnable 任务runnable
     */
    public void execute(@IntRange(from = 0, to = 10) int priority, Runnable runnable) {
        currencyExecutor.execute(new CurrencyPriorityRunnable(priority, runnable));
    }


    /**
     * 处理需要返回结果的任务
     * @param priority 线程优先级
     * @param runnable 任务runnable
     */
    public void execute(@IntRange(from = 0, to = 10) int priority, Callable<?> runnable) {
        currencyExecutor.execute(new CurrencyPriorityRunnable(priority, runnable));
    }


    public abstract static class Callable<T> implements Runnable {

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onPrepare();
                }
            });

            T t = onBackground();

            //移除所有消息
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onCompleted(t);
                }
            });
        }

        public void onPrepare() {

        }

        public abstract T onBackground();

        public abstract void onCompleted(T t);
    }


    static class CurrencyPriorityRunnable implements Runnable, Comparable<CurrencyPriorityRunnable> {
        int mPriority;
        Runnable mRunnable;

        public CurrencyPriorityRunnable(int priority, Runnable runnable) {
            this.mPriority = priority;
            this.mRunnable = runnable;
        }


        @Override
        public int compareTo(CurrencyPriorityRunnable priorityRunnable) {
            return Integer.compare(priorityRunnable.mPriority, this.mPriority);
        }

        @Override
        public void run() {
            mRunnable.run();
        }
    }


    //暂停线程池任务
    public void executorPause() {
        synchronized (this) {
            currencyLock.lock();
            try {
                isPaused = true;
                Log.e(TAG, "currencyExecutor is paused");
            } finally {
                currencyLock.unlock();
            }
        }
    }

    //恢复线程池任务
    public void executorResume() {
        synchronized (this) {
            currencyLock.lock();
            try {
                isPaused = false;
                pauseCondition.signalAll();
            } finally {
                currencyLock.unlock();
            }
            Log.e(TAG, "currencyExecutor is resumed");
        }
    }
}
