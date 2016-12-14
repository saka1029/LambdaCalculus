package lambda;

public interface Applicable {

    Expression apply(Expression argument, Context context);

}
