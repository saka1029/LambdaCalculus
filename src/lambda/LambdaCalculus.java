package lambda;

public class LambdaCalculus {

    private LambdaCalculus() {
    }

    public static Expression parse(String source) {
        return new Object() {

            int[] codePoints = source.codePoints().toArray();
            int length = codePoints.length;
            int index = 0;
            int ch = ' ';

            boolean isVariableChar(int ch) {
                switch (ch) {
                case -1: case 'λ': case '(': case ')': case '.':
                    return false;
                default:
                    return !Character.isWhitespace(ch);
                }
            }

            int next() {
                return ch = index < length ? codePoints[index++] : -1;
            }

            void skipSpaces() {
                while (Character.isWhitespace(ch))
                    next();
            }

            Lambda parseLambda(Bind<String, BoundVariable> bind) {
                skipSpaces();
                if (!isVariableChar(ch))
                    throw new RuntimeException("variable expected");
                String name = parseVariableName();
                skipSpaces();
                BoundVariable variable = new BoundVariable(name);
                Bind<String, BoundVariable> newBind = Bind.bind(bind, name, variable);
                Expression body;
                if (ch == '.') {
                    next(); // skip '.'
                    body = parse(newBind);
                } else
                    body = parseLambda(newBind);
                return new Lambda(variable, body);
            }

            Expression parseParen(Bind<String, BoundVariable> bind) {
                skipSpaces();
                Expression e = parse(bind);
                skipSpaces();
                if (ch != ')')
                    throw new RuntimeException("')' expected");
                next(); // skip ')'
                return e;
            }

            String parseVariableName() {
                StringBuilder sb = new StringBuilder();
                for ( ; isVariableChar(ch); next())
                    sb.appendCodePoint(ch);
                return sb.toString();
            }

            Variable parseVariable(Bind<String, BoundVariable> bind) {
                String name = parseVariableName();
                BoundVariable variable = Bind.get(bind, name);
                return variable != null ? variable : FreeVariable.of(name);
            }

            Expression parseTerm(Bind<String, BoundVariable> bind) {
                skipSpaces();
                switch (ch) {
                case -1:
                    throw new RuntimeException("unexpected end of string");
                case 'λ':
                    next();  // skip 'λ'
                    return parseLambda(bind);
                case '(':
                    next(); // skip '('
                    return parseParen(bind);
                default:
                    if (!isVariableChar(ch))
                        throw new RuntimeException(new StringBuilder("unexpected char '")
                            .appendCodePoint(ch).append("'").toString());
                    return parseVariable(bind);
                }
            }

            Expression parse(Bind<String, BoundVariable> bind) {
                Expression term = parseTerm(bind);
                while (true) {
                    skipSpaces();
                    if (ch != 'λ' && ch != '(' && !isVariableChar(ch))
                        break;
                    term = new Application(term, parseTerm(bind));
                }
                return term;
            }

            Expression parse() {
                Expression expression = parse(null);
                if (ch != -1)
                    throw new RuntimeException("extra string '"
                        + new String(codePoints, index - 1, length - index + 1) + "'");
                return expression;
            }
        }.parse();
    }
}
