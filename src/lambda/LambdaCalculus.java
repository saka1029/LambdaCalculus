package lambda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LambdaCalculus {

    public static boolean TO_STRING_DOT = false;
    public static boolean TRACE = false;
    public static UnboundVariable ON = UnboundVariable.of("on");
    public static UnboundVariable OFF = UnboundVariable.of("off");
    
    private LambdaCalculus() {}
    
    public static Term term(String source) {
        return Parser.parse(source);
    }
    
    public static Term reduce(String source, Context context) {
        return term(source).reduce(context);
    }
    
    public static Term normalize(String source, Context context) {
        return reduce(source, context).normalize();
    }
    
    static void define(Context context, String name, Primitive body) {
        context.define(UnboundVariable.of(name),
            new Primitive() {

                @Override
                public Term apply(Term argument, Context context) {
                    return body.apply(argument, context);
                }
                
                @Override
                public String toString() {
                    return "$$$" + name;
                }
            
            });
    }

    public static Context defaultContext() {
        Context c = new Context();
        define(c, "define", (arg0, context0) ->
            (Primitive) (arg1, context1) ->
                context1.define((UnboundVariable)arg0, arg1.reduce(context1)));
        define(c, "trace", (arg, context) -> {
            TRACE = !arg.equals(OFF);
            return arg;
        });
        define(c, "dot", (arg, context) -> {
            TO_STRING_DOT = !arg.equals(OFF);
            return arg;
        });
        return c;
    }
    
    /**
     * Command line processor
     * 
     * [usage]
     * java lambda.LambdaCalculus [-e] [-t] [-d] [FILENAME]
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Context c = defaultContext();
        InputStream in = System.in;
        boolean echo = false;
        String prompt = "% ";
        int i;
        L: for (i = 0; i < args.length; ++i)
            switch (args[i]) {
            case "-e": echo = true; break;
            case "-t": LambdaCalculus.TRACE = true; break;
            case "-d": LambdaCalculus.TO_STRING_DOT = true; break;
            default: break L;
            }
        if (i < args.length)
            in = new FileInputStream(args[i]);
        boolean isConsole = System.console() != null && in == System.in;
        try (InputStream is = in;
            Reader r = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(r)) {
            while (true) {
                if (isConsole || echo)
                    System.out.print(prompt);
                String line = reader.readLine();
                if (line == null) break;
                if (echo)
                    System.out.println(line);
                line = line.replaceFirst("#.*", "").trim();
                if (line.length() == 0) continue;
                if (line.equalsIgnoreCase("exit")) break;
                if (line.equalsIgnoreCase("quit")) break;
                try {
                    Term term = reduce(line, c);
                    System.out.println(term);
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    
}
