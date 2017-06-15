# SuperObservable

    观察者模式封装。
其实android源码里已经有观察者模式的封装了：
 ```
package android.database;
import java.util.ArrayList;
public abstract class Observable<T> {
    
    protected final ArrayList<T> mObservers = new ArrayList<T>();
    public void registerObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            if (mObservers.contains(observer)) {
                throw new IllegalStateException("Observer " + observer + " is already registered.");
            }
            mObservers.add(observer);
        }
    }

    public void unregisterObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                throw new IllegalStateException("Observer " + observer + " was not registered.");
            }
            mObservers.remove(index);
        }
    }

    public void unregisterAll() {
        synchronized(mObservers) {
            mObservers.clear();
        }
    }
}
 ```
可以看到这是一个抽象类，主要提供了三个方法，用来注册和注销观察者。使用起来就是继承Observable，然后添加一些notify的方法来通知观察者。
DataSetObservable就是这么做的：
    
```
package android.database;

public class DataSetObservable extends Observable<DataSetObserver> {

    public void notifyChanged() {
        synchronized(mObservers) {      
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public void notifyInvalidated() {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInvalidated();
            }
        }
    }
}
```
但是我也学着DataSetObservable这么用了一段时间，发现太麻烦了，每次都要写一个类继承Observable,而且要要调用里面的方法都要写一段像
```
synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInvalidated();
            }
        }
```
这样的代码，还有一个局限性就是传参，都要在notify里写一遍，所以势必要封装一下。
- 不用每次写一个类继承Observable
- 不用在调用Observer里的方法时都写一个notify方法

    顺着这个思路就是要 **抽象类似mObservers.get(i).onChanged();的方法。**
于是便写了一个接口,拿到了observer想干什么就干什么。
```
public interface Dispatcher<T> {
    void call(T t);
}
```
SuperObservale类诞生了：
```
public class SuperObservable<T> extends Observable<T> {

    public void notifyMethod(Dispatcher<T> dispatcher){

        synchronized (mObservers){
            for (int i = mObservers.size()-1; i >= 0; i--) {
                T t = mObservers.get(i);
                    if(dispatcher!=null){
                        dispatcher.call(t);
                    }
                }
            }
        }
    }
}
```
这样只要在disatcher里写好要调用的方法，就可以notify T(观察者)中的任何方法了。每次只要new SuperObservable<T>便创建了一个被观察者，简单了不少。
考虑到有一个项目中会用到多次观察者模式，便写了一个SuperObservable的管理类：
```
package com.nevermore.superobservable.ob;

import java.util.HashMap;

/**
 * 
 * author XuNeverMore
 * create on 2017/6/15 0015
 * github https://github.com/XuNeverMore
 */

public class SuperObservableManager {

    private static SuperObservableManager instance;

    private HashMap<Class,SuperObservable> mObservables;

    private SuperObservableManager() {
        mObservables = new HashMap<>();
    }

    public static SuperObservableManager getInstance(){
        if(instance==null){
            synchronized (SuperObservableManager.class){
                if(instance==null){
                    instance = new SuperObservableManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加被观察者
     * @param observableName
     * @param superObservable
     * @param <Observer>
     *  利用泛型约束被观察者superObservable:
     *  让其只受Observer类型的观察者观察
     */
    public <Observer> void  addObservable(Class<Observer> observableName,SuperObservable<Observer> superObservable){
        if(mObservables.containsKey(observableName)){
            throw new IllegalStateException("已添加过"+observableName.getSimpleName()+"类型的被观察者！");
        }
        mObservables.put(observableName,superObservable);
    }


    /**
     * 获取被观察者
     * @param observableName
     * @return
     */
    public <Observer> SuperObservable<Observer> getObservable(Class<Observer> observableName){
        SuperObservable<Observer> superObservable = mObservables.get(observableName);

        if(superObservable==null){
            throw new IllegalStateException("未添加"+observableName.getSimpleName()+"类型的被观察者！");
        }
        return superObservable;
    }


    /**
     * 移除被观察者
     * @param observableName
     * @return
     */
    public SuperObservable removeObservable(Class observableName){
        return mObservables.remove(observableName);
    }

}
```
 
