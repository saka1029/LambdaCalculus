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
//        reduce("define F (K I)", c);
        reduce("define F (S K)", c);
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
    
    /**
     * Iota combinator
     * 
     * In fact, it is possible to define a complete system using only one combinator.
     * An example is Chris Barker's iota combinator, which can be expressed in terms of S and K as follows:
     * ιx = xSK
     * It is possible to reconstruct S, K, and I from the iota combinator.
     * Applying ι to itself gives ιι = ιSK = SSKK = SK(KK)
     * which is functionally equivalent to I.
     * K can be constructed by applying ι twice to I
     * (which is equivalent to application of ι to itself):
     * ι(ι(ιι)) = ι(ιI) yields ι(ISK) = ι(SK) = SKSK = K (see Example computation).
     * Applying ι one more time gives ι(ι(ι(ιι))) = ιK = KSK = S.
     */
    @Test
    public void testIota() {
        Context c = SKIContext();
//        reduce("define ι (λx.x S K)", c);
        reduce("define ι (λx.x (λx y z.x z (y z)) λx y.x)", c);
        assertEquals(normalize("S S K K", c), normalize("ι ι", c));
        assertEquals(normalize("S K (K K)", c), normalize("ι ι", c));
        assertEquals(normalize("I", c), normalize("ι ι", c));
        assertEquals(normalize("K", c), normalize("ι (ι (ι ι))", c));
        assertEquals(normalize("S", c), normalize("ι (ι (ι (ι ι)))", c));
    }
 
    @Test
    public void testBoolean2() {
        Context c = SKIContext();
        reduce("define T K", c);
        reduce("define F (S K)", c);
        reduce("define NOT (x.x F T)", c);
//        reduce("define NOT (F T)", c);
        reduce("define OR x.y.x T y", c);
//        reduce("define OR T", c);
        reduce("define AND x.y.x y F", c);
//        reduce("define AND F", c);
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
    
    @Test
    public void testReversalExpression() {
        Context c = SKIContext();
        reduce("define reverse (S (K (S I)) K)", c);
        assertEquals(normalize("β α", c), normalize("reverse α β", c));
        assertEquals(normalize("β α", c), normalize("S (K (S I)) K α β", c));
    }
}
