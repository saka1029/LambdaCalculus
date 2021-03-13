package lambda;

import java.util.Map;

public abstract class Command extends Expression {

    abstract Expression reduce(Bind<BoundVariable, Expression> bind,
        Map<String, Expression> context, Expression argument);

    @Override
    Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context) {
        return this;
    }

    @Override
    void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
        StringBuilder sb) {
        sb.append(toString());
    }

    public static final Command DEFINE = new Command() {

        @Override
        public String toString() {
            return "define";
        }

        @Override
        Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context,
            Expression argument) {
            if (!(argument instanceof FreeVariable))
                throw new LambdaCalculusException("usage: define FREE_VARIABLE EXPRESSION");
            return new Command() {

                final String name = ((FreeVariable) argument).name;

                @Override
                public String toString() {
                    return "define-" + name;
                }

                @Override
                Expression reduce(Bind<BoundVariable, Expression> bind,
                    Map<String, Expression> context, Expression argument) {
                    context.put(name, argument);
                    return argument;
                }
            };
        }
    };

}
