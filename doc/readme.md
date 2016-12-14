# ラムダ計算

## 文法

```
Expression = Factor
           | Expression Factor

Factor     = Variable
           | Lambda
           | '(' Expression ')'

Lambda     = Variable '.' Expression
```

### 変数


### ラムダ式

通常のラムダ式は「λ変数の並び.本体」と表記しますが、
本プログラムにおけるラムダ式は「変数.本体」の形式で記述します。

```
λx.x   →   x.x
```


変数はひとつしか書けないので変数が複数ある場合はカリー化して表記する必要があります。
通常のラムダ式「λxy.x」はカリー化すると「λx.λy.x」となり、
本プログラムではここから「λ」を除去したもの、つまり「x.y.x」という表現になります。

```
λxy.x   →   λx.λy.x   →   x.y.x
```

ラムダ式を構成する「.」演算子は右結合なので「x.y.x」は「x.(y.x)」と同値です。

```
x.y.x   ⇔   x.(y.x)
```

### 関数適用

関数適用は左結合なので「x y z」は「(x y) z」と同値です。

### equals

正規化して評価する。


## チャーチ数

チャーチ数は自然数をラムダ式で表現したもので、0, 1, 2, 3 を以下のように表現します。

```
n0   = a.b.b
n1   = a.b.a b
n2   = a.b.a(a b)
n3   = a.b.a(a(a b))
```

`n`を受け取って`n + 1`を返す関数`succ`は以下のように表現します。

```
succ = n.f.x.f(n f x)
```

`succ n0`は以下のように評価されます。

```
> succ n0 : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 > succ : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 < n.f.x.f(n f x)
 > n0 : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 < a.b.b
 > f.x.f(n f x) : {n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
  > x.f(n f x) : {f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
   > f(n f x) : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
    > f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
    < f
    > n f x : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     > n f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      > n : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      < a.b.b
      > f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      < f
      > b.b : {a=f, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
       > b : {b=b, a=f, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
       < b
      < b.b
     < b.b
     > x : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     < x
     > b : {b=x, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     < x
    < x
   < f x
  < x.f x
 < f.x.f x
< f.x.f x
```

結果は`f.x.f x`なので前述の`n1`つまり`a.b.a b`に一致することがわかります。
