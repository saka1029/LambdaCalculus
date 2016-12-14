package lambda;

public class Lambda extends Expression implements Applicable {
    
    final Variable variable;
    final Expression body;
  
    public Lambda(Variable variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }
    
    @Override
    public boolean eq(Object obj) {
        if (!(obj instanceof Lambda))
            return false;
        Lambda o = (Lambda)obj;
        return o.variable.eq(variable) && o.body.eq(body);
    }

    @Override
    Expression evalCore(Context context) {
        Context n = context.put(variable, variable);
        Expression e = body.eval(n);
        return new Lambda(variable, e);
    }
    
    @Override
    public Expression apply(Expression argument, Context context) {
        Context n = context.put(variable, argument);
        return body.eval(n);
    }
    
    @Override
    public Expression normalize(NormalizeContext context) {
        NormalizeContext n = context.put(variable);
        Expression e = body.normalize(n);
        return new Lambda(n.normalized, e);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(variable).append(".").append(body);
        return sb.toString();
    }
}
