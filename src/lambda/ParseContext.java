package lambda;

import java.util.HashMap;

public class ParseContext extends BindMap<String, Lambda> {
    
    ParseContext() {
        super(new HashMap<>());
    }

}
