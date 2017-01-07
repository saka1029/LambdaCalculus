package test.lambda;

import static org.junit.Assert.*;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

import lambda.Context;

public class TestCurchNumerals {

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
        reduce("define 0 (f.x.x)", c);
        reduce("define 1 (f.x.f x)", c);
        reduce("define 2 (f.x.f (f x))", c);
        reduce("define 3 (f.x.f( f (f x)))", c);
        reduce("define 4 (f.x.f( f( f (f x))))", c);
        reduce("define 5 (f.x.f( f( f( f (f x)))))", c);
        reduce("define 6 (f.x.f( f( f( f( f (f x))))))", c);
        reduce("define succ (n.f.x.f (n f x))", c);
        reduce("define + (m.n.m succ n)", c);
        reduce("define * (m.n.m (+ n) 0)", c);
        reduce("define pred (n.f.x.n (g.h.h (g f)) (u.x) (u.u))", c);
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
        reduce("define 0 (λf x.x)", c);
        reduce("define 1 (λf x.f x)", c);
        reduce("define 2 (λf x.f (f x))", c);
        reduce("define 3 (λf x.f( f (f x)))", c);
        reduce("define 4 (λf x.f( f( f (f x))))", c);
        reduce("define 5 (λf x.f( f( f( f (f x)))))", c);
        reduce("define 6 (λf x.f( f( f( f( f (f x))))))", c);
        reduce("define succ (λn f x.f (n f x))", c);
        reduce("define + (m.n.m succ n)", c);
        reduce("define * (m.n.m (+ n) 0)", c);
        reduce("define pred (λn f x.n (λg h.h (g f)) (λu.x) (λu.u))", c);
        assertEquals(normalize("1", c), normalize("succ 0", c));
        assertEquals(normalize("2", c), normalize("succ 1", c));
        assertEquals(normalize("5", c), normalize("+ 2 3", c));
        assertEquals(normalize("6", c), normalize("* 2 3", c));
        assertEquals(normalize("4", c), normalize("* (+ 0 2) (+ 1 1)", c));
        assertEquals(normalize("3", c), normalize("pred 4", c));
    }

}
