package Chapter5

object chapter5 {

  /**
    * # 5 Sequencing Computations
    * このセクションでは、ジェネリックスと関数という2つの言語機能と、これらの機能を使って構築できる抽象化手法であるファンクタとモナドについて見ていきます。
    *
    * 前のセクションで開発したコードを出発点とします。整数のリストであるIntListを開発し、以下のようなコードを書きました。
    */
  sealed trait IntList {
    def length: Int =
      this match {
        case End          => 0
        case Pair(hd, tl) => 1 + tl.length
      }
    def double: IntList =
      this match {
        case End          => End
        case Pair(hd, tl) => Pair(hd * 2, tl.double)
      }
    def product: Int =
      this match {
        case End          => 1
        case Pair(hd, tl) => hd * tl.product
      }
    def sum: Int =
      this match {
        case End          => 0
        case Pair(hd, tl) => hd + tl.sum
      }
  }
  case object End extends IntList
  final case class Pair(head: Int, tail: IntList) extends IntList

  /**
  * このコードには2つの問題があります。
  * 1つ目の問題は、リストの格納先がIntsに限定されていることです。
  * 2つ目の問題は、繰り返しが多いことです。
  * 構造的な再帰パターンを使用しているので当然ですが、コードは同じ一般的な構造を持っており、重複の量を減らすことができたらいいと思います。
  *
  * このセクションでは、この2つの問題に取り組みます。
  * 前者については、ジェネリックを使って型を抽象化し、ユーザーが指定した型で動作するデータを作成できるようにします。
  * 後者については、関数を使ってメソッドを抽象化することで、コードの重複を減らすことができます。
  *
  * これらのテクニックを使っているうちに、いくつかの一般的なパターンが見えてくるでしょう。
  * このセクションの最後では、これらのパターンに名前を付けて詳しく調べてみましょう。
  */
}

object chapter51 {

  /**
    * ## 5.1 Generics
    * 汎用型は、型の抽象化を可能にするものです。
    * 汎用型はあらゆる種類のデータ構造に役立ちますが、一般的にはコレクションで使用されることが多いので、ここから始めます。
    */
  object chapter511 {

    /**
      * ### 5.1.1 Pandora’s Box
      * まず、リストよりも単純なコレクションから始めましょう。
      * 一つの値を格納するボックスです。
      * ボックスに格納されている型は気にしませんが、ボックスから値を取り出すときにその型を保持するようにしたいと思います。そのために、ジェネリック型を使います。
      */
    final case class Box[A](value: A)
    //    Box(2)
    //    // res0: Box[Int] = Box(2)
    //
    //    res0.value
    //    // res1: Int = 2
    //
    //    Box("hi") // typeパラメータを省略すると，scalaはその値を推測します。
    //    // res2: Box[String] = Box(hi)
    //
    //    res2.value
    //    // res3: String = hi

    /**
      * 構文[A]を型パラメータと呼びます。
      * また、型パラメータをメソッドに追加することもできます。
      * この場合、パラメータのスコープはメソッドの宣言とボディに限定されます。
      */
    def generic[A](in: A): A = in

    generic[String]("foo")
    // res4: String = foo

    generic(1) // typeパラメータを省略した場合、scalaはそれを推論します。
    // res5: Int = 1

    /**
      * 型のパラメータは、メソッドのパラメータと似たような仕組みになっています。
      * メソッドを呼び出すときには、メソッドのパラメータ名をメソッド呼び出しの中で指定された値にバインドします。
      * 例えば、generic(1)を呼び出すと、genericのボディ内でinという名前が1という値に束縛されます。
      *
      * 型パラメータを持つメソッドを呼び出したり、クラスを構築したりすると、型パラメータはメソッドやクラスの本体内の具象型に束縛されます。
      * つまり、generic(1)を呼び出すと、型パラメータAはgenericの本体内のIntに束縛されます。
      */

    /**
      * ---
      *
      * **Type Parameter Syntax：タイプ パラメータ シンタックス**
      * [A, B, C]のように、角括弧内に型名のリストを入れて、ジェネリック型を宣言します。
      * 慣習的に、ジェネリック型には単一の大文字を使用します。
      *
      * ジェネリック型は、クラス宣言やtrait宣言の中で宣言することができ、その場合は宣言の残りの部分で見ることができます。
      */
    //    case class Name[A](...){ ... }
    //    trait Name[A]{ ... }
    /**
      * また、メソッド宣言の中で宣言することもできますが、その場合は、メソッド内でのみ表示されます。
      */
    //    def name[A](...){ ... }
    /**
    *
    * ---
    */
  }

