package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Expression;

import static lambda.Expression.*;

public class TestNormalize {
    
    static void normalize(String s) {
        Expression e = of(s);
        System.out.println(e + " -> " + e.normalize());
    }

    @Test
    public void testNormlize() {
        normalize(("(n.f.x.f(n f x)) (f.x.x)"));
        normalize(("(n.f.x.f(n f x)) (f.x.f x)"));
        normalize(("(n.f.x.f(n f x)) (f.x.f (f x))"));
        assertEquals(of("x.x C x"), of("a.a C a"));
        assertEquals(of("(x.f x)x"), of("(y.f y)x"));
        normalize("(x.f x)x");
        normalize("(y.f y)x");
    }
}
