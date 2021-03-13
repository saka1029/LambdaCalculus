package lambda;

import java.util.Collections;
import java.util.Map;

public abstract class Expression {

    abstract void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb);

    public String toNormalizedString() {
        StringBuilder sb = new StringBuilder();
        toNormalizedString(null, new IntHolder(), sb);
        return sb.toString();
    }

    abstract Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context);

    public Expression reduce(Map<String, Expression> context) {
        return reduce(null, context);
    }

    public Expression reduce() {
        return reduce(Collections.emptyMap());
    }
}