  object chapter512 {

    /**
      * ### 5.1.2 Generic Algebraic Data Types
      * 型パラメータはメソッドのパラメータに似ていると説明しましたが、型パラメータを持つ trait を拡張する場合にもこの類似性が続きます。
      * sum型で行うようなtraitの拡張は、型レベルでのメソッド呼び出しに相当し、拡張しているtraitの型パラメータには値を与えなければなりません。
      *
      * これまでのセクションでは、以下のようなsum(some?)型を見てきました。
      */
    //    sealed trait Calculation
    //    final case class Success(result: Double) extends Calculation
    //    final case class Failure(reason: String) extends Calculation

    /**
      * これを一般化して、結果をDoubleに限定せず、何か一般的な型にしてみましょう。
      * そうすることで、数値計算に限定されなくなるので、名前をCalculationからResultに変更しましょう。
      * これで、データの定義は次のようになります。
      *
      * A型のResultは、A型のSuccessか、Stringの理由を持つFailureです。これは以下のコードに変換されます。
      */
    sealed trait Result[A]

    case class Success[A](result: A) extends Result[A]

    case class Failure[A](reason: String) extends Result[A]

    /**
      * SuccessとFailureの両方が、Resultが拡張されるときに渡される型パラメータAを導入していることに注目してほしい。
      * SuccessもA型の値を持つが、FailureはAを導入するだけなので、Resultに渡すことができる。
      * 後のセクションでは、よりすっきりとした実装方法であるvarianceを紹介しますが、今のところはこのパターンを使用します。
      */
    /**
      * ---
      *
      * **Invariant Generic Sum Type Pattern：不変の汎用和型パターン**
      * タイプTのAがBまたはCの書き込みである場合
      */
    //    sealed trait A[T]
    //    final case class B[T]() extends A[T]
    //    final case class C[T]() extends A[T]
    /**
    *
    * ---
    */
  }

  object chapter513 {

    /**
      * ### 5.1.3 Exercises
      */
    object chapter5131 {

      /**
        * #### 5.1.3.1 Generic List
        */
      /**
        * 私たちのIntList型は次のように定義されました。
        */
      //      sealed trait IntList
      //      case object End extends IntList
      //      final case class Pair(head: Int, tail: IntList) extends IntList
      /**
        * 名前をLinkedListに変更し、リストに格納されるデータの種類に汎用性を持たせます。
        */
      //      sealed trait LinkedList[A]
      //      case object End[A] extends LinkedList[A]
      //      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

      /**
        * 模範
        */
      /**
        * これは、ジェネリック・サム・タイプ・パターンの応用です。
        */
      sealed trait LinkedList[A]

      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

      final case class End[A]() extends LinkedList[A]
    }

    object chapter5132a {

      /**
        * #### 5.1.3.2 Working With Generic Types
        * このLinkedList型でできることはあまりありません。
        * 型は利用可能な操作を定義するものであり、Aのような汎用型では、利用可能な操作を定義する具象型がないことを覚えておいてください。
        * (汎用型はクラスがインスタンス化されたときに具象化されますが、それではクラスの定義にある情報を利用するには遅すぎます）
        *
        * しかし、LinkedListを使っていくつかの便利な機能を実現することができます。
        * lengthを実装し、LinkedListの長さを返します。いくつかのテストケースを以下に示します。
        *
        * ①
        */
      //      val example = Pair(1, Pair(2, Pair(3, End())))
      //      assert(example.length == 3)
      //      assert(example.tail.length == 2)
      //      assert(End().length == 0)
      sealed trait LinkedList[A] {
        def length: Int =
          this match {
            case End()       => 0
            case Pair(_, tl) => 1 + tl.length
          }
      }

      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

      final case class End[A]() extends LinkedList[A]

      /**
        * 模範
        */
      /**
        * このコードは、IntListのlengthの実装とほとんど変わりません。
        */
      //      sealed trait LinkedList[A] {
      //        def length: Int =
      //          this match {
      //            case Pair(hd, tl) => 1 + tl.length
      //            case End() => 0
      //          }
      //      }
      //      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
      //      final case class End[A]() extends LinkedList[A]
    }

    object chapter5132b {

