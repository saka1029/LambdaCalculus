package lambda;

public class Lambda implements Term, Applicable {

    final String name;
    Term body;
    
    Lambda(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    private Term etaConversion() {
        if (!(body instanceof Application)) return null;
        Application app = (Application)body;
        if (!(app.tail instanceof BoundVariable)) return null;
        BoundVariable v = (BoundVariable)app.tail;
        if (v.lambda != this) return null;
        if (app.head.containsBoundVariable(this)) return null;
        return app.head;
    }

    @Override
    public Term reduce(Context context) {
//        Term eta = etaConversion();
//        if (eta != null) return eta.reduce(context);
        Lambda lambda = new Lambda(name);
        try (Restorable r = context.bound.put(this, new BoundVariable(lambda))) {
            lambda.body = body.reduce(context);
        }
        Term e = lambda.etaConversion();
        if (e != null) {
            context.enter("η", this);
            Term reduced = e.reduce(context);
            context.exit(reduced);
            return reduced;
        }
        return lambda;
    }
    
    @Override
    public Term apply(Term argument, Context context) {
        try (Restorable r = context.bound.put(this, argument.reduce(context))) {
            return body.reduce(context);
        }
    }
    
    @Override
    public Term normalize(NormalizeContext context) {
        String name = "$" + context.size();
        Lambda lambda = new Lambda(name);
        BoundVariable variable = new BoundVariable(lambda);
        try (Restorable r = context.put(this, variable)) {
            lambda.body = body.normalize(context);
            return lambda;
        }
    }
    
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return body.containsBoundVariable(lambda);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Lambda))
            return false;
        Lambda o = (Lambda)obj;
        return o.name.equals(name) && o.body.equals(body);
    }

    private void toStringBody(StringBuilder sb) {
        sb.append(name);
        if (body instanceof Lambda) {
            sb.append(" ");
            ((Lambda)body).toStringBody(sb);
        } else
            sb.append(".").append(body);
    }

    public String toStringLambda() {
        StringBuilder sb = new StringBuilder();
        sb.append("λ");
        toStringBody(sb);
        return sb.toString();
    }
    
    public String toStringDot() {
        return name + "." + body;
    }
 
    @Override
    public String toString() {
        return toStringDot();
    }

}
