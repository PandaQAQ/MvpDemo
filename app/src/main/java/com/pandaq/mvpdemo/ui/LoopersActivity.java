package com.pandaq.mvpdemo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pandaq.mvpdemo.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PandaQ on 2017/3/6.
 * email:767807368@qq.com
 */

public class LoopersActivity extends BaseActivity {
    private static long TOTAL_TIME = 10000;
    private final static long ONECE_TIME = 1000;
    private static int TOTAL_TIME_SEC = 10;
    @BindView(R.id.handler_postDelayed)
    RadioButton mHandlerPostDelayed;
    @BindView(R.id.timerTask)
    RadioButton mTimerTask;
    @BindView(R.id.ScheduledExecutorService)
    RadioButton mScheduledExecutorService;
    @BindView(R.id.RxJava)
    RadioButton mRxJava;
    @BindView(R.id.CountDownTimer)
    RadioButton mCountDownTimer;
    @BindView(R.id.start)
    Button mStart;
    @BindView(R.id.rg_types)
    RadioGroup mRgTypes;
    @BindView(R.id.tv_count_value)
    TextView mTvValue;
    private LooperHandler mHandler = new LooperHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loopers);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.start)
    public void onClick() {
        TOTAL_TIME_SEC = (int) (TOTAL_TIME / 1000); //重置时间
        switch (mRgTypes.getCheckedRadioButtonId()) {
            case R.id.handler_postDelayed:
                handlerPostDelayed();
                break;
            case R.id.timerTask:
                timerTask();
                break;
            case R.id.ScheduledExecutorService:
                scheduledExecutorService();
                break;
            case R.id.RxJava:
                rxJava();
                break;
            case R.id.CountDownTimer:
                countDownTimer.start();
                break;
        }
    }

    /**
     * handler_postDelayed 方法实现
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = mHandler.obtainMessage(2);
            mHandler.sendMessage(msg);
        }

    };

    private void handlerPostDelayed() {
        mHandler.postDelayed(mRunnable, ONECE_TIME);
    }

    /**
     * TimkerTask 方式实现
     */
    private Timer timer;

    private void timerTask() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = mHandler.obtainMessage(1);
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, ONECE_TIME);
    }

    /**
     * ScheduledExecutorService 方式实现
     */
    private ScheduledExecutorService scheduled;

    private void scheduledExecutorService() {
        //初始化一个线程池大小为 1 的 ScheduledExecutorService
        scheduled = new ScheduledThreadPoolExecutor(1);
        mStart.setEnabled(false);//在发送数据的时候设置为不能点击
        mStart.setBackgroundColor(Color.GRAY);//背景色设为灰色
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage(0);
                mHandler.sendMessage(msg);
            }
        }, 0, ONECE_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * RxJava 方式实现
     */
    private Disposable mDisposable;

    private void rxJava() {
        final long count = TOTAL_TIME / 1000;
        Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (count + 1)) //设置总共发送的次数
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return count - aLong;
                    } //将数值倒置

                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mStart.setEnabled(false);//在发送数据的时候设置为不能点击
                        mStart.setBackgroundColor(Color.GRAY);//背景色设为灰色
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        String value = String.valueOf(aLong);
                        mTvValue.setText(value);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        mTvValue.setText(getResources().getString(R.string.done));
                        mStart.setEnabled(true);
                        mStart.setBackgroundColor(Color.parseColor("#f97e7e"));
                    }
                });
    }

    /**
     * CountDownTimer 实现倒计时
     */
    private CountDownTimer countDownTimer = new CountDownTimer(TOTAL_TIME, ONECE_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            String value = String.valueOf((int) (millisUntilFinished / 1000));
            mTvValue.setText(value);
        }

        @Override
        public void onFinish() {
            mTvValue.setText(getResources().getString(R.string.done));
        }
    };

    /**
     * handler 持有当前 Activity 的弱引用防止内存泄露
     */
    private static class LooperHandler extends Handler {
        WeakReference<LoopersActivity> mWeakReference;

        LooperHandler(LoopersActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoopersActivity loopersActivity = mWeakReference.get();
            switch (msg.what) {
                case 0:
                    loopersActivity.mTvValue.setText(String.valueOf(TOTAL_TIME_SEC));
                    if (TOTAL_TIME_SEC <= 0) {
                        loopersActivity.scheduled.shutdown();
                        loopersActivity.mTvValue.setText(loopersActivity.getResources().getString(R.string.done));
                        loopersActivity.mStart.setEnabled(true);
                        loopersActivity.mStart.setBackgroundColor(Color.parseColor("#f97e7e"));
                    }
                    TOTAL_TIME_SEC--;
                    break;
                case 1:
                    loopersActivity.mTvValue.setText(String.valueOf(TOTAL_TIME_SEC));
                    if (TOTAL_TIME_SEC <= 0) {
                        loopersActivity.timer.cancel();
                        loopersActivity.timer = null;
                        loopersActivity.mTvValue.setText(loopersActivity.getResources().getString(R.string.done));
                        loopersActivity.mStart.setEnabled(true);
                        loopersActivity.mStart.setBackgroundColor(Color.parseColor("#f97e7e"));
                    }
                    TOTAL_TIME_SEC--;
                    break;
                case 2:
                    loopersActivity.mHandler.postDelayed(loopersActivity.mRunnable, ONECE_TIME);
                    loopersActivity.mTvValue.setText(String.valueOf(TOTAL_TIME_SEC));
                    if (TOTAL_TIME_SEC <= 0) {
                        loopersActivity.mHandler.removeCallbacks(loopersActivity.mRunnable);
                        loopersActivity.timer = null;
                        loopersActivity.mTvValue.setText(loopersActivity.getResources().getString(R.string.done));
                        loopersActivity.mStart.setEnabled(true);
                        loopersActivity.mStart.setBackgroundColor(Color.parseColor("#f97e7e"));
                    }
                    TOTAL_TIME_SEC--;
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