      /**
        * JVMでは、すべての値が等しいかどうかを比較することができます。
        * 与えられたアイテムがリストにあるかどうかを判断するメソッドcontainsを実装します。
        * 自分のコードが以下のテストケースで動作することを確認する。
        *
        * ②
        */
      //      val example = Pair(1, Pair(2, Pair(3, End())))
      //      assert(example.contains(3) == true)
      //      assert(example.contains(4) == false)
      //      assert(End().contains(0) == false)
      //      // This should not compile
      //      // example.contains("not an Int")

      sealed trait LinkedList[A] {
        def contains(x: A): Boolean =
          this match {
            case End()        => false
            case Pair(hd, tl) => if (hd == x) true else tl.contains(x)
          }
      }

      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]

      final case class End[A]() extends LinkedList[A]

      /**
        * 模範
        */
      /**
        * これも標準的な構造的再帰パターンの例です。重要な点は、containsがA型のパラメータを取ることです。
        */
      //      sealed trait LinkedList[A] {
      //        def contains(item: A): Boolean =
      //          this match {
      //            case Pair(hd, tl) =>
      //              if (hd == item)
      //                true
      //              else
      //                tl.contains(item)
      //            case End() => false
      //          }
      //      }
      //
      //      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
      //      final case class End[A]() extends LinkedList[A]
    }

    object chapter5132c {

      /**
        * リストの n 番目の項目を返す apply メソッドを実装する。
        *
        * ヒント: コードの中でエラーを通知する必要がある場合（このような状況が1つあります）、例外を投げることを検討してください。以下にその例を示します。
        *
        * ③
        */
      //      throw new Exception("Bad things happened")

      /**
        * 以下のテストケースでソリューションが動作することを確認してください。
        */
      //      val example = Pair(1, Pair(2, Pair(3, End())))
      //      assert(example(0) == 1)
      //      assert(example(1) == 2)
      //      assert(example(2) == 3)
      //      assert(try {
      //        example(3)
      //        false
      //      } catch {
      //        case e: Exception => true
      //      })

//      sealed trait LinkedList[A] {
//        def length: Int =
//          this match {
//            case End()       => 0
//            case Pair(_, tl) => 1 + tl.length
//          }
//
//        def apply(n: Int): A = {
//          this match {
//            case Pair(hd, tl) => ???
//            case End()        => ???
//          }
//        }
//      }
//
//      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
//      final case class End[A]() extends LinkedList[A]

      /**
        * 模範
        */
      /**
        * この問題にはいくつかの興味深い点があります。
        * おそらく最も簡単な部分は、applyメソッドの戻り値の型としてジェネリック型を使用することです。
        *
        * 次に挙げられるのはEndケースで、ヒントではExceptionを投げるように言われていました。
        * 厳密に言えば、このケースではJavaのIndexOutOfBoundsExceptionを投げるべきですが、すぐにコードから例外処理を完全に取り除く方法を見てみましょう。
        *
        * 最後に、おそらく最も厄介な部分である、実際の構造的な再帰処理に入ります。
        * 重要な洞察は、インデックスがゼロの場合は現在の要素を選択し、そうでない場合はインデックスから1を引いて再帰するということです。
        * 整数は、1の足し算で再帰的に定義できます。例えば、3 = 2 + 1 = 1 + 1 + 1です。ここでは、リストと整数に対して構造的再帰を行っています。
        */
      sealed trait LinkedList[A] {
        def apply(index: Int): A =
          this match {
            case Pair(hd, tl) =>
              if (index == 0)
                hd
              else
                tl(index - 1)
            case End() =>
              throw new Exception("Attempted to get element from an Empty list")
          }
      }
      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
      final case class End[A]() extends LinkedList[A]
    }

    object chapter5132d {

      /**
        * 例外を投げることはクールではありません。例外を投げると、型安全性が失われます。
        * 型システムには、エラーを処理することを思い出させてくれるものが何もないからです。
        * 成功したか失敗したかを符号化した何らかの結果を返す方がずっと良いでしょう。このセクションでは、そのような型を紹介しました。
        */
      sealed trait Result[A]
      case class Success[A](result: A) extends Result[A]
      case class Failure[A](reason: String) extends Result[A]

      /**
        * applyを変更して、Resultを返すようにし、何が悪かったのかを示すfailure caseを追加します。
        * ここでは、いくつかのテストケースをご紹介します。
        *
        * ④
        */
//      assert(example(0) == Success(1))
//      assert(example(1) == Success(2))
//      assert(example(2) == Success(3))
//      assert(example(3) == Failure("Index out of bounds"))
      sealed trait LinkedList[A] {
        def apply(index: Int): Result[A] =
          this match {
            case Pair(hd, tl) => if (index == 0) Success(hd) else tl(index - 1)
            case End()        => Failure("Index out of bounds")
          }
      }
      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
      final case class End[A]() extends LinkedList[A]

