package lambda;

import java.util.Map;
import java.util.Objects;

public class Application extends Expression {

    public final Expression function, argument;

    public Application(Expression function, Expression argument) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(argument, "argument");
        this.function = function;
        this.argument = argument;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (function instanceof Lambda)
            sb.append("(");
        sb.append(function);
        if (function instanceof Lambda)
            sb.append(")");
        sb.append(" ");
        if (!(argument instanceof Variable))
            sb.append("(");
        sb.append(argument);
        if (!(argument instanceof Variable))
            sb.append(")");
        return sb.toString();
    }

    @Override
    void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
        if (function instanceof Lambda)
            sb.append("(");
        function.toNormalizedString(bind, number, sb);
        if (function instanceof Lambda)
            sb.append(")");
        sb.append(" ");
        if (!(argument instanceof Variable))
            sb.append("(");
        argument.toNormalizedString(bind, number, sb);
        if (!(argument instanceof Variable))
            sb.append(")");
    }

    @Override
    public Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context) {
        Expression newFunction = function.reduce(bind, context);
        Expression newArgument = argument.reduce(bind, context);
        if (newFunction instanceof Lambda) {
            Lambda lambda = (Lambda) newFunction;
            return lambda.body.reduce(Bind.bind(bind, lambda.variable, newArgument), context);
        } else if (newFunction instanceof Command) {
            Command command = (Command) newFunction;
            return command.reduce(bind, context, newArgument);
        }
        return new Application(newFunction, newArgument);
    }
}
