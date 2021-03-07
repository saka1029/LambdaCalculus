package lambda;

import java.util.Map;

public class Lambda extends Expression {

    public final BoundVariable variable;
    public final Expression body;

    public Lambda(BoundVariable variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }

    @Override
    public String toString() {
        return "λ" + variable + "." + body;
    }

    @Override
    void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
        String name = "%" + number.value++;
        sb.append("λ").append(name).append(".");
        body.toNormalizedString(Bind.bind(bind, variable, name), number, sb);
    }

    @Override
    public Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context) {
        BoundVariable newVariable = new BoundVariable(variable.name);
        Expression newBody = body.reduce(Bind.bind(bind, variable, newVariable), context);
        return new Lambda(newVariable, newBody);
    }

}
