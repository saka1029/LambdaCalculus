package lambda;

public class Application implements Term {
    
    final Term head, tail;
    
    Application(Term head, Term tail) {
        if (head == null) throw new IllegalArgumentException("head is null");
        if (tail == null) throw new IllegalArgumentException("tail is null");
        this.head = head;
        this.tail = tail;
    }
    
    @Override
    public Term reduce(Context context) {
        Term function = head.reduce(context);
        if (function instanceof Applicable) {
            context.enter("β", this);
            Term reduced = ((Applicable)function).apply(tail, context);
            context.exit(reduced);
            return reduced;
        } else
            return new Application(function, tail.reduce(context));
    }
    
    @Override
    public Term normalize(NormalizeContext context) {
        return new Application(head.normalize(context), tail.normalize(context));
    }
    
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return head.containsBoundVariable(lambda)
            || tail.containsBoundVariable(lambda);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Application))
            return false;
        Application o = (Application)obj;
        return o.head.equals(head) && o.tail.equals(tail);
    }
    
    /**
     * Lambdaの場合
     * Variable -> x.V -> x.V
     * Application -> x.U V
     * Lambda -> x.y.X
     * 
     * Applicationの場合
     * head =
     * Variable -> V T
     * Application -> A X T
     * Lambda -> x.B T
     * tail =
     * Variable -> H V
     * Application -> H (A X)
     * Lambda -> H x.B
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean headIsLambda = head instanceof Lambda;
        boolean tailIsApplication = tail instanceof Application;
        if (headIsLambda) sb.append("(");
        sb.append(head);
        if (headIsLambda) sb.append(")");
        sb.append(" ");
        if (tailIsApplication) sb.append("(");
        sb.append(tail);
        if (tailIsApplication) sb.append(")");
        return sb.toString();
    }

}
