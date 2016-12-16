package lambda;

public abstract class Expression {

    public Expression eval(Context context) {
        context.enter(this, context);
        Expression e = evalCore(context);
        context.leave(e);
        return e;
    }

    abstract Expression evalCore(Context context);
    public Expression eval() { return eval(Context.root()); }
    
    abstract Expression normalize(NormalizeContext context);
    public Expression normalize() { return normalize(NormalizeContext.root()); }

    public abstract boolean eq(Object obj);

    public boolean equals(Object obj) {
        if (!(obj instanceof Expression))
            return false;
        Expression o = (Expression)obj;
        return o.normalize().eq(normalize());
    }
    
    public static Expression of(String s) { return new Reader(s).read(); }
    public static Variable variable(String s) { return Variable.of(s); }

    public Application apply(Expression... args) {
        if (args.length == 0)
            throw new IllegalArgumentException("no args");
        Expression e = this;
        for (Expression a : args)
            e = new Application(e, a);
        return (Application)e;
    }
}
