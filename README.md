# MvpDemo

### 什么是MVP？
M：model层，负责数据提供和数据持久化,数据库，远程服务api之类的
V: view层，负责跟用户交互显示界面 
P：presenter层， presenter意思是主持人，我觉得把他形容成中介更合理。负责将M层的数据提供给V层，负责处理一些后台任务。

### model-view-presenter间的关系
mvp模式中的m层被完全分离出来，不会跟model层有任何直接的交互。而是通过接口与对应的presenter进行绑定。在view层中实现接口直接使用接口中的数据进行显示。
presenter层也是一个独立出来的类跟view的生命周期无关。在presenter层中进行数据请求操作和逻辑处理之后通过view的接口类调用接口方法为view提供处理好的数据。
model层则包含了各种数据实体的获取，远程api的调用获取服务器数据。将这些原始数据交给presenter层处理。

![mvp关系图（来源:泡在网上的日子）][1]
通过这张图可以比较直接的看出MVP各个层之间的关系，data与view间不会产生直接关系，而是通过代理presenter进行通信。
### Demo实战
前面说了这么多，都是在各种描述。要说明白一个东西还是代码最简单粗暴。下面是一个从知乎日报获取数据并显示在recyclerView中的demo。
**目录结构:**
这是一个demo的Java文件目录结构截图

![mvpdemo][2]
从目录截图可以看出MVP模式的一个特点，类和包的量大大的增加了。但是细看会发现类和包虽然多但是结构非常明确，看起来也不会觉得凌乱。包和类的增加是模块化和面向接口变成的必然结果。看似增加了工作量，多写了好多类但因为相对于MVC模式的高度解耦和模块化对后期的维护和开发提供了极大的便利。不再回牵一发而动全身，改了删了一个功能其他功能也受影响。当然好处还是要通过代码才能说清楚。
**代码说明:**
说代码之前我先画个草图把层与层之间的关系对应一下，理一理

![MVP模式一次获取数据的过程（按箭头顺序）][3]
本例中在MainActivity中的所有可能操作都通过实现IMainActivity的接口方法来实现:
``` java
public interface IMainActivity {
    //显示进度条
    void showProgressBar();
    //隐藏进度条
    void hidProgressBar();
    //加载数据
    void loadData();
//  loadMore refresh 就大家自由发挥了demo中就不写了
//    void loadMore();
//
//    void refresh();
//
//    void refreshSuccess(ArrayList<ZhihuStory> stories);
//
//    void refreshFail(String errCode, String errMsg);
//
//    void loadSuccess(ArrayList<ZhihuStory> stories);
//
//    void loadFail(String errCode, String errMsg);
    //加载数据成功回调
    void getDataSuccess(ArrayList<ZhihuStory> stories);
    //加载数据失败回调
    void getDataFail(String errCode, String errMsg);

}
```
MainActivity中new出MainPresenter对象达到持有Presenter的目的在MainPrensenter中则通过构造函数获得IMainActivity对象（实现了IMainActivity接口的MainActivity）。这样Activity与Prensenter就能通过mPrenster对象和mIMainActivity进行交互。具体代码如下：
``` java
public class MainActivity extends AppCompatActivity implements IMainActivity {

    ZhihuStoryAdapter mAdapter;
    @BindView(R.id.zhihudaily_list)
    RecyclerView mZhihudailyList;
    @BindView(R.id.activity_main)
    RelativeLayout mActivityMain;
    @BindView(R.id.progressbar)
    ProgressBar mProgressbar;
    //将View与Presenter关联
    private MainPresenter mPresenter = new MainPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mZhihudailyList.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }
    //加载数据
    @Override
    public void loadData() {
        mPresenter.loadData();
    }
    //显示进度条
    @Override
    public void showProgressBar() {
        mProgressbar.setVisibility(View.VISIBLE);
    }

    //隐藏进度条
    @Override
    public void hidProgressBar() {
        mProgressbar.setVisibility(View.GONE);
    }

    //实现获取成功的接口方法
    @Override
    public void getDataSuccess(ArrayList<ZhihuStory> stories) {
        //不管界面怎么改只要与presenter进行绑定都得到的是stories数据，view界面只负责展示不关心怎么获取怎么处理解析数据
        if (mAdapter != null) {
            mAdapter.addItem(stories);
        } else {
            mAdapter = new ZhihuStoryAdapter(this, stories);
            mZhihudailyList.setAdapter(mAdapter);
        }
    }
    
    //实现获取失败的接口方法
    @Override
    public void getDataFail(String errCode, String errMsg) {
        Snackbar.make(mActivityMain, errMsg, Snackbar.LENGTH_SHORT).show();
    }
}
```
在Presenter以及modle层逻辑业务都写好之后需要修改界面会很方便，怎么改都不会影响到数据的获取。需要在其他界面展示相同数据时只需初始化Prensenter并实现IMainActivity接口放法即可。

