# CurrencyExecutor
通用线程池库


####  根目录下build.gradle配置:
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
 
#### App目录下build.gradle配置:
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.libraMR:CurrencyExecutor:-SNAPSHOT'
	}
---
## 1.处理无需返回结果的任务 
### 示例一
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
```
---

## 2.处理需要返回结果的任务 
### 示例二
```java
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
---
