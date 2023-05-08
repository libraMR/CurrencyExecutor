package com.example.myexecutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.currency.CurrencyExecutor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //示例一
        CurrencyExecutor.getInstance().execute(1, new Runnable() {
            @Override
            public void run() {

            }
        });

        //暂停线程池任务
        CurrencyExecutor.getInstance().executorPause();

        //恢复线程池任务
        CurrencyExecutor.getInstance().executorResume();


        //示例二
        CurrencyExecutor.getInstance().execute(1,new CurrencyExecutor.Callable<String>() {
            @Override
            public String onBackground() {
                return null;
            }

            @Override
            public void onCompleted(String s) {

            }
        });

        //暂停线程池任务
        CurrencyExecutor.getInstance().executorPause();

        //恢复线程池任务
        CurrencyExecutor.getInstance().executorResume();
    }
}