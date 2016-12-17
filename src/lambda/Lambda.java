package lambda;

public class Lambda implements Applicable {
    
    final Variable variable;
    final Expression body;
  
    public Lambda(Variable variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Expression))
            return false;
        Expression o = (Expression)obj;
        return o.normalize().eq(normalize());
    }
    
    @Override
    public boolean eq(Object obj) {
        if (!(obj instanceof Lambda))
            return false;
        Lambda o = (Lambda)obj;
        return o.variable.eq(variable) && o.body.eq(body);
    }

    @Override
    public Expression evalCore(Context context) {
        try (Restorable r = context.put(variable, variable)) {
            Expression e = body.eval(context);
            return new Lambda(variable, e);
        }
    }
    
    @Override
    public Expression apply(Expression argument, Context context) {
        try (Restorable r = context.put(variable, argument)) {
            return body.eval(context);
        }
    }
    
    @Override
    public Expression normalize(Context context) {
        Variable s = context.normalizedVariable();
        try (Restorable r = context.put(variable)) {
            Expression e = body.normalize(context);
            return new Lambda(s, e);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(variable).append(".").append(body);
        return sb.toString();
    }
}
