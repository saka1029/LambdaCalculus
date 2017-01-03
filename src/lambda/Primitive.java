package lambda;

public interface Primitive extends Term, Applicable {

    Term apply(Term argument, Context context);
    
    @Override
    default Term reduce(Context context) {
        return this;
    }
    
    @Override
    default Term normalize(NormalizeContext context) {
        return this;
    }
    
    @Override
    default boolean containsBoundVariable(Lambda lambda) {
        return false;
    }
}
