package test.lambda;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import lambda.Context;
import lambda.UnboundVariable;

import static lambda.LambdaCalculus.*;

public class TestLambdaCalculus {

    static void equalsNormalized(String expected, String actual, Context c) {
        assertEquals(normalize(expected, c), normalize(actual, c));
    }

    @Test
    public void testContext() {
        Context c = defaultContext();
        reduce("define true x.y.x", c);
        reduce("define ident x.x", c);
        reduce("define ident y.y", c);
        equalsNormalized("K", "ident K", c);
        equalsNormalized("true", "ident true", c);
        assertEquals("$$$define", reduce("define", c).toString());
        System.out.println(c);
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
     * 自然数と算術[編集]
     * 自然数をラムダ式で表現する方法はいくつか異なる手法が知られているが、
     * その中でもっとも一般的なのはチャーチ数（英語版）（英: Church numerals）と呼ばれるもので、
     * 以下のように定義されている。
     * 0 := λf x. x
     * 1 := λf x. f x
     * 2 := λf x. f (f x)
     * 3 := λf x. f (f (f x))
     * 以下同様である。直感的には、数 n はラムダ式では f という関数をもらって
     * それを n 回適用したものを返す関数である。つまり、チャーチ数は1引数関数を受け取り、
     * 1引数関数を返す高階関数である。（チャーチの提唱した元々のラムダ計算は、
     * ラムダ式の引数が少なくとも一回は関数の本体に出現していなくてはならないことになっていた。
     * そのため、その体系では上に挙げた 0 の定義は不可能である。）
     * 上のチャーチ数の定義のもとで、後続（後者）を計算する関数、
     * すなわち n を受け取って n + 1 を返す関数を定義することができる。それは以下のようになる。
     * SUCC := λn f x. f (n f x)
     * また、加算は以下のように定義できる。
     * PLUS := λm n f x. m f (n f x)
     * または単にSUCCを用いて、以下のように定義してもよい。
     * PLUS := λm n. m SUCC n
     * PLUS は2つの自然数をとり1つの自然数を返す関数である。
     * この理解のためには例えば、 PLUS 2 3 == 5 であることを確認してみるとよいだろう。また、乗算は以下のように定義される。
     * MULT := λm n. m (PLUS n) 0
     * この定義は、 m と n の乗算は、 0 に n を m回加えることと等しい、ということを利用して作られている。もう少し短く、
     * 以下のように定義することもできる。
     * MULT := λm n f. m (n f)
     * 正の整数 n の先行（前者）を計算する関数 PRED n = n − 1 は簡単ではなく、
     * PRED := λn f x. n (λg h. h (g f)) (λu. x) (λu. u)
     * もしくは
     * PRED := λn. n (λg k. (g 1) (λu. PLUS (g k) 1) k) (λv. 0) 0
     * と定義される。
     * 上の部分式 (g 1) (λu. PLUS (g k) 1) k は、 g(1) がゼロとなるとき k に評価され、
     * そうでないときは g(k) + 1 に評価されることに注意せよ。
     */
    @Test
    public void testNumerals() {
        Context c = defaultContext();
        reduce("define 0 f.x.x", c);
        reduce("define 1 f.x.f x", c);
        reduce("define 2 f.x.f (f x)", c);
        reduce("define 3 f.x.f( f (f x))", c);
        reduce("define 4 f.x.f( f( f (f x)))", c);
        reduce("define 5 f.x.f( f( f( f (f x))))", c);
        reduce("define 6 f.x.f( f( f( f( f (f x)))))", c);
        reduce("define succ n.f.x.f (n f x)", c);
        reduce("define + m.n.m succ n", c);
        reduce("define * m.n.m (+ n) 0", c);
        reduce("define pred n.f.x.n (g.h.h (g f)) (u.x) (u.u)", c);
        assertEquals(normalize("1", c), normalize("succ 0", c));
        assertEquals(normalize("2", c), normalize("succ 1", c));
        assertEquals(normalize("5", c), normalize("+ 2 3", c));
        assertEquals(normalize("6", c), normalize("* 2 3", c));
        assertEquals(normalize("4", c), normalize("* (+ 0 2) (+ 1 1)", c));
        assertEquals(normalize("3", c), normalize("pred 4", c));
        System.out.println(reduce("f.x.f x", c));
    }

    @Test
    public void testNumeralsLambda() {
        Context c = defaultContext();
        reduce("define 0 λf x.x", c);
        reduce("define 1 λf x.f x", c);
        reduce("define 2 λf x.f (f x)", c);
        reduce("define 3 λf x.f( f (f x))", c);
        reduce("define 4 λf x.f( f( f (f x)))", c);
        reduce("define 5 λf x.f( f( f( f (f x))))", c);
        reduce("define 6 λf x.f( f( f( f( f (f x)))))", c);
        reduce("define succ λn f x.f (n f x)", c);
        reduce("define + m.n.m succ n", c);
        reduce("define * m.n.m (+ n) 0", c);
        reduce("define pred λn f x.n (λg h.h (g f)) (λu.x) (λu.u)", c);
        assertEquals(normalize("1", c), normalize("succ 0", c));
        assertEquals(normalize("2", c), normalize("succ 1", c));
        assertEquals(normalize("5", c), normalize("+ 2 3", c));
        assertEquals(normalize("6", c), normalize("* 2 3", c));
        assertEquals(normalize("4", c), normalize("* (+ 0 2) (+ 1 1)", c));
        assertEquals(normalize("3", c), normalize("pred 4", c));
    }

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
        reduce("define true x.y.x", c);
        reduce("define false x.y.y", c);
        reduce("define and p.q.p q false", c);
        reduce("define or p.q.p true q", c);
        reduce("define not p.p false true", c);
        reduce("define ifThenElse p.x.y.p x y", c);
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
    
    /**
     * 対[編集]
     * （2つ組の）順序対のデータ型は、 TRUE および FALSE を用いて定義することができる。
     * これらはチャーチ対（英語版）（英: Church pairs）とよばれている。
     * CONS := λs b f. f s b
     * CAR := λp. p TRUE
     * CDR := λp. p FALSE
     * リンク型のリスト構造は、空リストのために特定の予約された値（例えば FALSE ）を用い、
     * リストをその先頭要素と後続リストの CONS 対として表現することによって実現できる。
     */
    @Test
    public void testPairs() {
        Context c = defaultContext();
        reduce("define true x.y.x", c);
        reduce("define false x.y.y", c);
        reduce("define nil x.true", c);
        reduce("define cons s.b.f.f s b", c);
        reduce("define car p.p true", c);
        reduce("define cdr p.p false", c);
        assertEquals(normalize("true", c), normalize("car (cons true false)", c));
        assertEquals(normalize("false", c), normalize("cdr (cons true false)", c));
        reduce("define ABC (cons A (cons B (cons C nil)))", c);
        assertEquals(normalize("A", c), normalize("car ABC", c));
        assertEquals(normalize("B", c), normalize("car (cdr ABC)", c));
        assertEquals(normalize("C", c), normalize("car (cdr (cdr ABC))", c));
        assertEquals(normalize("nil", c), normalize("cdr (cdr (cdr ABC))", c));
        assertEquals(normalize("cons B (cons C nil)", c), normalize("cdr ABC", c));
        System.out.println(reduce("ABC", c));
    }
    
    /**
     * Fixed point combinators in lambda calculus
     * The Y combinator, discovered by Haskell B. Curry, is defined as:

     * Y=λ f.(λ x.f (x x)) (λ x.f (x x))
     * Yコンビネータ
     * 型無しラムダ計算においてよく知られた（そしておそらく最もシンプルな）
     * 不動点コンビネータはYコンビネータと呼ばれる。
     * これはハスケル・カリーによって発見されたもので、次のように定義される。
     * Y = (λf . (λx . f (x x)) (λx . f (x x)))
     */
    @Ignore
    @Test(expected = StackOverflowError.class)
    public void testYCombinator() {
        // Y=λ f.(λ x.f (x x)) (λ x.f (x x))
        Context c = defaultContext();
        reduce("define Y λ f.(λ x.f (x x)) (λ x.f (x x))", c);
        assertEquals(normalize("g (Y g)", c), normalize("Y g", c));
    }
    
}