      /**
        * 模範
        */
//      sealed trait Result[A]
//      case class Success[A](result: A) extends Result[A]
//      case class Failure[A](reason: String) extends Result[A]
//
//      sealed trait LinkedList[A] {
//        def apply(index: Int): Result[A] =
//          this match {
//            case Pair(hd, tl) =>
//              if(index == 0)
//                Success(hd)
//              else
//                tl(index - 1)
//            case End() =>
//              Failure("Index out of bounds")
//          }
//      }
//      final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
//      final case class End[A]() extends LinkedList[A]
    }
  }
}

object chapter52 {

  /**
    * ## 5.2 Functions
    * 関数を使うと、メソッドを抽象化して、メソッドを値に変え、プログラムの中で渡したり操作したりすることができます。
    *
    * ここでは、IntListを操作する3つのメソッドを見てみましょう。
    */
//  sealed trait IntList {
//    def length: Int =
//      this match {
//        case End          => 0
//        case Pair(hd, tl) => 1 + tl.length
//      }
//    def double: IntList =
//      this match {
//        case End          => End
//        case Pair(hd, tl) => Pair(hd * 2, tl.double)
//      }
//    def product: Int =
//      this match {
//        case End          => 1
//        case Pair(hd, tl) => hd * tl.product
//      }
//    def sum: Int =
//      this match {
//        case End          => 0
//        case Pair(hd, tl) => hd + tl.sum
//      }
//  }
//
//  case object End extends IntList
//  case class Pair(hd: Int, tl: IntList) extends IntList

  /**
    * これらのメソッドはすべて同じ一般的なパターンを持っていますが、これは構造的再帰を使用しているので驚くことではありません。
    * 重複している部分を取り除くことができればいいのですが。
    *
    * まず、Intを返すメソッド、length、product、sumに注目してみましょう。次のようなメソッドを書きたいと思います。
    */
//  def abstraction(end: Int, f: ???): Int =
//    this match {
//      case End          => end
//      case Pair(hd, tl) => f(hd, tl.abstraction(end, f))
//    }
  /**
    * ペアケースのヘッドと再帰呼び出しの組み合わせを行う、ある種のオブジェクトを示すためにfを使っています。
    * 今のところ、この値の型をどうやって書くのか、どうやって構築するのかわかりません。
    * しかし、このセクションのタイトルから、私たちが欲しいのは関数であることが推測できます。
    *
    * 関数はメソッドのようなもので、パラメータを指定して呼び出し、評価して結果を得ることができます。
    * メソッドとは異なり、関数は値です。
    * 関数は、メソッドや他の関数に渡すことができます。また、関数をメソッドから返すこともできます。
    *
    * このコースのかなり前に apply メソッドを紹介しましたが、これはオブジェクトを構文的には関数として扱うことができるものです。
    */
  object add1 {
    def apply(in: Int) = in + 1
  }
  add1(2)
  // res0: Int = 3
  /**
    * これは，Scalaで真の関数型プログラミングを行うための大きな一歩ですが，重要な要素が1つ欠けています :types です
    *
    * これまで見てきたように，型は値の抽象化を可能にします．
    * 加算器のような特殊な関数も見てきましたが，私たちが本当に必要としているのは，あらゆる種類の計算を表現することができる汎用的な型のセットです．
    *
    * それがScalaの関数型です。
    */
  object chapter521 {

    /**
      * ### 5.2.1 Function Types
      * 関数の型は(A, B) => Cのように書きます。
      * AとBはパラメータの型で、Cは結果の型です。同じパターンで、引数のない関数から任意の数の引数を持つ関数まで一般化されます。
      *
      * 上の例では、fは2つのIntsをパラメータとして受け取り、Intを返す関数とします。
      * したがって、(Int, Int) => Intと書くことができます。
      */

    /**
      * ---
      *
      * **関数型宣言構文：Function Type Declaration Syntax**
      * 関数型を宣言するには、次のように書きます。
      */
//    (A, B, ...) => C
    /**
      * ここで
      * * A、B、...は入力パラメーターの型であり
      * * Cは結果の型です。
      *
      * 関数が1つのパラメータしか持たない場合は、括弧を省略することができます。
      */
//    A => B
    /**
    *
    *  ---
    */
  }

