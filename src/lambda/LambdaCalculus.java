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
        return c;
    }
    
    /**
     * Command line processor
     * 
     * [usage]
     * java lambda.LambdaCalculus [-e]
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Context c = defaultContext();
        InputStream in = System.in;
        boolean echo = false;
        String prompt = "% ";
        for (String arg : args) {
            switch (arg) {
            case "-e": echo = true; break;
            case "-t": LambdaCalculus.TRACE = true; break;
            case "-d": LambdaCalculus.TO_STRING_DOT = true; break;
            default:
                in = new FileInputStream(arg);
                break;
            }
        }
        try (InputStream is = in;
            Reader r = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(r)) {
            while (true) {
                System.out.print(prompt);
                String line = reader.readLine();
                if (line == null) break;
                if (echo) System.out.println(line);
                line = line.replaceFirst("#.*", "");
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
