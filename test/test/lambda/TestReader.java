package test.lambda;

import static org.junit.Assert.*;


import org.junit.Test;

import static lambda.Expression.*;

public class TestReader {
    
    @Test
    public void testRead() {
        assertEquals(variable("a"), of("a"));
        assertEquals(variable("a"), of("a  "));
        assertEquals(variable("abc"), of("(abc)"));
        assertEquals(variable("a").apply(variable("b")), of("a  b"));
        assertEquals(variable("a").lambda(variable("a")), of("a.a"));
        assertEquals(variable("a").lambda(variable("a")), of("a . a"));
        assertEquals(variable("a").lambda(variable("a")), of("(a . a)"));
        assertEquals(variable("a").lambda(variable("a")), of("a . ( a )"));
        assertEquals(variable("a").lambda(variable("b").lambda(variable("a"))), of("  a . b. a"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testMissingRightParen() {
        of("(ab");
    }
    
    @Test(expected = RuntimeException.class)
    public void testUnknownChar() {
        of("#abc");
    }
    
    @Test(expected = RuntimeException.class)
    public void testUnread() {
        of("ab)");
    }
    
}
