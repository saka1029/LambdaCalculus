package lambda;

public interface Term {

    Term reduce(Context context);

    Term normalize(NormalizeContext context);

    boolean containsBoundVariable(Lambda lambda);
    
    default Term normalize() {
        return normalize(new NormalizeContext());
    }
    
}
