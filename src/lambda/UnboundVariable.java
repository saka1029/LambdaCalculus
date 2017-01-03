package lambda;

import java.util.HashMap;
import java.util.Map;

public class UnboundVariable implements Variable {

    static final Map<String, UnboundVariable> variables = new HashMap<>();
    
    final String name;
    
    private UnboundVariable(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }
    
    public static UnboundVariable of(String name) {
        return variables.computeIfAbsent(name, key -> new UnboundVariable(key));
    }
    
    @Override
    public Term reduce(Context context) {
        return context.unbound.getOrDefault(this, this);
    }
    
    @Override
    public Term normalize(NormalizeContext context) {
        return this;
    }
    
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
