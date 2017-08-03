package test.lambda;

import static lambda.LambdaCalculus.TO_STRING_DOT;
import static lambda.LambdaCalculus.TRACE;
import static lambda.LambdaCalculus.defaultContext;
import static lambda.LambdaCalculus.normalize;
import static lambda.LambdaCalculus.reduce;
import static lambda.LambdaCalculus.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Ignore;
import org.junit.Test;

import lambda.Context;

public class TestLambdaCalculus {

    static void equalsNormalized(String expected, String actual, Context c) {
        assertEquals(normalize(expected, c), normalize(actual, c));
    }

    static void notEqualsNormalized(String expected, String actual, Context c) {
        assertNotEquals(normalize(expected, c), normalize(actual, c));
    }

    @Test
    public void testContext() {
        Context c = defaultContext();
        reduce("define true (x.y.x)", c);
        reduce("define ident (x.x)", c);
        reduce("define ident (y.y)", c);
        equalsNormalized("K", "ident K", c);
        equalsNormalized("true", "ident true", c);
        assertEquals("$$$define", reduce("define", c).toString());
        System.out.println(c);
    }

    @Test
    public void testUndefine() {
        Context c = defaultContext();
        reduce("define true (x.y.x)", c);
        equalsNormalized("x.y.x", "true", c);
        reduce("undefine true", c);
        equalsNormalized("true", "true", c);
        notEqualsNormalized("x.y.x", "true", c);
    }

    @Test
    public void testBuiltin() {
        Context c = defaultContext();
        assertEquals(false, TRACE);
        reduce("trace on", c);
        assertEquals(true, TRACE);
        reduce("trace off", c);
        assertEquals(false, TRACE);
        assertEquals(false, TO_STRING_DOT);
        reduce("dot on", c);
        assertEquals(true, TO_STRING_DOT);
        reduce("dot off", c);
        assertEquals(false, TO_STRING_DOT);
    }

    @Test
    public void testAlphaConversion() {
        Context c = defaultContext();
        equalsNormalized("x.y.y K", "x.x.x K", c);
        equalsNormalized("x.x", "(x.x.x) K", c);
        equalsNormalized("x.x.x", "(y.x.y) x.x", c);
        equalsNormalized("x.y.y", "(y.x.y) x.x", c);
        equalsNormalized("x.x", "(x.(x.x x) x) y.y", c);
        equalsNormalized("x.y.x", "define true λx y.x", c);
        equalsNormalized("λx x y.x", "define nil λx.true", c);
        equalsNormalized("λz x y.x", "λx.true", c);
        equalsNormalized("true", "nil K", c);
        // x.true x -> x.(x.y.x) x -> x.y.x
        // z.true z -> z.(x.y.x) z -> apply x to z -> z.(y.z) -> z.y.z
        equalsNormalized("x.y.x", "x.true x", c);
        equalsNormalized("x.y.x", "x.(x.y.x) x", c);
        equalsNormalized("z.x.y.x x", "x.x.y.x x", c);
        equalsNormalized("x.y.x", "z.true z", c);
        equalsNormalized("x.x x.y.x", "x.x true", c);
        equalsNormalized("K true", "(x.x true) K", c);
    }

    /**
     * η-renameが必要となる場合 from Wikipedia
     */
    @Test
    public void testRequireEtaRenameFromWikipedia() {
        Context c = defaultContext();
        equalsNormalized("z.z (x.y)", "(x.y.y x) (x.y)", c);
        System.out.println(reduce("(x.y.y x) (x.y)", c));
        // -> y.y x.y
        // 最後のyは自由変数である。
        // 実体としては正しいが、表記としては誤り。
        System.out.println(normalize("(x.y.y x) (x.y)", c));
        // -> $0.$0 $1.y
        //  = z.z x.y
        // 最後のyが自由変数であることがわかる。
    }

    @Test
    public void testParen() {
        Context c = defaultContext();
        equalsNormalized("x.y.x", "define true λx y.x", c);
        System.out.println(reduce("z.true z", c));
        System.out.println(term("x.(x.y.x) x"));
    }