在Presenter中：
``` java
    public class MainPresenter {
    private IMainActivity mIMainActivity;
    private ZhihuDailyBiz mDailyBiz;

    public MainPresenter(IMainActivity IMainActivity) {
        //绑定获得View对象
        mIMainActivity = IMainActivity;
        //绑定获得业务实现对象
        mDailyBiz = new ZhihuDailyBiz();
    }
    
    //对View提供的调用方法
    public void loadData() {
        mIMainActivity.showProgressBar();
        mDailyBiz.getStoryData("news/latest", new OnEventLister<ArrayList<ZhihuStory>>() {
            @Override
            public void onSuccess(ArrayList<ZhihuStory> response) {
                mIMainActivity.hidProgressBar();
                mIMainActivity.getDataSuccess(response);
            }

            @Override
            public void onFail(String errCode, String errMsg) {
                mIMainActivity.hidProgressBar();
                mIMainActivity.getDataFail(errCode, errMsg);
            }
        });
    }

}
```
Presenter提供`loadData()`方法供view调用，方法内部再通过业务对象去调用业务方法。业务类中将结果通过接口放法返回给Presenter，presenter获取到结果后再通过持有的Iview对象（实现IMainActivity接口的MainActivity）调用接口放法将结果传递给view显示。在Presenter中也可以对数据进行一些其他如存储之类的操作。

Model层比较随意，不同逻辑不同需求场景对用着不同的实现方式。这里是一次向服务器请求数据的需求：
``` java
public class ZhihuDailyBiz {

    //获取数据的具体实现方法
    public void getStoryData(final String url, final OnEventLister<ArrayList<ZhihuStory>> eventLister) {
        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                try {
                    String result = HttpServiceManager.httpGet(url);
                    Gson gson = new Gson();
                    ZhiHuDaily daily = gson.fromJson(result, ZhiHuDaily.class);
                    final ArrayList<ZhihuStory> stories = daily.getStories();
                    if (stories != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                eventLister.onSuccess(stories);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                eventLister.onFail("-100", "获取日报失败！");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventLister.onFail("-100", "获取日报失败！");
                        }
                    });
                }
            }
        }.start();
    }
}
```
这是请求数据的具体实现方法，Http请求已经进行过封装可以在[demo][4]中去看一下。在Presenter中调用此方法之后，会进行网络请求。请求结果将通过传入的OnEventListener对象的两个接口方法回馈给Presenter，（presenter中实现了onsuccess和onfail方法）。可以根据服务器的各种状态码和message作为成功还是失败的依据。这里直接用解析story结果是否为空来判断。


### 最后
MVP模式能降低各个层的耦合度，提高代码的可读性和项目的可维护性。网络还有异步操作这些如果配合RxAndroid+Retrofit使用效果更佳，RxAndroid+Retrofit的网络框架我现在也只是会用阶段，待我深入理解了之后会再来一篇文章与大家分享
更多内容可以关注简书[PandaQ404][4]   

## 新增 
### Retrofit+RxAndroid 
详细内容移步简书[优雅的构建Android项目之RxAndroid+Retrofit网络请求][5]

### okhttp兼容自签名证书https链接
详细内容移步简书[Okhttp 访问自签名证书 HTTPS 地址解决方案][6]

### 自定义 View 之组合大法
详细内容移步简书[自定义 View 之组合大法][7]

  [1]: http://oddbiem8l.bkt.clouddn.com/mvp.png
  [2]: http://oddbiem8l.bkt.clouddn.com/project.png
  [3]: http://oddbiem8l.bkt.clouddn.com/mvp%E4%B8%80%E4%B8%AA%E8%AF%B7%E6%B1%82%E7%9A%84%E8%BF%87%E7%A8%8B.png
  [4]: http://www.jianshu.com/u/aa53f5d59037
  [5]: http://www.jianshu.com/p/a7635e39c5ac
  [6]: http://www.jianshu.com/p/cc7ae2f96b64
  [7]: http://www.jianshu.com/p/92ae9fb83e74
