package com.sloth.functions.mvp;

import android.content.Context;
import android.os.Bundle;

import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.ReflectUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 15:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RYBasePresenter<V extends RYBaseView> {
    protected Context mContext;
    protected V mView;
    private final Map<String, RYBaseCase> cases = new HashMap<>();

    public RYBasePresenter(V view) {
        this.mView = view;
        initPreCases();
    }

    public RYBasePresenter(Context context, V view) {
        this.mContext = context;
        this.mView = view;
        initPreCases();
    }

    private void initPreCases() {
        //初始化代理case，注入model
        initPreModels();
        //初始化自定义case
        Set<RYBaseCase> preCases = new HashSet<>();
        prepareCases(preCases);
        if(!preCases.isEmpty()){
            for(RYBaseCase item: preCases){
                item.setContext(mContext);
                item.setView(mView);
                cases.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    private void initPreModels() {
        Set<RYBaseModel> preModels = new HashSet<>();
        prepareModels(preModels);

        if(!preModels.isEmpty()){
            //如果model不为空说明presenter直接使用了model，手动新建一个代理DefaultCase
            DefaultCase defaultCase = new DefaultCase(mContext, mView);
            cases.put(defaultCase.getClass().getSimpleName(), defaultCase);
            defaultCase.setModelsFromOutside(preModels);
        }
    }

    protected void prepareCases(Set<RYBaseCase> list){ }

    protected void prepareModels(Set<RYBaseModel> list){ }

    protected  <C extends RYBaseCase> C getCase(Class<C> clz){
        String className = clz.getSimpleName();

        if(cases.get(className) != null){
            return (C) cases.get(className);
        }

        LogUtils.d("halo","可以手动置入" + className + "，优化执行效率!");

        C ins = ReflectUtils.reflect(clz).get();
        ins.setContext(mContext);
        ins.setView(mView);
        cases.put(className, ins);
        return ins;
    }

    /**
     * 原则上presenter应当通过case分散业务逻辑到不同用例中
     * 但某些场景下没必要新建Case
     * 例如，presenter直接访问model进行请求时，实际是通过DefaultCase来代理请求的
     * @param clz
     * @param <M>
     * @return
     */
    protected <M extends RYBaseModel> M getModel(Class<M> clz){
        return (M) getCase(DefaultCase.class).getModel(clz);
    }

    /**
     * 不涉及case和model的任务，直接由DefaultCase -> DefaultModel代理 来处理
     * @param disposable
     */
    protected void execute(Disposable disposable){
        getCase(DefaultCase.class).execute(disposable);
    }

    protected void onRestoreInstance(Bundle bundle){ }

    protected void onSaveInstanceRestored(Bundle bundle){ }

    public void destroy(){
        if(cases.size() > 0){
            for(RYBaseCase caseItem: cases.values()){
                caseItem.destroy();
            }
        }
        cases.clear();
    }

}
