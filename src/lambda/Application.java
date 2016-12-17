package lambda;

public class Application implements Term {
    
    final Term head;
    final Term tail;
    
    public Application(Term head, Term tail) {
        this.head = head;
        this.tail = tail;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Term))
            return false;
        Term o = (Term)obj;
        return o.normalize().eq(normalize());
    }

    @Override
    public boolean eq(Object obj) {
        if (!(obj instanceof Application))
            return false;
        Application o = (Application)obj;
        return o.head.eq(head) && o.tail.eq(tail);
    }
    
    @Override
    public Term evalCore(Context context) {
        Term function = head.eval(context);
        Term argument = tail.eval(context);
        if (function instanceof Applicable)
            return ((Applicable)function).apply(argument, context);
        else
            return new Application(function, argument);
    }
    
    @Override
    public Term normalize(Context context) {
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
