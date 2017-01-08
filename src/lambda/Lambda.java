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
        // bodyを簡約して自分自身を新しいLambdaに置換する。
        Lambda lambda = new Lambda(name);
        try (Restorable r = context.bound.put(this, new BoundVariable(lambda))) {
            lambda.body = body.reduce(context);
        }
        // η-変換可能であれば変換する。
        Term e = lambda.etaConversion();
        if (e != null) {
            context.enter("η", lambda);
            Term reduced = e.reduce(context);
            context.exit("η", reduced);
            return reduced;
        }
        return lambda;
    }
    
    @Override
    public Term apply(Term argument, Context context) {
        // bodyがこのラムダで定義されている束縛変数を含まないのであれば、
        // 引数の簡約を行わず、束縛自身も行わない。
        // この2行があることで以下の再帰呼び出しが実行できるようになる。
        // reduce("define fact (n.ifthenelse (iszero n) 1 (* n (fact (pred n))))", c);
        if (!body.containsBoundVariable(this))
            return body.reduce(context);
        Term result = null;
        // 引数を簡約して束縛し、bodyを評価する。
        try (Restorable r = context.bound.put(this, argument.reduce(context))) {
            context.enter("β", this);
            return result = body.reduce(context);
        } finally {
            context.exit("β", result);
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
        return LambdaCalculus.TO_STRING_DOT ?
            toStringDot() :
            toStringLambda();
    }

}
