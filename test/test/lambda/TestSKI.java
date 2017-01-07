package test.lambda;

import static org.junit.Assert.*;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

import lambda.Context;

public class TestSKI {

    static Context SKIContext() {
        Context c = defaultContext();
        reduce("define I (λx.x)", c);
        reduce("define K (λx y.x)", c);
        reduce("define S (λx y z.x z (y z))", c);
        return c;
    }
    
    @Test
    public void testSKSK() {
        Context c = SKIContext();
        assertEquals(normalize("K", c), normalize("S K S K", c));
        assertEquals(normalize("I", c), normalize("S K S", c));
        assertEquals(normalize("I", c), normalize("S K K", c));
        assertEquals(normalize("I", c), normalize("S K x", c));
    }
    
    @Test
    public void testBoolean() {
        Context c = SKIContext();
        reduce("define T K", c);
        reduce("define F (K I)", c);
        reduce("define NOT (S (S I (K F))(K T))", c);
        reduce("define OR (S I (K T))", c);
        reduce("define AND (S S (K (K F)))", c);
        assertEquals(normalize("F", c), normalize("NOT T", c));
        assertEquals(normalize("T", c), normalize("NOT F", c));
        assertEquals(normalize("T", c), normalize("AND T T", c));
        assertEquals(normalize("F", c), normalize("AND T F", c));
        assertEquals(normalize("F", c), normalize("AND F T", c));
        assertEquals(normalize("F", c), normalize("AND F F", c));
        assertEquals(normalize("T", c), normalize("OR T T", c));
        assertEquals(normalize("T", c), normalize("OR T F", c));
        assertEquals(normalize("T", c), normalize("OR F T", c));
        assertEquals(normalize("F", c), normalize("OR F F", c));
    }
}
