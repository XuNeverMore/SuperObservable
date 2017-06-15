package com.nevermore.superobservable.ob;

import android.database.Observable;

/**
 *
 * author XuNeverMore
 * create on 2017/6/15 0015
 * github https://github.com/XuNeverMore
 */


public class SuperObservable<T> extends Observable<T> {

    public void notifyMethod(Dispatcher<T> dispatcher){

        synchronized (mObservers){
            for (int i = mObservers.size()-1; i >= 0; i--) {
                T t = mObservers.get(i);
                if(t!=null){
                    if(dispatcher!=null){
                        dispatcher.call(t);
                    }
                }
            }
        }
    }


}
