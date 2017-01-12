package lambda;

import java.util.IdentityHashMap;

/**
 * 正規化のコンテキストです。
 * 
 * @author saka1029
 */
public class NormalizeContext extends BindMap<Lambda, BoundVariable> {
    
    /**
     * コンストラクタです。
     */
    public NormalizeContext() {
        super(new IdentityHashMap<>());
    }

}
