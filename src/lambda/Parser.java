package lambda;

/**
 * 文字列を式に変換します。 式の文法は以下のとおりです。
 * 
 * <pre>
 * <code>
 * 式     = 因子 { 因子 }
 * 
 * 因子   = 変数
 *        | ラムダ
 *        | '(' 式 ')'
 * 
 * ラムダ = 変数 '.' 式
 *        | 'λ' 変数 { 変数 } '.' 式
 *        | '\' 変数 { 変数 } '.' 式
 * </code>
 * </pre>
 * 
 * @author saka1029
 */
public class Parser {

    /**
     * 文字列の終端を表す定数です。
     */
    static final int EOS = -1;

    /**
     * パース対象となる文字列です。
     */
    final String source;

    /**
     * コンテキストです。
     */
    final ParseContext context = new ParseContext();

    /**
     * パース対象文字列の次の読み込み位置を保持するインデックスです。
     */
    int index = 0;

    /**
     * パース対象文字列の内、現在読み込んでいる文字を保持します。
     */
    int ch = ' ';

    /**
     * コンストラクタです。
     * 
     * @param source
     *            パースする対象の文字列を指定します。
     */
    private Parser(String source) {
        this.source = source;
    }

    /**
     * 文字列を式に変換します。
     * 
     * @param s
     *            変換対象文字列を指定します。
     * @return 変換結果の式を返します。
     */
    public static Term parse(String s) {
        return new Parser(s).parse();
    }

    /**
     * 次の文字を取得して返します。 返す結果はフィールドchにも代入します。
     * 
     * @return 次に解析すべき文字を返します。 文字列の終端に達した場合はEOSを返します。
     */
    int get() {
        if (index >= source.length())
            return ch = EOS;
        return ch = source.charAt(index++);
    }

    /**
     * 変数名を構成する文字であるかどうかを調べます。
     * 
     * @param ch
     *            調べる文字を指定します。
     * @return 変数名を構成する文字である場合trueを返します。
     */
    public static boolean isVariableChar(int ch) {
        switch (ch) {
        // case '$':
        case 'λ': case '\\':
            return false;
        case '+': case '-': case '*': case '/':
        case '%': case '^': case '=':
        case '|': case '&': case '!':
            return true;
        default:
            return Character.isJavaIdentifierPart((char) ch);
        }
    }

    /**
     * 空白を読み飛ばします。
     */
    void skipSpaces() {
        while (Character.isWhitespace((char) ch))
            get();
    }

    /**
     * 変数を読み込みます。
     * 
     * @return 変数名を文字列として返します。
     */
    String parseVariable() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        get();
        while (isVariableChar(ch)) {
            sb.append((char) ch);
            get();
        }
        return sb.toString();
    }

    /**
     * 変数またはラムダをパースします。 変数の後に'.'が続く場合ラムダと解釈します。
     * 
     * @return 変数またはラムダ式を返します。
     */
    Term parseVariableOrLambda() {
        String name = parseVariable();
        skipSpaces();
        if (ch == '.') {
            get();
            Lambda lambda = new Lambda(name);
            try (Restorable r = context.put(name, lambda)) {
                lambda.setBody(parseTerm());
                return lambda;
            }
        }
        Lambda lambda = context.get(name);
        if (lambda != null)
            return new BoundVariable(lambda);
        else if (name.startsWith("$"))
            throw new RuntimeException("Free variable must not start with '$'");
        else
            return UnboundVariable.of(name);
    }

    /**
     * `λ` または `\`で始まるラムダ式をパースします。
     */
    Term parseLambda() {
        skipSpaces();
        if (ch == '.') {
            get();
            return parseTerm();
        }
        if (!isVariableChar(ch))
            throw new RuntimeException("Variable expected");
        String name = parseVariable();
        skipSpaces();
        Lambda lambda = new Lambda(name);
        try (Restorable r = context.put(name, lambda)) {
            lambda.setBody(parseLambda());
            return lambda;
        }
    }

    /**
     * 因子をパースします。
     * 
     * @return
     */
    Term parseFactor() {
        skipSpaces();
        switch (ch) {
        case '(':
            get();
            Term t = parseTerm();
            skipSpaces();
            if (ch != ')')
                throw new RuntimeException("')' expected");
            get();
            return t;
        case 'λ':
        case '\\':
            get();
            skipSpaces();
            if (ch == '.')
                throw new RuntimeException("Variables missing");
            return parseLambda();
        default:
            if (isVariableChar(ch))
                return parseVariableOrLambda();
            throw new RuntimeException("Unknown character: " + (char) ch);
        }
    }

    /**
     * {@link Term}をパースします。
     * 
     * @return
     */
    Term parseTerm() {
        Term t = parseFactor();
        while (true) {
            skipSpaces();
            if (ch != '(' && ch != 'λ' && ch != '\\' && !isVariableChar(ch))
                break;
            t = new Application(t, parseFactor());
        }
        return t;
    }

    /**
     * 文字列としての式をパースます。
     * 
     * @return パースした結果を返します。
     * @throws RuntimeException 構文エラーを検出したときにスローします。
     */
    public Term parse() {
        Term r = parseTerm();
        if (ch != EOS)
            throw new RuntimeException("Extra string: " + source.substring(index - 1));
        return r;
    }
}
