package lambda;

public class BoundVariable implements Variable {

    final Lambda lambda;
    
    BoundVariable(Lambda lambda) {
        if (lambda == null) throw new IllegalArgumentException("lambda is null");
        this.lambda = lambda;
    }
 
    @Override
    public Term reduce(Context context) {
        return context.bound.getOrDefault(lambda, this);
    }
 
    @Override
    public Term normalize(NormalizeContext context) {
        return context.getOrDefault(lambda, this);
    }
    
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return lambda == this.lambda;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoundVariable))
            return false;
        BoundVariable o = (BoundVariable)obj;
        return o.lambda.name.equals(lambda.name);
    }

    @Override
    public String toString() {
        return lambda.name;
    }
}
