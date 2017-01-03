package lambda;

import java.util.IdentityHashMap;

public class NormalizeContext extends BindMap<Lambda, BoundVariable> {
    
    public NormalizeContext() {
        super(new IdentityHashMap<>());
    }

}
