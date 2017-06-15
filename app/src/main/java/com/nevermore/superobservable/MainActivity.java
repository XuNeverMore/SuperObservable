package com.nevermore.superobservable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nevermore.superobservable.ob.Dispatcher;
import com.nevermore.superobservable.ob.SuperObservable;
import com.nevermore.superobservable.ob.SuperObservableManager;

public class MainActivity extends AppCompatActivity implements LoginObserver {

    private TextView tv;
    private SuperObservable<LoginObserver> loginObservable;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        syncLoginState();

        init();
    }

    private void syncLoginState() {
        tv.setText(isLogin ? "退出" : "登录");
    }

    private void init() {
        //添加登录状态的被观察者
        SuperObservableManager.getInstance()
                .addObservable(LoginObserver.class, new SuperObservable<LoginObserver>());


        loginObservable = SuperObservableManager.getInstance().getObservable(LoginObserver.class);
        //注册观察者
        loginObservable.registerObserver(this);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogin = !isLogin;

                if (isLogin) {
                   loginObservable.notifyMethod(new Dispatcher<LoginObserver>() {
                       @Override
                       public void call(LoginObserver loginObserver) {
                           loginObserver.onLogin();
                       }
                   });
                } else {

                    loginObservable.notifyMethod(new Dispatcher<LoginObserver>() {
                        @Override
                        public void call(LoginObserver loginObserver) {
                            loginObserver.onLogout();
                        }
                    });
                }


            }
        });

    }

    @Override
    public void onLogin() {
        syncLoginState();
        Toast.makeText(this, "登录", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogout() {
        syncLoginState();
        Toast.makeText(this, "退出", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        //注销观察者
        if (loginObservable != null) {
            loginObservable.unregisterObserver(this);
        }
        super.onDestroy();
    }
}