  object chapter522 {

    /**
      * ### 5.2.2 Function literals
      * また、Scalaには新しい関数を作るための関数リテラル構文があります。ここでは，いくつかの関数リテラルの例を示します．
      */
    val sayHi = () => "Hi!"
    // sayHi: () => String = <function0>

    sayHi()
    // res1: String = Hi!

    val add1 = (x: Int) => x + 1
    // add1: Int => Int = <function1>

    add1(10)
    // res2: Int = 11

    val sum = (x: Int, y: Int) => x + y
    // sum: (Int, Int) => Int = <function2>

    sum(10, 20)
    // res3: Int = 30

    /**
      * 引数の型がわかっているコードでは、型のアノテーションをやめて、Scala に推論させることもできます。※10
      * 関数の結果の型を宣言するシンタックスはなく，通常は推論されますが，これを行う必要がある場合には，関数の本体式に型を記述します．
      */
    (x: Int) => (x + 1): Int

    /**
      * ---
      *
      * - ※10
      * ただし、引数リストを括弧で囲むことができるのは、1引数の関数の場合だけです。
      *
      * ---
      */

    /**
      * ---
      *
      * **関数リテラル構文：Function Literal Syntax**
      * 関数リテラルを宣言する構文は次のとおりです。
      */
//    (parameter: type, ...) => expression
    /**
    * ここで、 - オプションのパラメータは、関数のパラメータに与えられた名前です。- 型は、関数のパラメータの型です。 - 式は、関数の結果を決定します。
    *
    * ---
    */
  }

  object chapter523 {

    /**
      * ### 5.2.3 Exercises
      */
    object chapter5231a {

      /**
        * #### 5.2.3.1 A Better Abstraction
        * 私たちは、sum、length、productを抽象化して、次のようにスケッチしました。
        */
      //      def abstraction(end: Int, f: ???): Int =
      //        this match {
      //          case End          => end
      //          case Pair(hd, tl) => f(hd, tl.abstraction(end, f))
      //        }
      /**
        *
        * この関数の名前を、通常知られている名前であるfoldに変更し、実装を終了します。
        *
        * ①
        */
      //      sealed trait IntList {
      //        def fold(end: Int, f: (Int, Int) => Int): Int =
      //          this match {
      //            case End          => end
      //            case Pair(hd, tl) => f(hd, tl.fold(end, f))
      //          }
      //      }
      //
      //      case object End extends IntList
      //      case class Pair(hd: Int, tl: IntList) extends IntList

      /**
        * 模範
        */
      /**
        * あなたのfoldメソッドは以下のようになります。
        */
      //      sealed trait IntList {
      //        def fold(end: Int, f: (Int, Int) => Int): Int =
      //          this match {
      //            case End => end
      //            case Pair(hd, tl) => f(hd, tl.fold(end, f))
      //          }
      //
      //        // other methods...
      //      }
      //      case object End extends IntList
      //      final case class Pair(head: Int, tail: IntList) extends IntList
    }

    object chapter5231b {

      /**
        * ここで、sum、length、productをfoldの観点から再実装します。
        *
        * ②
        */
      sealed trait IntList {
        def fold(end: Int, f: (Int, Int) => Int): Int = {
          this match {
            case End          => end
            case Pair(hd, tl) =>
//              println(s"-- ${f(hd, tl.fold(end, f))}")
              f(hd, tl.fold(end, f))
          }
        }

        def sum: Int = fold(0, (x: Int, y: Int) => x + y)
        def length: Int = fold(0, (x: Int, y: Int) => 1 + y)
        def product: Int = fold(0, (x: Int, y: Int) => x * y)
      }

      case object End extends IntList
      case class Pair(hd: Int, tl: IntList) extends IntList
    }

    object chapter5231c {

      /**
        * パターンマッチングやポリモーフィックを使って実装されたメソッドは、fold で書き換えた方が便利なのでしょうか？
        * このことは、fold の最適な使い方について何を教えてくれますか？
        *
        * ③
        */
      // 同じようなコードが減るから良いと思う

      /**
        * 模範
        */
      /**
      * ポリモーフィック実装でfoldを使用すると、多くの重複が発生します。
      * foldを使用しないポリモーフィック実装は、より簡単に記述できます。
      * パターン・マッチングの実装では、パターン・マッチングの重複を取り除くことで、fold の恩恵を受けています。
      *
      * **一般的にfoldは、クラス外のユーザーにとっては良いインターフェースになりますが、クラス内での使用には必ずしも適していません。**
      */
    }

