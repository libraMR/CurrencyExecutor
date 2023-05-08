# CurrencyExecutor
通用线程池库

Step 1.Add it in your root build.gradle at the end of repositories:

```xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency
```xml
dependencies {
	        implementation 'com.github.libraMR:CurrencyExecutor:master-SNAPSHOT'
	}
```


```java
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
```
