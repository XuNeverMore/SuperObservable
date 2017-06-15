package com.nevermore.superobservable.ob;

/**
 * 
 * author XuNeverMore
 * create on 2017/6/15 0015
 * github https://github.com/XuNeverMore
 */

public interface Dispatcher<T> {
    void call(T t);
}
