package lambda;

public interface Applicable extends Expression {

    Expression apply(Expression argument, Context context);

}
