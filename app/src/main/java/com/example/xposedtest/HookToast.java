package com.example.xposedtest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @Info
 * @Auth Bello
 * @Time 2019/5/24 22:53
 * @Ver
 */
public class HookToast implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {

        // 拦截测试修改private变量的内容
        if (param.packageName.equals("com.example.xposedtest")){
            final Class clz = param.classLoader.loadClass("com.example.xposedtest.MainActivity");
            XposedHelpers.findAndHookMethod("com.example.xposedtest.MainActivity", param.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    // 取出变量
                    Field field = clz.getDeclaredField("btn");
                    // 变量是private，所以需要accessible
                    field.setAccessible(true);
                    Button btn = (Button) field.get(param.thisObject);
                    btn.setText("HOOKED BUTTON");

                }
            });
        }

        // 拦截测试点击响应
        if (param.packageName.equals("com.example.xposedtest")){
            Class clz = param.classLoader.loadClass("com.example.xposedtest.MainActivity");

            XposedHelpers.findAndHookMethod(clz, "showMsg", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("HaHa~~~~~~~~");
                }


                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult("Now, You are Hooked!");
                }
            });

        }

        // 练手1：去除酷我音乐的banner
        if (param.packageName.equals("cn.kuwo.kwplayerhd")){

            XposedBridge.log("kuwo play: Loaded~~~~");

            XposedHelpers.findAndHookMethod(View.class, "setLayoutParams", ViewGroup.LayoutParams.class, new XC_MethodHook() {

                @SuppressLint("ResourceType")
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    View view = (View) param.thisObject;

                    // 匹配banner控件中的资源id
                    if (view.getId() == 2131362371 || view.getId() == 2131362372 || view.getId() == 2131362373 || view.getId() == 2131361809 ){
                        view.setVisibility(View.GONE);
                        XposedBridge.log("view id=> " + view.getId() + " is hooked.");
                    }

                }
            });
        }

//      // 练手2：去除mumu模拟器桌面下面的推荐广告
        if (param.packageName.equals("com.mumu.launcher")){
            XposedBridge.log("mumu launcher loading~~~");

            XposedHelpers.findAndHookMethod(View.class, "setLayoutParams", ViewGroup.LayoutParams.class, new XC_MethodHook() {

                @SuppressLint("ResourceType")
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    View view = (View) param.thisObject;

                    if (view.getId() == 2131755050 || view.getId() == 2131755089){
                        view.setVisibility(View.GONE);
                        XposedBridge.log("mumu launcher hot seat is hooking~~~~~~");
                    }

                }
            });
        }


        // 练手3：买房记修改初始化的3000金额和100个出租屋
        if (param.packageName.equals("com.zf.dsmfj")){
            final Class cla = XposedHelpers.findClass("com.zf.dsmfj.Util", param.classLoader);

            XposedBridge.log("dsmfj loading~~~~~~~~");

            XposedHelpers.findAndHookMethod(cla, "Encrypt", String.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    // 修改初始化的值，要在hook之前修改
                    XposedBridge.log("~~~1~~ " + param.args[0]);
                    if (param.args[0].equals("3000")){
                        param.args[0] = "800000";
                    }
                    if (param.args[0].equals("100")){
                        param.args[0] = "1000";
                    }
                }

            });

        }

    }
}
