package lambda;

public class Lambda implements Applicable {
    
    final Variable variable;
    final Term body;
  
    public Lambda(Variable variable, Term body) {
        this.variable = variable;
        this.body = body;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Term))
            return false;
        Term o = (Term)obj;
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
    public Term evalCore(Context context) {
        try (Restorable r = context.put(variable, variable)) {
            Term e = body.eval(context);
            return new Lambda(variable, e);
        }
    }
    
    @Override
    public Term apply(Term argument, Context context) {
        try (Restorable r = context.put(variable, argument)) {
            return body.eval(context);
        }
    }
    
    @Override
    public Term normalize(Context context) {
        Variable s = context.normalizedVariable();
        try (Restorable r = context.put(variable)) {
            Term e = body.normalize(context);
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
