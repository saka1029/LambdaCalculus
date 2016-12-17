package lambda;

public interface Applicable extends Term {

    Term apply(Term argument, Context context);

}
