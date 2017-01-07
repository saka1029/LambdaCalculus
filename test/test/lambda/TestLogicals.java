package test.lambda;

import static org.junit.Assert.*;
import lambda.Context;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

public class TestLogicals {

    /**
     * 論理記号と述語
     * 
     * TRUE や FALSE といった真理値は慣習的に以下のように定義されることが多い。
     * これらはチャーチ真理値（英語版）（英: Church booleans）とよばれている。
     * TRUE := λx y. x
     * FALSE := λx y. y
     * （この FALSE は前述のチャーチ数のゼロと同じ定義であることに注意せよ）
     * これらの真理値に対して論理記号を定義することができる。たとえば、以下のようなものがある。
     * AND := λp q. p q FALSE
     * OR := λp q. p TRUE q
     * NOT := λp. p FALSE TRUE
     * IFTHENELSE := λp x y. p x y
     */
    @Test
    public void testLogicals() {
        Context c = defaultContext();
        reduce("define true (x.y.x)", c);
        reduce("define false (x.y.y)", c);
        reduce("define and (p.q.p q false)", c);
        reduce("define or (p.q.p true q)", c);
        reduce("define not (p.p false true)", c);
        reduce("define ifThenElse (p.x.y.p x y)", c);
        assertEquals(normalize("false", c), normalize("not true", c));
        assertEquals(normalize("true", c), normalize("not false", c));
        assertEquals(normalize("true", c), normalize("and true true", c));
        assertEquals(normalize("false", c), normalize("and true false", c));
        assertEquals(normalize("false", c), normalize("and false true", c));
        assertEquals(normalize("false", c), normalize("and false false", c));
        assertEquals(normalize("true", c), normalize("or true true", c));
        assertEquals(normalize("true", c), normalize("or true false", c));
        assertEquals(normalize("true", c), normalize("or false true", c));
        assertEquals(normalize("false", c), normalize("or false false", c));
        assertEquals(normalize("true", c), normalize("ifThenElse true true false", c));
        assertEquals(normalize("false", c), normalize("ifThenElse false true false", c));
        assertEquals(normalize("false", c), normalize("ifThenElse (not true) true false", c));
    }
}
