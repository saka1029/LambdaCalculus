package lambda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
                case -1: case 'λ': case '\\': case '(': case ')': case '.':
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
                    throw new LambdaCalculusException("variable expected");
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
                    throw new LambdaCalculusException("')' expected");
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
                    throw new LambdaCalculusException("unexpected end of string");
                case 'λ': case '\\':
                    next();  // skip 'λ' or '\\'
                    return parseLambda(bind);
                case '(':
                    next(); // skip '('
                    return parseParen(bind);
                default:
                    if (!isVariableChar(ch))
                        throw new LambdaCalculusException(new StringBuilder("unexpected char '")
                            .appendCodePoint(ch).append("'").toString());
                    return parseVariable(bind);
                }
            }

            Expression parse(Bind<String, BoundVariable> bind) {
                Expression term = parseTerm(bind);
                while (true) {
                    skipSpaces();
                    if (ch != 'λ' && ch != '\\' && ch != '(' && !isVariableChar(ch))
                        break;
                    term = new Application(term, parseTerm(bind));
                }
                return term;
            }

            Expression parse() {
                Expression expression = parse(null);
                if (ch != -1)
                    throw new LambdaCalculusException("extra string '"
                        + new String(codePoints, index - 1, length - index + 1) + "'");
                return expression;
            }
        }.parse();
    }

    public static class NullWriter extends Writer {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static class ConsumerWriter extends Writer {

        final Consumer<String> consumer;
        final StringBuilder sb = new StringBuilder();

        public ConsumerWriter(Consumer<String> consumer) {
            this.consumer = consumer;
        }

        void writeLine() {
            if (sb.length() <= 0)
                return;
            consumer.accept(sb.toString());
            sb.setLength(0);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            String line = new String(cbuf, off, len);
            sb.append(line.replaceAll("[\r\n]", ""));
            if (line.endsWith("\n") || line.endsWith("\r"))
                writeLine();
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static void repl(Reader reader, Writer writer,
        Map<String, Expression> context, boolean echo, boolean prompt) throws IOException {
        String promptStr = "> ";
        BufferedReader input = new BufferedReader(reader);
        PrintWriter output = new PrintWriter(writer, true);
        if (prompt) {
            output.write(promptStr);
            output.flush();
        }
        String line;
        while ((line = input.readLine()) != null) {
            if (echo)
                output.println(line);
            line = line.trim();
            if (line.isEmpty())
                continue;
            if (line.equals("exit"))
                break;
            try {
                Expression result = parse(line).reduce(context);
                output.println(result);
            } catch (LambdaCalculusException e) {
                output.println("Error: " + e.getMessage());
            }
            if (prompt) {
                output.write(promptStr);
                output.flush();
            }
        }
    }


    static void usage() {
        System.err.println("usage: java " + LambdaCalculus.class.getName()
            + " [-e] [-c CHARSET] [-l LOADFILE] [-o OUTFILE] [INFILE]");
        System.err.println("-e            Echo input");
        System.err.println("-c CHARSET    Specify file encoding");
        System.err.println("-l LOADFILE   Load file");
        System.err.println("-o OUTFILE    Output file");
        System.err.println("INFILE        Input file");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        Charset charset = Charset.defaultCharset();
        Reader reader = null;
        Writer writer = null;
        boolean echo = false;
        boolean prompt = false;
        int length = args.length;
        Map<String, Expression> context = new HashMap<>();
        context.put("define", Command.DEFINE);
        int i = 0;
        for (; i < length; ++i) {
            if (args[i].startsWith("-"))
                switch (args[i]) {
                case "-e":
                    echo = true;
                    break;
                case "-c":
                    charset = Charset.forName(args[++i]);
                    break;
                case "-l":
                    Reader load = new InputStreamReader(new FileInputStream(args[++i]), charset);
                    repl(load, new NullWriter(), context, false, false);
                    break;
                case "-o":
                    writer = new OutputStreamWriter(new FileOutputStream(args[++i]), charset);
                    break;
                default:
                    usage();
                }
            else if (reader == null)
                reader = new InputStreamReader(new FileInputStream(args[i++]), charset);
            else
                usage();
        }
        if (reader == null) {
            reader = new InputStreamReader(System.in);
            prompt = true;
        } else
            prompt = echo;
        if (writer == null)
            writer = new OutputStreamWriter(System.out);
        repl(reader, writer, context, echo, prompt);
    }
}
