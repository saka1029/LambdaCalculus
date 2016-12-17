package lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public interface Expression {

    static Expression of(String s) { return new Reader(s).read(); }
    static Variable variable(String s) { return Variable.of(s); }

    default Expression eval(Context context) {
        context.enter(this, context);
        Expression e = evalCore(context);
        context.leave(e);
        return e;
    }

    default Application apply(Expression... args) {
        if (args.length == 0)
            throw new IllegalArgumentException("no args");
        Expression e = this;
        for (Expression a : args)
            e = new Application(e, a);
        return (Application)e;
    }

    default Expression normalize() { return normalize(new Context()); }

    static Expression defineNative(Context c, String name, Native n) {
        return c.define(Variable.of(name), Native.of(name, n));
    }

    static Context defaultContext() {
        Expression TRUE = of("x.y.x");
        Expression FALSE = of("x.y.y");
        Context c = new Context();
        c.define("define", (arg0, ctx0) -> (Native) (arg1, ctx1) ->
            ctx1.define((Variable)arg0, arg1.eval(ctx1)));
        c.define("normalize", (arg, ctx) -> arg.normalize());
        c.define(Variable.of("true"), TRUE);
        c.define(Variable.of("false"), FALSE);
        c.define("and", "p.q.p q false");
        c.define("or", "p.q.p true q");
        c.define("not", "p.p false true");
        c.define("equals", (arg0, ctx0) -> (Native) (arg1, ctx1) ->
            arg0.equals(arg1) ? TRUE : FALSE);
        c.define("trace", (arg, context) -> {
            boolean f = arg.equals(TRUE);
            context.trace = f; return arg;
        } );
        return c;
    }

    default Expression eval() { return eval(defaultContext()); }
    boolean eq(Object obj);

    Expression evalCore(Context context);
    Expression normalize(Context context);
    
    static void main(String[] args) throws IOException {
        Context context = defaultContext();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("% ");
                String line = reader.readLine();
                line = line.replaceFirst("#.*$", "").trim();
                if (line.length() <= 0) break;
                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;
                try {
                    Expression evaled = of(line).eval(context);
                    System.out.println(evaled);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