    /**
     * <b>Sample Interpretations</b><br>
     * Below are some lambda calculus interpretation test cases:
     * <pre><code>
     * Expression               Result           Comment
     * (\x.\y.(y x) (y w))  ->  \z.(z (y * w))    * Avoid capturing the free variable y in (y w)
     * (\x.\y.(x y) (y w))  ->  (y w)             * Avoid capturing the free variable yin (y w), and perform eta reduction
     * (\x.x y)             ->  y                 * Identity combinator
     * \x.(y x)             ->  y                 * Eta reduction
     * ((\y.\x.(y x) \x.(x x)) y) -> (y y)        * Application combinator
     * (((\b.\t.\e.((b t) e) \x.\y.x) x) y) -> x  * If-then-else combinator
     * \x.((\x.(y x) \x.(z x)) x)  ->  (y z)      * Eta reductions
     * (\y.(\x.\y.(x y) y) (y w))  ->  (y w)      * Alpha renaming, beta reduction and eta reduction all * involved
     * </code><pre>
     * @see <a href="http://www.cs.rpi.edu/academics/courses/fall16/proglang/pa1/programming_assignment_1.pdf"
     * >Programming Assignment #1</a>
     */
    @Test
    public void testProgrammingAssignment() {
        Context c = defaultContext();
        // (\x.\y.(y x) (y w))  ->  \z.(z (y * w))    * Avoid capturing the free variable y in (y w)
        equalsNormalized("z.z (y w)", "(x.y.y x) (y w)", c);
        // (\x.\y.(x y) (y w))  ->  (y w)             * Avoid capturing the free variable yin (y w), and perform eta reduction
        equalsNormalized("y w", "(x.y.x y) (y w)", c);
        // (\x.x y)             ->  y                 * Identity combinator
        equalsNormalized("y", "(x.x) y", c);
        // \x.(y x)             ->  y                 * Eta reduction
        equalsNormalized("y", "x.y x", c);
        // ((\y.\x.(y x) \x.(x x)) y) -> (y y)        * Application combinator
        equalsNormalized("y y", "((y.x.y x) x.x x) y", c);
        // (((\b.\t.\e.((b t) e) \x.\y.x) x) y) -> x  * If-then-else combinator
        equalsNormalized("x", "(b.t.e.b t e) (x.y.x) x y", c); // ifThenElse true x y
        equalsNormalized("y", "(b.t.e.b t e) (x.y.y) x y", c); // ifThenElse false x y
        // \x.((\x.(y x) \x.(z x)) x)  ->  (y z)   * Eta reductions
        equalsNormalized("y z", "x.((x.y x) (x.z x)) x", c);
        // (\y.(\x.\y.(x y) y) (y w))  ->  (y w)   * Alpha renaming, beta reduction and eta reduction all * involved
        // This implementation does not require α-renaming
        equalsNormalized("y w", "(y.((x.y.x y) y)) (y w)", c);
    }

    /**
     * Y combinator
     *
     * Fixed point combinators in lambda calculus
     * The Y combinator, discovered by Haskell B. Curry, is defined as:

     * Y=λ f.(λ x.f (x x)) (λ x.f (x x))
     *
     * Yコンビネータ
     * 型無しラムダ計算においてよく知られた（そしておそらく最もシンプルな）
     * 不動点コンビネータはYコンビネータと呼ばれる。
     * これはハスケル・カリーによって発見されたもので、次のように定義される。
     * Y = (λf . (λx . f (x x)) (λx . f (x x)))
     *
     * !!!! 今の強欲なeduce(簡約)の実装ではStackOverflowが発生する。
     */
    @Ignore
    @Test(expected = StackOverflowError.class)
    public void testYCombinator() {
        // Y=λ f.(λ x.f (x x)) (λ x.f (x x))
        Context c = defaultContext();
        reduce("define Y (λ f.(λ x.f (x x)) (λ x.f (x x)))", c);
        assertEquals(normalize("g (Y g)", c), normalize("Y g", c));
    }
}
