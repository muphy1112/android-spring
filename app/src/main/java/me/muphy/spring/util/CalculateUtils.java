package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.context.ContextHolder;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.util.Stack;

/**
 * 将字符串转计算公式
 * Object r1 = evaluate("function f(){var x = 5; y = 455; var m = {'x':x,'y':y}; return x}; var i = f();"); // Undefined
 * Object r2 = evaluate("function f(){var x = 5; y = 455; var m = {'x':x,'y':y}; return x}; var i = f(); i;"); // Integer:5
 * Object r3 = evaluate("var f = function(){var x = 5; var m = {'x':x,'y':55}; return m}; var i = f();", "i"); // NativeObject:{'x':x,'y':y}
 * Object r4 = evaluate("var f = function(v){var x = 5; var m = {'x':x,'y':v}; return m;};", "f", 1); // NativeObject:{'x':x,'y':y}
 * Object r5 = evaluate("var f = function(v){var x = 5; var m = {'x':x,'y':v}; return x + v;};", "f", 1); // Double:5
 *
 * @author: 若非
 * @date: 2021/9/9 20:43
 */
public class CalculateUtils {

    /**
     * 将字符串转计算公式 {@code evaluate(7+ +55 + +5 * (2-5)) }
     *
     * @param js
     * @return: 计算结果
     * @author: 若非
     * @date: 2021/9/9 20:43
     */
    public static <T> T evaluate(String js) {
        Context ctx = ContextHolder.getContext().getBean(Context.class);
        Scriptable scope = ctx.initStandardObjects();    //初始化本地对象
        T result = null;
        try {
            Object o = ctx.evaluateString(scope, js, "", 1, null);
            if (o != Scriptable.NOT_FOUND && !(o instanceof Undefined)) {
                result = (T) o;//执行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行JS代码
     *
     * @param js
     * @param resultName scope中获取返回参数名称
     * @return: js对象 计算结果
     * @author: 若非
     * @date: 2021/9/9 20:43
     */
    public static <T> T evaluate(String js, String resultName) {
        Context ctx = ContextHolder.getContext().getBean(Context.class);
        Scriptable scope = ctx.initStandardObjects();    //初始化本地对象
        T result = null;
        try {
            ctx.evaluateString(scope, js, "", 1, null);//执行
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object o = scope.get(resultName, scope);
        if (o != Scriptable.NOT_FOUND && !(o instanceof Undefined)) {
            result = (T) o;//执行
        }
        return result;
    }

    /**
     * 执行JS代码
     *
     * @param js             js代码
     * @param functionName   js方法名称
     * @param functionParams js方法参数
     * @return js对象
     * @author: 若非
     * @date: 2021/9/9 20:43
     */
    public static <T> T evaluate(String js, String functionName, Object... functionParams) {
        Context ctx = ContextHolder.getContext().getBean(Context.class);
        ctx.setOptimizationLevel(-1);
        T result = null;
        try {
            Scriptable scope = ctx.initStandardObjects();
            ctx.evaluateString(scope, js, null, 1, null);
            Function function = (Function) scope.get(functionName, scope);
            Object o = function.call(ctx, scope, scope, functionParams);
            if (o != Scriptable.NOT_FOUND && !(o instanceof Undefined)) {
                result = (T) o;//执行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加减乘除计算 结果使用包装类方便取值
     *
     * @param expression
     * @return
     * @author: 若非
     * @date: 2021/9/9 20:43
     * @see this#evaluate
     * @deprecated {@code this.evaluate}
     */
    @Remind(deprecated = true, notice = "请使用evaluate方法来进行表达式计算，这里只支持+-*/")
    public static Double calculationResult(String expression) {
        char[] chars = expression.toCharArray();
        Stack<String> stack = new Stack<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '+':
                case '-':
                    stack.push(stringBuilder.toString());
                    stack.push(String.valueOf(calculationResult(stack)));
                    stack.push(chars[i] + "");
                    stringBuilder = new StringBuilder();
                    break;
                case '*':
                case '/':
                    stack.push(stringBuilder.toString());
                    stack.push(chars[i] + "");
                    stringBuilder = new StringBuilder();
                    break;
                default:
                    stringBuilder.append(chars[i]);
            }
        }
        stack.push(stringBuilder.toString());
        Double v = calculationResult(stack);
        return v;
    }

    /**
     * 加减乘除计算 使用包装类方便取值
     *
     * @param stack
     * @return 执行结果
     * @author: 若非
     * @date: 2021/9/9 20:43
     * @see this#evaluate
     * @deprecated {@code this.evaluate}
     */
    private static Double calculationResult(Stack<String> stack) {
        double res = Double.parseDouble(stack.pop());
        while (!stack.empty()) {
            String opt = stack.pop();
            double second = Double.parseDouble(stack.pop());
            res = calculation(res, second, opt.charAt(0));
        }
        return res;
    }

    /**
     * 加减乘除计算 使用包装类方便取值
     *
     * @param res
     * @param x
     * @param opt
     * @return 执行结果
     * @author: 若非
     * @date: 2021/9/9 20:43
     * @see this#evaluate
     * @deprecated {@code this.evaluate}
     */
    private static Double calculation(double res, double x, char opt) {
        switch (opt) {
            case '+':
                return res + x;
            case '-':
                return x - res;
            case '*':
                return res * x;
            case '/':
                if (x < 0.0000000001f) {
                    return Double.MAX_VALUE;
                }
                return x / res;
            default:
                return res;
        }
    }
}
