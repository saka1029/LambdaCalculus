package lambda;

public class Parser {

    static final int EOS = -1;
    final String source;
    final ParseContext context = new ParseContext();
    int index = 0;
    int ch = ' ';
    
    private Parser(String source) {
        this.source = source;
    }

    public static Term parse(String s) {
        return new Parser(s).parse();
    }
    
    int get() {
        if (index >= source.length())
            return ch = EOS;
        return ch = source.charAt(index++);
    }
    
    public static boolean isVariableChar(int ch) {
        switch (ch) {
        case '$': case 'λ': case '\\':
            return false;
        case '+': case '-': case '*': case '/':
        case '%': case '^': case '=':
        case '|': case '&': case '!':
            return true;
        default:
            return Character.isJavaIdentifierPart((char)ch);
        }
    }
    
    void skipSpaces() {
        while (Character.isWhitespace((char)ch))
            get();
    }

    String parseVariable() {
        StringBuilder sb = new StringBuilder();
        sb.append((char)ch);
        get();
        while (isVariableChar(ch)) {
            sb.append((char)ch);
            get();
        }
        return sb.toString();
    }
    
    Term parseVariableOrLambda() {
        String name = parseVariable();
        skipSpaces();
        if (ch == '.') {
            get();
            Lambda lambda = new Lambda(name);
            try (Restorable r = context.put(name, lambda)) {
                lambda.body = parseTerm();
                return lambda;
            }
        }
        Lambda lambda = context.get(name);
        return lambda != null ?
            new BoundVariable(lambda) :
            UnboundVariable.of(name);
    }
    
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
            lambda.body = parseLambda();
            return lambda;
        }
    }
    
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
        case 'λ': case '\\':
            get();
            skipSpaces();
            if (ch == '.')
                throw new RuntimeException("Variables missing");
            return parseLambda();
        default:
            if (isVariableChar(ch))
                return parseVariableOrLambda();
            throw new RuntimeException("Unknown character: " + (char)ch);
        }
    }
    
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

    public Term parse() {
        Term r = parseTerm();
        if (ch != EOS)
            throw new RuntimeException("Extra string: " + source.substring(index - 1));
        return r;
    }
}
