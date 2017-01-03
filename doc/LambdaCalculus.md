# ラムダ計算

## α-変換
α-conversion

<!--
Alpha-conversion, sometimes known as alpha-renaming,
allows bound variable names to be changed.
For example, alpha-conversion of λx.x might yield λy.y.
Terms that differ only by alpha-conversion are called α-equivalent.
Frequently, in uses of lambda calculus, α-equivalent terms are considered to be equivalent.
-->

α-変換はα-名前変更とも呼ばれる。
それは束縛変数の名前を変更する。
例えば、`λx.x`は`λy.y`に置き換えることができる。
α-変換によって置換可能な二つの項はα-等価と呼ばれる。
ラムダ計算においてα-等価な項は等しいとされる。


<!--
The precise rules for alpha-conversion are not completely trivial.
First, when alpha-converting an abstraction,
the only variable occurrences that are renamed are those that are bound to the same abstraction.
For example, an alpha-conversion of λx.λx.x could result in λy.λx.x,
but it could not result in λy.λx.y.
The latter has a different meaning from the original.
-->
α-変換の正確なルールは自明ではない。
第１にα-変換するとき、名前を変更する変数は束縛変数でなければならない。
例えば、`λx.λx.x`をα-変換して`λy.λx.x`とすることができるが`λy.λx.y`とはならない。
後者は元の式とは異なる。
（元の式の末尾にある関数本体`x`は２番目のラムダ式の束縛変数であって、
最初のラムダ式の束縛変数ではない点に注意する必要がある）

<!--
Second, alpha-conversion is not possible if it would result in a variable getting captured by a different abstraction.
For example, if we replace x with y in λx.λy.x, we get λy.λy.y, which is not at all the same.
-->
第２に変換結果の変数が別の部分に束縛される場合はα-変換できない。
例えば、`λx.λy.x`の変数`x`を`y`に置換すると`λ.y.λy.y`となるがこれは元の式とは異なる。

→ 変換しようとしているラムダ式の中に決して出現しない変数名に置換することで、この問題は回避できる。

<!--
In programming languages with static scope,
alpha-conversion can be used to make name resolution simpler
by ensuring that no variable name masks a name in a containing scope
(see alpha renaming to make name resolution trivial).
-->
静的スコープを持つプログラミング言語では、
α-変換を使用して名前解決を簡単にすることができる
変数名が含まれているスコープ内の名前をマスクしないようにする必要がある
（名前解決を簡単にするためにアルファ名前変更を参照のこと）。


<!--
In the De Bruijn index notation, any two alpha-equivalent terms are literally identical.
-->
ド・ブラン索引記法においては、どの二つのα-変換も同値である。

### ド・ブラン索引

ド・ブラン索引 (De Bruijn Index)とは、ラムダ計算において、
名前を使わずに引数（束縛変数）を参照するための記法である。
オランダ人数学者ニコラース・ホーバート・ド・ブランによって発明された。
この記法では、それぞれのλでは引数の名前を書かない。引数は、通常の記法でその引数を宣言するλが、
何階層外側にあるかを表す自然数の番号で表記する。
例えば、λz. (λy. y (λx. x)) (λx. z x) は λ (λ 1 (λ 1)) (λ 2 1) となる。
ド・ブラン・レベルは絶対的な位置を表すが、ド・ブラン・インデックスは相対的な位置を表す。

## Substitution
Substitution, written E[V := R],
is the process of replacing all free occurrences of the variable V in the expression E with expression R.
Substitution on terms of the λ-calculus is defined by recursion on the structure of terms,
as follows (note: x and y are only variables while M and N are any λ expression).

```
x[x := N]       ≡ N
y[x := N]       ≡ y, if x ≠ y
(M1 M2)[x := N] ≡ (M1[x := N]) (M2[x := N])
(λx.M)[x := N]  ≡ λx.M
(λy.M)[x := N]  ≡ λy.(M[x := N]), if x ≠ y, provided y ∉ FV(N)
To substitute into a lambda abstraction, it is sometimes necessary to α-convert the expression. For example, it is not correct for (λx.y)[y := x] to result in (λx.x), because the substituted x was supposed to be free but ended up being bound. The correct substitution in this case is (λz.x), up to α-equivalence. Notice that substitution is defined uniquely up to α-equivalence.
```