package lambda;

import java.util.HashMap;
import java.util.Map;

public class FreeVariable extends Variable {

    static final Map<String, FreeVariable> all = new HashMap<>();

    private FreeVariable(String name) {
        super(name);
    }

    public static FreeVariable of(String name) {
        return all.computeIfAbsent(name, k -> new FreeVariable(k));
    }

    @Override
    void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
        sb.append(name);
    }

    @Override
    public Expression reduce(Bind<BoundVariable, Expression> bind,
        Map<String, Expression> context) {
        Expression subst = context.get(name);
        return subst != null ? subst.reduce(context) : this;
    }
}
