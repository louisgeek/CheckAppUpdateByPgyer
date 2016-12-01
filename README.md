# CheckAppUpdateByPgyer
通过蒲公英Pgyer的接口 Service 实现带进度下载App 通知栏显示 在线更新 自动更新Demo

![image](https://raw.githubusercontent.com/louisgeek/CheckAppUpdateByPgyer/master/screenshots/pic1.png)

![image](https://raw.githubusercontent.com/louisgeek/CheckAppUpdateByPgyer/master/screenshots/pic2.png)

![image](https://raw.githubusercontent.com/louisgeek/CheckAppUpdateByPgyer/master/screenshots/pic3.png)

![image](https://raw.githubusercontent.com/louisgeek/CheckAppUpdateByPgyer/master/screenshots/pic4.png)







Add it in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}




Add the dependency

    dependencies {
	        compile 'com.github.louisgeek:CheckAppUpdateByPgyer:1.1.1'
	}




Use

1、

    //normal 手动检测  提示下载  无更新提示
      id_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleCheckUpdateTool.updateNormal(StartActivity.this,Pgyer.APP_ID,Pgyer.API_KEY);
            }
        });


     @Override
    protected void onDestroy() {
        super.onDestroy();
        CheckUpdateHelper.unregisterCheckUpdate(this);
    }

2、	
		
	  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	
	......
    //normal  自动检测  提示下载  无更新不提示
    SimpleCheckUpdateTool.updateNormal_HasNoMsg(StartActivity.this,Pgyer.APP_ID,Pgyer.API_KEY);
	}
		
     @Override
    protected void onDestroy() {
        super.onDestroy();
        CheckUpdateHelper.unregisterCheckUpdate(this);
    }


3、
		
		  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	......
    //simple 静默下载，提示安装
    SimpleCheckUpdateTool.updateSilentDown(StartActivity.this,Pgyer.APP_ID,Pgyer.API_KEY);
     }
