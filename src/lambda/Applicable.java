package lambda;

public abstract class Applicable extends Expression {

    abstract Expression apply(Expression argument, Context context);

}
