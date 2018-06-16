package ryuunoakaihitomi.xposed.xposeddisablehooksreflectionkiller;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Main Hook
 * Created by ZQY on 2018/2/21.
 */

public class X implements IXposedHookLoadPackage {

    private static final String TAG = "XposedHookProtector";

    private static boolean isHookComing;


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {

        //clazz init
        ClassLoader classLoader = lpparam.classLoader;
        Class<?> classLoaderClazz = XposedHelpers.findClass("java.lang.ClassLoader", classLoader);
        Class<?> classClazz = XposedHelpers.findClass("java.lang.Class", classLoader);
        //Class<?> fieldClazz = XposedHelpers.findClass("java.lang.reflect.Field", classLoader);

        //Lintener:de.robv.android.xposed.XposedBridge
        XposedHelpers.findAndHookMethod(classLoaderClazz, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ("de.robv.android.xposed.XposedBridge".equals(param.args[0])) {
                    isHookComing = true;
                    //Hide
                    param.args[0] = null;
                    Log.d(TAG, ".loadClass(\"de.robv.android.xposed.XposedBridge\")");
                }
            }
        });

        //Change param:disableHooks
        XposedHelpers.findAndHookMethod(classClazz, "getDeclaredField", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                if ("disableHooks".equals(param.args[0])) {
                    Log.d(TAG, "\"disableHooks\" found.");
                    if (isHookComing) {
                        param.args[0] = null;
                        isHookComing = false;
                        Log.w(TAG, lpparam.packageName + " is trying disabling xposed hook.");
                    }
                }
            }
        });

        //Hide
        XposedHelpers.findAndHookMethod(classClazz, "getDeclaredFields", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (XposedBridge.class.equals(param.thisObject)) {
                    Log.d(TAG, lpparam.packageName + " invoke XposedBridge.class.getDeclaredFields()");
                    param.setResult(null);
                }
            }
        });
    }
}
