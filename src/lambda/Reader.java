package lambda;

/**
 * Syntax:<br>
 * <pre>{@code
 * Expression = Factor { Factor }
 * Factor     = Variable
 *            | Lambda
 *            | '(' Expression ')'
 * Lambda     = Variable '.' Expression
 * }</pre>
 */
class Reader {

    static final int EOF = -1;
    final String s;
    int index = 0;
    int ch = ' ';
    
    static boolean isVariableStart(int ch) {
        switch (ch) {
        case '+': case '-': case '*': case '/':
        case '|': case '&': case '!':
        case '^': case '%':
            return true;
        default:
            return Character.isJavaIdentifierPart(ch);
        }
    }
    
    static boolean isVariablePart(int ch) {
        return isVariableStart(ch);
    }

    int get() {
        if (index >= s.length())
            return ch = EOF;
        return ch = s.charAt(index++);
    }

    void skipSpaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    Reader(String s) {
        this.s = s;
    }

    Expression readParen() {
        get();
        Expression e = readExpression();
        skipSpaces();
        if (ch != ')')
            throw new RuntimeException("')' expected");
        get();
        return e;
    }

    Variable readVariable() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        get();
        while (isVariablePart(ch)) {
            sb.append((char) ch);
            get();
        }
        return Variable.of(sb.toString());
    }

    Expression readLambda() {
        Variable v = readVariable();
        skipSpaces();
        if (ch == '.') {
            get();
            Expression e = readExpression();
            return new Lambda(v, e);
        }
        return v;
    }

    Expression readFactor() {
        skipSpaces();
        if (ch == '(')
            return readParen();
        else if (isVariableStart(ch))
            return readLambda();
        else
            throw new RuntimeException("Unknown char: " + (char) ch);
    }

    Expression readExpression() {
        Expression e = readFactor();
        skipSpaces();
        while (ch == '(' || isVariableStart(ch)) {
            e = new Application(e, readFactor());
            skipSpaces();
        }
        return e;
    }

    Expression read() {
        Expression e = readExpression();
        if (ch != EOF)
            throw new RuntimeException("Unread string: " + s.substring(index));
        return e;
    }
}
