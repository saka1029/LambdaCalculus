package lambda;

public class Context {
    
    final Context previous;
    final Variable variable;
    final Expression expression;
    final Tracer tracer;
    
    static class Tracer {
        public int level = 0;
    }
    
    private Context() {
        previous = null;
        variable = null;
        expression = null;
        tracer = new Tracer();
    }
    
    private Context(Context parent, Variable variable, Expression expression) {
        this.previous = parent;
        this.variable = variable;
        this.expression = expression;
        this.tracer = parent.tracer;
    }

    public static Context root() { return new Context(); }
    
    public Expression get(Variable v) {
        if (previous == null) return null;
        if (v.equals(variable)) return expression;
        return previous.get(v);
    }
    
    public Context put(Variable v, Expression e) {
        return new Context(this, v, e);
    }
    
    public Context put(String v, String e) {
        return put((Variable)Expression.of(v), Expression.of(e));
    }
    
    String indent() {
        return tracer.level == 0 ? ""
            : String.format("%" + tracer.level + "s", "");
    }
    
    void enter(Expression e) {
        System.out.println(indent() + "> " + e + " : " + this);
        ++tracer.level;
    }
    
    void leave(Expression e) {
        --tracer.level;
        System.out.println(indent() + "< " + e);
    }
 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Context c = this; c.previous != null; c = c.previous)
            sb.append(c == this ? "" : ", ")
                .append(c.variable)
                .append("=")
                .append(c.expression);
        sb.append("}");
        return sb.toString();
    }

}