    object chapter5231d {

      /**
        * なぜ double メソッドを fold で書けないのでしょうか？
        * fold に何らかの変更を加えれば、実現可能なのでしょうか？
        *
        * ④
        */
//      foldがIntしか返せない作りだから

      /**
        * 模範
        */
      /**
        * fold は Int を返し、double は IntList を返すからです。
        * しかし、double の一般的な構造は fold に取り込まれています。このことは、両者を並べてみるとよくわかります。
        */
//      def double: IntList =
//        this match {
//          case End          => End
//          case Pair(hd, tl) => Pair(hd * 2, tl.double)
//        }
//
//      def fold(end: Int, f: (Int, Int) => Int): Int =
//        this match {
//          case End          => end
//          case Pair(hd, tl) => f(hd, tl.fold(end, f))
//        }
      /**
      * もし、foldの型をIntから何らかの一般的な型に一般化できれば、doubleと書くことができます。
      * 読者の皆さん、それが次のテーマなのです。
      */
    }

    object chapter5231e {

      /**
        * foldの一般化されたバージョンを実装し、それに基づいてdoubleを書き換えます。
        *
        * ⑤
        */
      // 型推論が合わせられなかった
//      sealed trait IntList[A] {
//        def fold[A](end: A, f: (A, A) => A): A = {
//          this match {
//            case End()        => end
//            case Pair(hd, tl) =>
//              //              println(s"-- ${f(hd, tl.fold(end, f))}")
//              f(hd, tl.fold(end, f))
//          }
//        }
//
//        def sum: Int = fold(0, (x: Int, y: Int) => x + y)
////        def double = Pair(fold(0, (x: Int, y: Int) => x * 2))
////        case Pair(hd, tl) => Pair(hd * 2, tl.double)
//
//      }
//
//      case class End[A]() extends IntList[A]
//      case class Pair[A](hd: A, tl: IntList[A]) extends IntList[A]

      // 模範見て修正
//      sealed trait IntList {
//        def fold[A](end: A, f: (Int, A) => A): A = {
//          this match {
//            case End()        => end
//            case Pair(hd, tl) =>
//              //              println(s"-- ${f(hd, tl.fold(end, f))}")
//              f(hd, tl.fold(end, f))
//          }
//        }
//
//        def sum: Int = fold(0, (x: Int, y: Int) => x + y)
////        def double = fold(0, (x: Int, y: Int) => Pair(x * 2, y))
//        def double: IntList =
//          fold(End(), (x, y) => Pair(x * 2, y))
////          fold[IntList](End(), (hd, tl) => Pair(hd * 2, tl))
//        //        case Pair(hd, tl) => Pair(hd * 2, tl.double)
//      }
//
//      case class End() extends IntList
//      case class Pair(hd: Int, tl: IntList) extends IntList

      /**
        * 模範
        */
      /**
        * リターンタイプのフォールドを一般化したいと思います。我々の出発点は
        */
//      def fold(end: Int, f: (Int, Int) => Int): Int
      /**
        * リターンタイプを交換して辿っていくと、次のようになります。
        */
//      def fold[A](end: A, f: (Int, A) => A): A
      /**
        * ここでは、メソッドにジェネリックタイプを使用して、変化する戻り値の型を捉えています。
        * これでdoubleを実装することができます。
        * これを実装しようとすると、型推論が失敗することがわかりますので、少し助けてあげなければなりません。
        */
      sealed trait IntList {
        def fold[A](end: A, f: (Int, A) => A): A =
          this match {
            case End          => end
            case Pair(hd, tl) => f(hd, tl.fold(end, f))
          }
        def length: Int =
          fold[Int](0, (_, tl) => 1 + tl)
        def product: Int =
          fold[Int](1, (hd, tl) => hd * tl)
        def sum: Int =
          fold[Int](0, (hd, tl) => hd + tl)
        def double: IntList =
          fold[IntList](End, (hd, tl) => Pair(hd * 2, tl))
      }
      case object End extends IntList
      final case class Pair(head: Int, tail: IntList) extends IntList
    }
  }
  def main(args: Array[String]): Unit = {
    println("chapter52")

//    import chapter523.chapter5231b._
//
//    val example = Pair(1, Pair(2, Pair(3, End)))
//    println(example.sum)
//    println(example.product)
//    println(example.length)

    import chapter523.chapter5231e._

    val example = Pair(1, Pair(2, Pair(3, End)))
    println(example.sum)
  }
}
