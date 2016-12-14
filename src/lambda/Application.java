package lambda;

public class Application extends Expression {
    
    final Expression head;
    final Expression tail;
    
    public Application(Expression head, Expression tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public boolean eq(Object obj) {
        if (!(obj instanceof Application))
            return false;
        Application o = (Application)obj;
        return o.head.eq(head) && o.tail.eq(tail);
    }
    
    @Override
    public Expression evalCore(Context context) {
        Expression function = head.eval(context);
        Expression argument = tail.eval(context);
        if (function instanceof Applicable)
            return ((Applicable)function).apply(argument, context);
        else
            return new Application(function, argument);
    }
    
    @Override
    public Expression normalize(NormalizeContext context) {
        return new Application(head.normalize(context), tail.normalize(context));
    }
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean headIsLambda = head instanceof Lambda;
        boolean tailIsApplication = tail instanceof Application;
        if (headIsLambda) sb.append("(");
        sb.append(head);
        if (headIsLambda) sb.append(")");
        if (!headIsLambda && !tailIsApplication) sb.append(" ");
        if (tailIsApplication) sb.append("(");
        sb.append(tail);
        if (tailIsApplication) sb.append(")");
        return sb.toString();
    }

}
