package com.sloth.functions.mvp;

import android.content.Context;

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
public class RYBaseCase<V extends RYBaseView> {
    protected Context context;
    protected V mView;
    private final Map<String, RYBaseModel> models = new HashMap<>();

    public RYBaseCase() {
        initPreModels();
    }

    public RYBaseCase(V v) {
        this.mView = v;
        initPreModels();
    }

    public RYBaseCase(Context context, V v) {
        this.context = context;
        this.mView = v;
        initPreModels();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setView(V mView) {
        this.mView = mView;
    }

    private void initPreModels() {
        Set<RYBaseModel> preModels = new HashSet<>();
        prepareModels(preModels);
        if(!preModels.isEmpty()){
            for(RYBaseModel item: preModels){
                item.setContext(context);
                models.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    /**
     * 用户手动预置入的model实例，可以直接使用，如果未初始化，会由反射自动生成一个实例
     * @return
     */
    protected void prepareModels(Set<RYBaseModel> list){ }

    /**
     * 外部置入model
     */
    public void setModelsFromOutside(Set<RYBaseModel> outside){
        if(!outside.isEmpty()){
            for(RYBaseModel item: outside){
                item.setContext(context);
                models.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    public <M extends RYBaseModel> M getModel(Class<M> clz){
        String className = clz.getSimpleName();

        if(models.get(className) != null){
            return (M) models.get(className);
        }

        LogUtils.d("halo", "可以手动置入" + className + "，优化执行效率!");

        M ins = ReflectUtils.reflect(clz).get();
        ins.setContext(context);
        models.put(className, ins);
        return ins;
    }

    /**
     * 有些需要直接执行的事件流
     * 由Case借 Model的生命周期管理功能来执行
     * 达到Activity销毁时，任务自动终止的效果
     */
    public void execute(Disposable disposable){
        getModel(DefaultModel.class).execute(disposable);
    }

    public void destroy(){
        if(models.size() > 0){
            for(RYBaseModel model: models.values()){
                model.destroy();
            }
        }
        models.clear();
    }

}
