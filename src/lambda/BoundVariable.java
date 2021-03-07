package lambda;

import java.util.Map;

public class BoundVariable extends Variable {

    public BoundVariable(String name) {
        super(name);
    }

    @Override
    void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
        sb.append(Bind.get(bind, this));
    }

    @Override
    public Expression reduce(Bind<BoundVariable, Expression> bind,
        Map<String, Expression> context) {
        Expression expression = Bind.get(bind, this);
        if (expression != null)
            return expression;
        return this;
    }
}
