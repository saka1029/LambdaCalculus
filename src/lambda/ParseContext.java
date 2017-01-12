package lambda;

import java.util.HashMap;

/**
 * {@link lambda.Parser}で使用するコンテキストです。
 * 
 * @author saka1029
 *
 */
public class ParseContext extends BindMap<String, Lambda> {
    
    /**
     * コンストラクタです。
     * ベースとなるマップとしてHashMapを使います。
     */
    ParseContext() {
        super(new HashMap<>());
    }

}
