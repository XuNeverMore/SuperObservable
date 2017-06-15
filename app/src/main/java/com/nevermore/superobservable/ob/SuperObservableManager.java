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
        return mObservables.get(observableName);
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
