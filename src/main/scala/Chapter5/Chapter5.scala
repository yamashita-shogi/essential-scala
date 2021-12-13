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

object chapter53 {

  /**
    * ## 5.3 Generic Folds for Generic Data
    * 一般的なデータを持つクラスを定義した場合、そのクラスにはあまり多くのメソッドを実装できないことを見てきました。
    * ユーザーが汎用型を提供するので、その型で動作する関数を提供してもらう必要があります。
    * とはいえ、ジェネリックデータの使い方にはいくつかの共通したパターンがあります。
    * ここでは、IntListのコンテキストでfoldを見てきました。
    * ここでは、foldをさらに詳しく調べ、任意の代数的データ型に対してfoldを実装するためのパターンを学びます。
    */
  object chapter531 {

    /**
      * ### 5.3.1 Fold
      * 前回のfoldでは、整数のリストを扱っていました。今回は、一般的な型のリストに一般化してみましょう。必要なツールはすべて用意されています。
      * まずデータの定義ですが、この例では不変的な和の型パターンを使用するように少し修正しています。
      */
    sealed trait LinkedList[A]
    final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
    final case class End[A]() extends LinkedList[A]

    /**
      * IntListで見たfoldの最後のバージョンは
      */
//    def fold[A](end: A, f: (Int, A) => A): A =
//      this match {
//        case End          => end
//        case Pair(hd, tl) => f(hd, tl.fold(end, f))
//      }

    /**
      * これをLinkedList[A]に拡張するのは、非常に簡単なことです。
      * 単に、Pairのヘッド要素がIntではなくA型であることを考慮すればよいのです。
      */
    // TODO: ここ分からない
    //  Pairのhead int -> A
//    sealed trait LinkedList[A] {
//      def fold[B](end: B, f: (A, B) => B): B =
//        this match {
//          case End() => end
//          case Pair(hd, tl) => f(hd, tl.fold(end, f))
//        }
//    }
//    final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
//    final case class End[A]() extends LinkedList[A]

    /**
      * foldは、構造的再帰を応用したもので、各ケースで適用する関数をユーザーが渡せるようにしたものです。
      * 構造的再帰は、代数的データ型を変換するあらゆる関数を記述するための一般的なパターンであるため、fold はこの一般的なパターンを具体的に実現したものです。
      * つまり、fold は一般的な変換または反復方法です。代数的データ型に対して記述したいあらゆる関数は、fold
      */

    /**
      * ---
      *
      * **Fold Pattern**
      * 代数的データ型 A に対して、fold はそれを汎用型 B に変換します。Fold は構造的な再帰です。
      *
      * * A の各ケースに対して 1 つの関数パラメータ。
      * * 各関数は，関連するクラスのフィールドをパラメータとして取ります。
      * * A が再帰的である場合、再帰的なフィールドを参照する関数パラメータはすべて B 型のパラメータを取ります。
      *
      * Aが再帰的な場合，再帰的なフィールドを参照する関数のパラメータはすべてB型のパラメータを取る．
      * パターンマッチのケースの右辺，あるいは必要に応じて多相性のメソッドは，適切な関数の呼び出しで構成される．
      *
      * ---
      */

    /**
      * このパターンを応用して、上記の折り畳み方法を導き出してみましょう。まずは、基本的なテンプレートから始めます。
      */
//    sealed trait LinkedList[A] {
//      def fold[B](???): B =
//        this match {
//          case End() => ???
//          case Pair(hd, tl) => ???
//        }
//    }
//    final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
//    final case class End[A]() extends LinkedList[A]

    /**
      * これは、構造的な再帰テンプレートに、戻り値の型にジェネリック型のパラメータを追加しただけのものです。
      *
      * 次に、LinkedListの2つのクラスにそれぞれ1つずつ関数を追加します。
      */
//    def fold[B](end: ???, pair: ???): B =
//      this match {
//        case End() => ???
//        case Pair(hd, tl) => ???
//      }

    /**
      * 関数の型のルールから。
      * * endはパラメータを持たず（Endは値を格納しないので），Bを返します。したがって，その型は() => Bとなり，単にB型の値に最適化することができます。
      * * pairは2つのパラメータを持ち，1つはリストの先頭，もう1つは末尾です。先頭の引数はA型で、末尾は再帰的なのでB型です。したがって、最終的な型は(A, B) => Bとなります。
      *
      * 代入すると次のようになります。
      */
//    def fold[B](end: B, pair: (A, B) => B): B =
//      this match {
//        case End() => end
//        case Pair(hd, tl) => pair(hd, tl.fold(end, pair))
//      }
  }
  object chapter532 {

    /**
      * ### 5.3.2 Working With Functions
      * Scalaでは、関数や関数を受け取るメソッド（高階のメソッドと呼ばれる）を扱う際に、いくつかのコツがあります。ここでは、以下のことを見ていきます。
      *
      * 1. 関数を書くためのコンパクトな構文。
      * 2. メソッドを関数に変換する。
      * 3. 型推論を支援する高階のメソッドの書き方
      */
    object chapter5321 {

      /**
        * #### 5.3.2.1 Placeholder syntax
        * 非常にシンプルな状況では、プレースホルダー構文と呼ばれる極端な省略法を使ってインライン関数を書くことができます。これは次のようなものです。
        */
//      ((_: Int) * 2)
//      // res: Int => Int = <function1>
      /**
        * `(_: Int) * 2` は、コンパイラによって `(a: Int) => a * 2` と展開されます。
        * コンパイラが型を推論できる場合にのみプレースホルダ構文を使用する方がより慣用的です。以下にいくつかの例を示します。
        */
//      _ + _     // expands to `(a, b) => a + b`
//      foo(_)    // expands to `(a) => foo(a)`
//      foo(_, b) // expands to `(a) => foo(a, b)`
//      _(foo)    // expands to `(a) => a(foo)`
//      // and so on...

      /**
      * プレースホルダー構文は、簡潔で素晴らしいものですが、大きな式では混乱を招くため、非常に小さな関数にのみ使用するようにしてください。
      */
    }
  }

  object chapter533 {

    /**
      * ### 5.3.3 Converting methods to functions
      * Scalaには、このセクションに直接関係するもう一つの機能があります。
      * それは、メソッドの呼び出しを関数に変換する機能です。これはプレースホルダー構文と密接な関係があります。
      */
    // TODO: ここどういう意味かな
    //  プレースホルダーを使って関数を関数化している感じ
    object Sum {
      def sum(x: Int, y: Int) = x + y
    }

    // NG
//    Sum.sum
//    // <console>:23: error: missing argument list for method sum in object Sum
//    // Unapplied methods are only converted to functions when a function type is expected.
//    // You can make this conversion explicit by writing `sum _` or `sum(_,_)` instead of `sum`.
//    //        Sum.sum
//    //            ^

    // OK
//    (Sum.sum _)
//    // res1: (Int, Int) => Int = <function2>

    /**
      * Scalaが関数を必要としていると推察した場合、アンダースコアをやめて単にメソッド名を書けば、コンパイラがそのメソッドを自動的に関数に昇格させます。
      */
    object MathStuff {
      def add1(num: Int) = num + 1
    }
//    Counter(2).adjust(MathStuff.add1)
//    // res2: Counter = Counter(3)

    object chapter5331 {

      /**
        * #### 5.3.3.1 Multiple Parameter Lists
        * Scalaのメソッドは、実際に複数のパラメータリストを持つことができます。
        * このようなメソッドは，通常のメソッドと同じように動作しますが，各パラメータリストを別々にブラケットに入れなければなりません。
        */
      def example(x: Int)(y: Int) = x + y
      // example: (x: Int)(y: Int)Int

      example(1)(2)
      // res3: Int = 3

      /**
        * 複数のパラメータリストには、2つの関連した用途があります。
        * それは、関数をインラインで定義する際に見た目を良くすることと、型推論を助けることです。
        *
        * 前者は、コードブロックのような関数を書くことができるというものです。例えば、foldを次のように定義すると
        */
      // 前と一緒
//      def fold[B](end: B)(pair: (A, B) => B): B =
//        this match {
//          case End() => end
//          case Pair(hd, tl) => pair(hd, tl.fold(end, pair))
//        }

      /**
        * このように呼ぶことができます。
        */
//      fold(0){ (total, elt) => total + elt }
      /**
        * ↑よりも少し読みやすくなっています。
        */
      // TODO: どっちが読みやすいっていってる？ -> 文章的にパラメータが2個に分かれているので、たぶん↑が読みやすいって意味かな？
//      fold(0, (total, elt) => total + elt)

      /**
        * さらに重要なのは、型推論を容易にするために、複数のパラメータリストを使用することです。
        * Scala の型推論アルゴリズムは，あるパラメータに対して推論された型を，同じリストの別のパラメータに使用することはできません．
        * 例えば，次のようなシグネチャを持つfoldがあるとします．
        */
//      def fold[B](end: B, pair: (A, B) => B): B

      /**
        * ScalaがendにBを推論した場合，この推論された型をpairのBに使うことはできないので，pairの型宣言を書かなければならないことが多い．
        * しかし，Scala は，あるパラメータリストで推論された型を別のパラメータリストで使用することができます．つまり，foldを次のように書くと
        */
//      def fold[B](end: B)(pair: (A, B) => B): B

      /**
      * この場合、endからBを推論することで（通常は簡単ですが）、型ペアを推論する際にBを使用することができます。
      * これにより、型宣言の数が減り、開発プロセスがスムーズになります。
      */
    }
  }

  object chapter534 {

    /**
      * ### 5.3.4 Exercises
      */
    object chapter5341a {

      /**
        * #### 5.3.4.1 Tree
        * 二分木は次のように定義できる。
        * A型のTreeとは、左右のTreeを持つNode、またはA型の要素を持つLeafのことです。
        * この代数的なデータ型とfoldメソッドを実装します．
        */
      // fold分からなかった
//      sealed trait Tree[A] {
//        def fold[B](???): B =
//          this match {
//            case End() => ???
//            case Pair(hd, tl) => ???
//          }
//      }
//      final case class Node[A](l: Tree[A], r: Tree[A]) extends Tree[A]
//      final case class Leaf[A](elt: A) extends Tree[A]

      /**
        * 模範
        */
      /**
        * これもリストと同様に再帰的なデータ型です。パターンに従えば問題ありません。
        */
      sealed trait Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B
      }
      final case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B =
          node(left.fold(node, leaf), right.fold(node, leaf))
      }
      final case class Leaf[A](value: A) extends Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B =
          leaf(value)
      }
    }
    object chapter5341b {
      sealed trait Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B
      }
      final case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B =
          node(left.fold(node, leaf), right.fold(node, leaf))
      }
      final case class Leaf[A](value: A) extends Tree[A] {
        def fold[B](node: (B, B) => B, leaf: A => B): B =
          leaf(value)
      }

      /**
        * fold を使って、以下の Tree を String に変換します。
        */
      val tree: Tree[String] =
        Node(Node(Leaf("To"), Leaf("iterate")),
             Node(Node(Leaf("is"), Leaf("human,")),
                  Node(Leaf("to"), Node(Leaf("recurse"), Leaf("divine")))))

      /**
        * 文字列の追加には + メソッドを使用します。
        */
//      tree.fold((_, _), _.toString)

      /**
        * 模範
        */
      /**
        * fold用の汎用型変数をインスタンス化する必要があることに注意してください。この場合、型推論は失敗します。
        */
      tree.fold[String]((a, b) => a + " " + b, str => str)
    }
  }
}

object chapter54 {

  /**
    * ## 5.4 Modelling Data with Generic Types
    * このセクションでは、データをモデル化する際にジェネリック型が与えてくれるさらなる力を見ていきます。
    * ジェネリック型を使えば、一般的なsum型やproduct型を実装できるだけでなく、オプション値のような他の有用な抽象化をモデル化することができます。
    */
  object chapter541 {

    /**
      * ### 5.4.1 Generic Product Types
      * ここでは、ジェネリックを使ってproductタイプをモデル化してみましょう。
      * 例えば、IntとString、BooleanとDoubleなど、2つの値を返すメソッドを考えてみましょう。
      */
//    def intAndString: ??? = // ...
//
//    def booleanAndDouble: ??? = // ...

    /**
      * 問題は，戻り値の型として何を使うかです．
      * 型パラメータのない通常のクラスを使い，通常の代数的なデータ型パターンを使用することもできますが，そうすると，戻り値の型の組み合わせごとにクラスのバージョンを1つずつ実装しなければなりません．
      */
//    case class IntAndString(intValue: Int, stringValue: String)
//
//    def intAndString: IntAndString = // ...
//
//    case class BooleanAndDouble(booleanValue: Boolean, doubleValue: Double)
//
//    def booleanAndDouble: BooleanAndDouble = // ...

    /**
      * その答えは、ジェネリックを使用して、両方のリターンタイプに関連するデータを含む製品タイプ（例えばペア）を作成することです。
      */
//    def intAndString: Pair[Int, String] = // ...
//
//    def booleanAndDouble: Pair[Boolean, Double] = // ...

    /**
      * ジェネリックは、継承ではなく集約に頼ってproductタイプを定義するという異なるアプローチを提供します。
      */
    object chapter5411 {

      /**
        * #### 5.4.1.1 Exercise: Pairs
        * 先ほどの Pair クラスを実装します。このクラスは、2つの値 1と2 を格納し、両方の引数で汎用的でなければなりません。使い方の例です。
        */
//      val pair = Pair[String, Int]("hi", 2)
//      // pair: Pair[String,Int] = Pair(hi,2)
//
//      pair.one
//      // res0: String = hi
//
//      pair.two
//      // res1: Int = 2
      // 自作
//      case class Pair[A, B](one: A, two: B)

//      // 確認コード
//      val pair = Pair[String, Int]("hi", 2)
//      // pair: Pair[String,Int] = Pair(hi,2)
//
//      pair.one
//      // res0: String = hi
//
//      pair.two
//      // res1: Int = 2

      /**
        * 模範
        */
      /**
        * 1つのタイプのパラメータが良ければ、2つのタイプのパラメータがより良い。
        */
      case class Pair[A, B](one: A, two: B)

      /**
        * これは以前に見たproductタイプのパターンと同じですが、ジェネリックタイプを導入しています。
        *
        * なお，Pairsを構築する際には，常に型パラメータを指定する必要はありません．コンパイラは可能な限り，通常通り型を推論しようとします．
        */
      val pair = Pair("hi", 2)
      // pair: Pair[String,Int] = Pair(hi,2)
    }
  }

  object chapter542 {

    /**
      * ### 5.4.2 Tuples
      * タプルとは、ペアをより多くの項に一般化したものです。
      * Scalaには、最大22個の要素を持つジェネリックなタプル型が組み込まれており、タプルを作成するための特別な構文も用意されています。
      * これらのクラスを使えば，ほぼすべての項の間のあらゆる種類の「これとこれ」の関係を表現することができます。
      *
      * このクラスはTuple1[A]からTuple22[A, B, C, ...]と呼ばれていますが，sugared11形式(A, B, C, ...)で書くこともできます．
      * 例えば、以下のようになります。
      */
    Tuple2("hi", 1) // unsugared syntax
    // res2: (String, Int) = (hi,1)

    ("hi", 1) // sugared syntax
    // res3: (String, Int) = (hi,1)

    ("hi", 1, true)
    // res4: (String, Int, Boolean) = (hi,1,true)

    /**
      * 同じ構文で、タプルをパラメータとして受け取るメソッドを定義することができます。
      */
    def tuplized[A, B](in: (A, B)) = in._1
    // tuplized: [A, B](in: (A, B))A

    tuplized(("a", 1))
    // res5: String = a

    /**
      * また、次のようにタプルをパターンマッチすることもできます。
      */
    (1, "a") match {
      case (a, b) => a + b
    }
    // res6: String = 1a

    /**
      * タプルを分解するにはパターンマッチングが自然ですが、各クラスには_1、_2などのフィールドがあります。
      */
    val x = (1, "b", true)
    // x: (Int, String, Boolean) = (1,b,true)

    x._1
    // res7: Int = 1

    x._3
    // res8: Boolean = true
  }

  object chapter543 {

    /**
      * ### 5.4.3 Generic Sum Types
      * 次に、ジェネリックを使ってsum型をモデル化することを考えてみましょう。
      * ここでも、代数的データ型パターンを使って、共通する部分をスーパータイプに分解して実装しました。
      * ジェネリクスを使うと、このパターンを抽象化して、...まあ...ジェネリックな実装を提供することができます。
      *
      * あるメソッドを考えてみましょう。このメソッドは、パラメータの値に応じて、2つの型のうちの1つを返します。
      */
    def intOrString(input: Boolean) =
      if (input == true) 123 else "abc"
    // intOrString: (input: Boolean)Any

    /**
      * コンパイラは結果の型をAnyと推定するため、このメソッドを上記のように単純に書くことはできません。
      * 代わりに、論理和を明示的に表す新しい型を導入する必要があります。
      */
//    def intOrString(input: Boolean): Sum[Int, String] =
//      if (input == true) {
//        Left[Int, String](123)
//      } else {
//        Right[Int, String]("abc")
//      }
//    // intOrString: (input: Boolean)sum.Sum[Int,String]
    /**
      * どうやってSumを実装するのか？これまでに見てきたパターンに、ジェネリック型を加えたものを使えばいいのです。
      */
    object chapter5431 {

      /**
        * #### 5.4.3.1 Exercise: Generic Sum Type
        * 2つのサブタイプ Left と Right を持つtrait Sum[A, B] を実装します。
        * LeftとRightが2つの異なる型の値を包むことができるように、型パラメータを作成してください。
        *
        * ヒント：3つの型すべてに両方の型パラメータを付ける必要があります。使用例です。
        */
//      Left[Int, String](1).value
//      // res9: Int = 1
//
//      Right[Int, String]("foo").value
//      // res10: String = foo
//
//      val sum: Sum[Int, String] = Right("foo")
//      // sum: sum.Sum[Int,String] = Right(foo)
//
//      sum match {
//        case Left(x)  => x.toString
//        case Right(x) => x
//      }
//      // res11: String = foo

      /**
        * 模範
        */
      /**
        * このコードは、私たちの不変的なジェネリック・サム・タイプ・パターンに、別のタイプ・パラメータを加えたものです。
        */
      sealed trait Sum[A, B]
      final case class Left[A, B](value: A) extends Sum[A, B]
      final case class Right[A, B](value: B) extends Sum[A, B]
      /**
      * Scalaの標準ライブラリには、2つのケースに対応する汎用のsum型Eitherがありますが、それ以上のケースに対応する型はありません。
      */
    }
  }

  object chapter544 {

    /**
      * ### 5.4.4 Generic Optional Values
      * 多くの式では、値が出る場合と出ない場合があります。
      * 例えば、ハッシュテーブル（連想配列）の要素をキーで検索しても、そこに値がない場合もあります。
      * Webサービスに問い合わせをしても、そのサービスがダウンしていて返事が来ないかもしれません。
      * ファイルを探していても、そのファイルが削除されているかもしれません。このようなオプション値の状況をモデル化する方法はいくつかあります。
      * 例外を発生させたり、値がない場合にnullを返したりすることができます。これらの方法の欠点は、型システムに何の情報もエンコードされていないことです。
      *
      * 一般的に，私たちは堅牢なプログラムを書きたいと思っています。
      * そして，Scalaでは型システムを利用して，プログラムに維持させたいプロパティをエンコードしようとしています。
      * 一般的なプロパティの1つに「エラーを正しく処理する」というものがあります。
      * 型システムでオプションの値をエンコードすることができれば，コンパイラは値が利用できない場合を考慮するように強制し，コードの堅牢性を高めることができます。
      */
    object chapter5441 {

      /**
        * #### 5.4.4.1 Exercise: Maybe that Was a Mistake
        * Aを含むFullと値を含まないEmptyの2つのサブタイプを持つジェネリックタイプAのMaybeというジェネリック形質を作成します。使い方の例。
        */
//      val perhaps: Maybe[Int] = Empty[Int]
//
//      val perhaps: Maybe[Int] = Full(1)

      // 自作
//      sealed trait Maybe[A]
//      final case class Full[A](value: A) extends Maybe[A]
//      final case class Empty[A](value: A) extends Maybe[A]

      /**
        * 模範
        */
      /**
        * 私たちの不変的なジェネリック・サム・タイプ・パターンを適用して、次のようになります。
        */
      sealed trait Maybe[A]
      final case class Full[A](value: A) extends Maybe[A]
      final case class Empty[A]() extends Maybe[A]
    }
  }

  object chapter545 {

    /**
    * ### 5.4.5 Take Home Points
    * このセクションでは、ジェネリクスを使って、sum型、product型、オプション値をモデル化しました。
    *
    * これらの抽象化はScalaのコードでよく使われており、Scalaの標準ライブラリにも実装があります。
    * sum型はEither、productはタプル、オプション値はOptionでモデル化されています。
    */
  }

  object chapter546 {

    /**
      * ### 5.4.6 Exercises
      */
    object chapter5461 {

      /**
        * #### 5.4.6.1 Generics versus Traits
        * sumの型とproductの型は、ほとんどすべての種類のデータ構造をモデル化することができる一般的な概念です。
        * これまで、これらの型を記述する方法として、traitsとgenericsの2つを見てきました。どのような場合にそれぞれの使用を検討すべきでしょうか？
        */

      /**
        * 模範
        */
      /**
      * 最終的には私たちの判断に委ねられます。
      * チームによって採用するプログラミングスタイルは異なります。
      * しかし、私たちはそれぞれのアプローチの特性に注目して選択を行います。
      *
      * 継承をベースとしたアプローチである「ポートレート」と「クラス」は、特定の型と名前を持つ永続的なデータ構造を作ることができます。
      * すべてのフィールドやメソッドに名前を付け、各クラスに使用目的に応じたコードを実装することができます。
      * そのため、継承は、コードベースの多くの領域で再利用されるプログラムの重要な側面をモデル化するのに適しています。
      *
      * 一般的なデータ構造（タプル、オプション、イーサーなど）は、非常に広範で汎用的です。
      * Scalaの標準ライブラリには、コード内のデータ間の関係を素早くモデル化するために使用できる定義済みのクラスが幅広く用意されています。
      * これらのクラスは、独自の型を定義するとコードベースに不必要な冗長性が生じるような、迅速で一回限りのデータ操作に適しています。
      */
    }
    object chapter5462 {

      /**
        * ここでは、オプションデータをモデル化するためのsum型を実装しました。
        */
//      sealed trait Maybe[A]
//      final case class Full[A](value: A) extends Maybe[A]
//      final case class Empty[A]() extends Maybe[A]
      /**
        * このタイプにfoldを実装します。
        */
//      sealed trait Maybe[A]
//      final case class Full[A](value: A) extends Maybe[A]
//      final case class Empty[A]() extends Maybe[A]

      /**
        * 模範
        */
      /**
        * コードは、LinkedListの実装と非常によく似ています。私のソリューションでは、ベースとなる trait でパターン・マッチングを選択しています。
        */
      sealed trait Maybe[A] {
        def fold[B](full: A => B, empty: B): B =
          this match {
            case Full(v) => full(v)
            case Empty() => empty
          }
      }
      final case class Full[A](value: A) extends Maybe[A]
      final case class Empty[A]() extends Maybe[A]
    }

    object chapter5463 {

      /**
        * #### 5.4.6.3 Folding Sum
        * ここでは、汎用的なsum型を実装しました。
        */
//      sealed trait Sum[A, B]
//      final case class Left[A, B](value: A) extends Sum[A, B]
//      final case class Right[A, B](value: B) extends Sum[A, B]
      /**
        * Sumのためのfoldを作る
        */
//      sealed trait Sum[A, B] {
//        def fold[B](l: B => B, r: A => B): B
//      }
//      final case class Left[A, B](value: A) extends Sum[A, B] {
//        def fold[B](node: (B, B) => B, leaf: A => B): B =
//          node(value.fold(node, leaf), right.fold(node, leaf))
//      }
//      final case class Right[A, B](value: B) extends Sum[A, B] {
//        def fold[B](node: (B, B) => B, leaf: A => B): B =
//          leaf(value)
//      }

      /**
        * 模範
        */
      sealed trait Sum[A, B] {
        def fold[C](left: A => C, right: B => C): C =
          this match {
            case Left(a)  => left(a)
            case Right(b) => right(b)
          }
      }
      final case class Left[A, B](value: A) extends Sum[A, B]
      final case class Right[A, B](value: B) extends Sum[A, B]
    }
  }
}

object chapter55 {

  /**
    * ## 5.5 Sequencing Computation
    * ここまでで、一般的なデータと代数的なデータ型に対する折り畳みを習得しました。
    * ここでは、他の一般的な計算パターンとして、1）代数的なデータ型ではfoldよりも便利に使えることが多く、2）foldに対応していない特定のデータ型でも実装可能なものを見ていきます。
    * これらの方法は、map および flatMap と呼ばれています。
    */
  object chapter551 {

    /**
      * ### 5.5.1 Map
      * IntのユーザーIDのリストがあり、ユーザーIDを与えるとUserレコードを返す関数があるとします。
      * リストに含まれるすべてのIDのユーザーレコードのリストを取得したいとします。
      * 型としては、List[Int]とInt => Userの関数があり、List[User]を取得したいとします。
      *
      * データベースから読み込まれたユーザーレコードを表すオプションの値と、そのユーザーの最新の注文を読み込む関数があるとします。
      * レコードがあれば、そのユーザの直近の注文を検索したいとします。
      * つまり、Maybe[User]とUser => Orderという関数があり、Maybe[Order]が欲しいということです。
      *
      * エラーメッセージや完了した注文を表すsum型があるとします。
      * 完了した注文があれば、その注文の合計値を取得したいとします。
      * つまり、Sum[String, Order]と関数 Order => Double があり、Sum[String, Double]が欲しいのです。
      *
      * これらに共通しているのは，F[A]という型と，A => Bという関数があり，結果F[B]が欲しいということです．この操作を行うメソッドをmapといいます。
      *
      * それでは、LinkedListにmapを実装してみましょう。まず、型の概要を説明し、一般的な構造再帰の骨格を追加します。
      */
//    sealed trait LinkedList[A] {
//      def map[B](fn: A => B): LinkedList[B] =
//        this match {
//          case Pair(hd, tl) => ???
//          case End()        => ???
//        }
//    }
//    final case class Pair[A](head: A, tail: LinkedList[A]) extends LinkedList[A]
//    final case class End[A]() extends LinkedList[A]

    /**
      * 構造的再帰パターンを使用できることは、fold（構造的再帰パターンを抽象化しただけのもの）が代数的データ型の普遍的なイテレータであることがわかっているからです。
      * したがって
      * * Pairの場合、LinkedList[B]を返すためにheadとtailを結合しなければならず、tailで再帰する必要があることもわかっています（型でわかる）。次のように書くことができます。
      */
//    case Pair(hd, tl) => {
//      val newTail: LinkedList[B] = tail.map(fn)
//      // newTailとheadを組み合わせてLinkedList[B]を作成する。
//    }

    /**
      * fnを使ってheadをBに変換し、newTailとBからより大きなリストを作成することで、最終的な解決策を得ることができます。
      */
//    case Pair(hd, tl) => Pair(fn(hd), tl.map(fn))
    /**
      * * Endの場合は、関数に適用するAの値がありません。返すことができるのは、Endだけです。
      *
      * したがって、完全なソリューションは
      */
    sealed trait LinkedList[A] {
      def map[B](fn: A => B): LinkedList[B] =
        this match {
          case Pair(hd, tl) => Pair(fn(hd), tl.map(fn))
          case End()        => End[B]()
        }
    }
    case class Pair[A](hd: A, tl: LinkedList[A]) extends LinkedList[A]
    case class End[A]() extends LinkedList[A]

    /**
    * タイプとパターンを使い分けることで、解決の糸口が見えてきました。
    */
  }

  object chapter552 {

    /**
      * ### 5.5.2 FlatMap
      * では、次のような例を想像してみてください。
      *
      * * ユーザーのリストがあり、そのユーザーのすべての注文のリストを取得したいとします。つまり、LinkedList[User]と、User => LinkedList[Order]という関数があり、LinkedList[Order]が必要です。
      * * データベースから読み込まれたユーザを表すオプションの値があり、そのユーザの直近の注文を検索したいとします（これもオプションの値です）。つまり、Maybe[User]とUser => Maybe[Order]がありますが、Maybe[Order]が欲しいのです。
      * * エラーメッセージまたは注文を保持するsum型があり、ユーザーに請求書をメールで送信したいとします。メール送信は、エラーメッセージかメッセージ ID を返します。つまり、Sum[String, Order]と関数 Order => Sum[String, Id]があり、Sum[String, Id]を求めています。
      *
      * これらに共通しているのは、F[A]という型と、A => F[B]という関数があり、結果F[B]が欲しいということです。この操作を行うメソッドをflatMapと呼びます。
      *
      * それでは、MaybeのflatMapを実装してみましょう（LinkedListのflatMapを実装するにはappendメソッドが必要です）。まず、型の概要を説明します。
      */
//    sealed trait Maybe[A] {
//      def flatMap[B](fn: A => Maybe[B]): Maybe[B] = ???
//    }
//    final case class Full[A](value: A) extends Maybe[A]
//    final case class Empty[A]() extends Maybe[A]

    /**
      * 先ほどと同じパターンで、構造的な再帰を行い、型を参考にしてメソッド本体を埋めていきます。
      */
    sealed trait Maybe[A] {
      def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
        this match {
          case Full(v) => fn(v)
          case Empty() => Empty[B]()
        }
    }
    final case class Full[A](value: A) extends Maybe[A]
    final case class Empty[A]() extends Maybe[A]
  }

  object chapter553 {

    /**
      * ### 5.5.3 Functors and Monads
      * F[A]のようにMapメソッドを持つ型をファンクタと呼ぶ。ファンクタがflatMapメソッドを持っている場合はモナドと呼ばれます※12
      */
    /**
      * ---
      *
      * - ※12
      * ファンクタやモナドにはもう少し多くの機能があります。
      * モナドには、通常 point と呼ばれるコンストラクタが必要であり、map や flatMap の操作が従わなければならないいくつかの代数的な法則があります。
      * モナドについての詳しい情報は，オンラインで検索すればすぐに見つかるでしょうし，『Scala with Cats』という本の中でも詳しく説明されています。
      *
      * ---
      */
    /**
      * mapやflatMapの最も身近な応用例はリストのようなコレクションクラスですが、より大きな意味を持つのは計算の順序付けです。
      * 例えば、失敗する可能性のある計算がいくつかあるとします。例えば
      */
    import Chapter5.chapter55.chapter552._

    def mightFail1: Maybe[Int] =
      Full(1)

    def mightFail2: Maybe[Int] =
      Full(2)

    def mightFail3: Maybe[Int] =
      Empty() // This one failed

    /**
      * これらの計算を次々と実行していきたいと思います。
      * どれか一つでも失敗すれば、すべての計算が失敗します。そうでなければ、得られたすべての数値を加算します。
      * これは、flatMapを使って次のように行うことができます。
      */
    mightFail1 flatMap { x =>
      mightFail2 flatMap { y =>
        mightFail3 flatMap { z =>
          Full(x + y + z)
        }
      }
    }

    /**
      * その結果、Emptyになりました。mightFail3を削除して、残るのは
      */
    mightFail1 flatMap { x =>
      mightFail2 flatMap { y =>
        Full(x + y)
      }
    }

    /**
    * と入力すると、計算は成功し、Full(3)が得られます。
    *
    * 一般的な考え方として、モナドはある文脈の中で値を表現します。
    * コンテキストは使用しているモナドによって異なります。これまでの例では、以下のような文脈がありました。
    *
    * * データベースから値を取得するときに得られるような、オプションの値。
    * * エラーメッセージや計算中の値を表すような、値の合計
    * * 値のリスト。
    *
    * mapは、コンテキストを維持したまま、コンテキスト内の値を新しい値に変換したいときに使います。
    * flatMapは、値を変換して新しいコンテキストを提供したいときに使います。
    */
  }

  object chapter554 {

    /**
      * ### 5.5.4 Exercises
      */
    object chapter5541 {

      /**
        * #### 5.5.4.1 Mapping Lists
        */
      /**
        * 次のリストがある場合
        */
//      val list: LinkedList[Int] = Pair(1, Pair(2, Pair(3, End())))
      /**
        * * リストのすべての要素を2倍する。
        * * リストのすべての要素に1を加える。
        * * リストのすべての要素を3で割る。
        */
      import Chapter5.chapter55.chapter551._
      val list: LinkedList[Int] = Pair(1, Pair(2, Pair(3, End())))

      // * リストのすべての要素を2倍する。
      list.map(_ * 2)

      // * リストのすべての要素に1を加える。
      list.map(_ + 1)

      // * リストのすべての要素を3で割ります。
      list.map(_ / 3)

      /**
        * 模範
        */
      /**
        * これらのエクササイズは、マップを使うことに慣れるためのものです。
        */
      list.map(_ * 2)
      list.map(_ + 1)
      list.map(_ / 3)
    }

    object chapter5542a {

      /**
        * #### 5.5.4.2 Mapping Maybe
        * Maybeのmapを実装。
        */
//      import Chapter5.chapter55.chapter552._
      sealed trait Maybe[A] {
        def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
          this match {
            case Full(v) => fn(v)
            case Empty() => Empty[B]()
          }
        def map[B](fn: A => B): Maybe[B] =
          this match {
            case Full(v) => Full(fn(v))
            case Empty() => Empty[B]()
          }
      }
      final case class Full[A](value: A) extends Maybe[A]
      final case class Empty[A]() extends Maybe[A]

      /**
        * 模範
        */
//      sealed trait Maybe[A] {
//        def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
//          this match {
//            case Full(v) => fn(v)
//            case Empty() => Empty[B]()
//          }
//        def map[B](fn: A => B): Maybe[B] =
//          this match {
//            case Full(v) => Full(fn(v))
//            case Empty() => Empty[B]()
//          }
//      }
//      final case class Full[A](value: A) extends Maybe[A]
//      final case class Empty[A]() extends Maybe[A]
    }

    object chapter5542b {

      /**
        * ボーナスポイントとして、flatMapでマップを実装してください。
        */
//      sealed trait Maybe[A] {
//        def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
//          this match {
//            case Full(v) => fn(v)
//            case Empty() => Empty[B]()
//          }
//        def map[B](fn: A => B): Maybe[B] =
//          this.flatMap(fn)
//
//      }
//      final case class Full[A](value: A) extends Maybe[A]
//      final case class Empty[A]() extends Maybe[A]

      /**
        * 模範
        */
      sealed trait Maybe[A] {
        def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
          this match {
            case Full(v) => fn(v)
            case Empty() => Empty[B]()
          }

        // Emptyの場合はflatMap側にあるから書かなくてOK？
        def map[B](fn: A => B): Maybe[B] =
          flatMap[B](v => Full(fn(v)))
      }
      final case class Full[A](value: A) extends Maybe[A]
      final case class Empty[A]() extends Maybe[A]
    }

    object chapter5543a {

      /**
        * #### 5.5.4.3 Sequencing Computations
        * この演習では、Scala の組み込みクラスである List クラスが flatMap メソッドを持っているので、これを使用します。
        *
        * このリストが与えられた場合
        */
//      val list = List(1, 2, 3)
      /**
        * すべての要素とその否定の両方を含む List[Int] を返します。
        * 順序は重要ではありません。ヒント：要素が与えられると、その要素とその否定を含むリストを作成します
        */

      // 問題文が否定ってなってて分からなかった

      /**
        * 模範
        */
//      list.flatMap(x => List(x, -x))
    }

    object chapter5543b {

      /**
        * このリストがあれば
        */
//      val list: List[Maybe[Int]] = List(Full(3), Full(2), Full(1))
      /**
        * 奇数要素にNoneを含む `List[Maybe[Int]]` を返します。ヒント：もしx % 2 == 0ならば、xは偶数です。
        */
      sealed trait Maybe[A] {
        def flatMap[B](fn: A => Maybe[B]): Maybe[B] =
          this match {
            case Full(v) => fn(v)
            case Empty() => Empty[B]()
          }
        def map[B](fn: A => B): Maybe[B] =
          this match {
            case Full(v) => Full(fn(v))
            case Empty() => Empty[B]()
          }
      }
      final case class Full[A](value: A) extends Maybe[A]
      final case class Empty[A]() extends Maybe[A]

      val list: List[Maybe[Int]] = List(Full(3), Full(2), Full(1))

      val a: Seq[Maybe[Any]] = list.map(x => x.map(xx => if (xx % 2 == 0) xx else None))

      /**
        * 模範
        */
      val b: Seq[Maybe[Int]] = list.map(maybe =>
        maybe.flatMap[Int] { x =>
          if (x % 2 == 0) Full(x) else Empty()
      })
    }

    object chapter5544a {

      /**
        * #### 5.5.4.4 Sum
        * Sum型を思い出してください。
        */
//      sealed trait Sum[A, B] {
//        def fold[C](left: A => C, right: B => C): C =
//          this match {
//            case Left(a)  => left(a)
//            case Right(b) => right(b)
//          }
//      }
//      final case class Left[A, B](value: A) extends Sum[A, B]
//      final case class Right[A, B](value: B) extends Sum[A, B]
      /**
        * 内蔵のEitherとの名前の衝突を防ぐために、LeftケースとRightケースの名前をそれぞれFailureとSuccessに変更します。
        */
//      sealed trait Sum[A, B] {
//        def fold[C](success: A => C, failure: B => C): C =
//          this match {
//            case Success(a) => success(a)
//            case Failure(b) => failure(b)
//          }
//      }
//      final case class Success[A, B](value: A) extends Sum[A, B]
//      final case class Failure[A, B](value: B) extends Sum[A, B]

      /**
        * 模範
        */
      sealed trait Sum[A, B] {
        def fold[C](error: A => C, success: B => C): C =
          this match {
            case Failure(v) => error(v)
            case Success(v) => success(v)
          }
      }
      final case class Failure[A, B](value: A) extends Sum[A, B]
      final case class Success[A, B](value: B) extends Sum[A, B]
    }

    object chapter5544b {

      /**
        * さて、ここからが少し厄介なところです。
        * これから map と flatMap を実装するのですが、ここでも Sum trait のパターンマッチングを使います。
        * mapから始める。
        * mapの一般的な作り方は、F[A]のような型から始めて、関数A => Bを適用してF[B]を得るというものです。
        * しかし、Sumは2つの汎用型パラメータを持っています。
        * F[A]のパターンに合うようにするために、これらのパラメータの1つを固定し、もう1つのパラメータをmapが変更できるようにします。
        * 自然な選択は、Failureに関連する型パラメータを固定し、mapがSuccessを変更できるようにすることだ。
        * これは「fail-fast」な動作に相当します。Sumが失敗した場合、順番に行われる計算は実行されません。
        *
        * 要約すると、mapのタイプは
        */
//      def map[C](f: B => C): Sum[A, C]
      sealed trait Sum[A, B] {
        def fold[C](error: A => C, success: B => C): C =
          this match {
            case Failure(v) => error(v)
            case Success(v) => success(v)
          }

        def map[C](f: B => C): Sum[A, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => Success(f(v))
          }
      }
      final case class Failure[A, B](value: A) extends Sum[A, B]
      final case class Success[A, B](value: B) extends Sum[A, B]

      /**
        * 模範
        */
//      sealed trait Sum[A, B] {
//        def fold[C](error: A => C, success: B => C): C =
//          this match {
//            case Failure(v) => error(v)
//            case Success(v) => success(v)
//          }
//        def map[C](f: B => C): Sum[A, C] =
//          this match {
//            case Failure(v) => Failure(v)
//            case Success(v) => Success(f(v))
//          }
//      }
//      final case class Failure[A, B](value: A) extends Sum[A, B]
//      final case class Success[A, B](value: B) extends Sum[A, B]
    }

    object chapter5544c {

      /**
        * 次に、mapと同じロジックでflatMapを実装します。
        */
      sealed trait Sum[A, B] {
        def fold[C](error: A => C, success: B => C): C =
          this match {
            case Failure(v) => error(v)
            case Success(v) => success(v)
          }
        def map[C](f: B => C): Sum[A, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => Success(f(v))
          }

        def flatMap[C](f: B => Sum[A, C]): Sum[A, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => f(v)
          }
      }
      final case class Failure[A, B](value: A) extends Sum[A, B]
      final case class Success[A, B](value: B) extends Sum[A, B]

      /**
        * 模範
        */
//      sealed trait Sum[A, B] {
//        def fold[C](error: A => C, success: B => C): C =
//          this match {
//            case Failure(v) => error(v)
//            case Success(v) => success(v)
//          }
//        def map[C](f: B => C): Sum[A, C] =
//          this match {
//            case Failure(v) => Failure(v)
//            case Success(v) => Success(f(v))
//          }
//        def flatMap[C](f: B => Sum[A, C]) =
//          this match {
//            case Failure(v) => Failure(v)
//            case Success(v) => f(v)
//          }
//      }
//      final case class Failure[A, B](value: A) extends Sum[A, B]
//      final case class Success[A, B](value: B) extends Sum[A, B]
    }
  }
  def main(args: Array[String]): Unit = {
    println("chapter55")
    import Chapter5.chapter55.chapter554.chapter5541._
    println(list.map(_ * 2))
    println(list.map(_ + 1))
    println(list.map(_ / 3))
  }
}

object chapter56 {

  /**
    * ## 5.6 Variance
    * このセクションでは、バリアンスアノテーションについて説明します。
    * バリアンスアノテーションとは、型パラメータを使って型間のサブクラス関係を制御することができるものです。
    * これを理解するために、不変的な一般和の型パターンをもう一度見てみましょう。
    *
    * Maybe型は次のように定義されています。
    */
//  sealed trait Maybe[A]
//  final case class Full[A](value: A) extends Maybe[A]
//  final case class Empty[A]() extends Maybe[A]

  /**
    * 理想的には、Emptyの使われていないタイプパラメータを削除して、次のように書きたいと思います。
    */
//  sealed trait Maybe[A]
//  final case class Full[A](value: A) extends Maybe[A]
//  case object Empty extends Maybe[???]

  /**
    * オブジェクトは型パラメータを持つことができません。
    * Emptyをオブジェクトにするためには、定義のextends Maybeの部分で具体的な型を指定する必要があります。
    * しかし、どのような型パラメータを使用すればよいのでしょうか。
    * 特定のデータ型にこだわりがなければ、UnitやNothingのようなものを使うことができます。しかし、これでは型エラーになってしまいます。
    */
//  sealed trait Maybe[A]
//  // defined trait Maybe
//
//  final case class Full[A](value: A) extends Maybe[A]
//  // defined class Full
//
//  case object Empty extends Maybe[Nothing]
//  // defined object Empty
//  // warning: previously defined class Empty is not a companion to object Empty.
//  // Companions must be defined together; you may wish to use :paste mode for this.

//  val possible: Maybe[Int] = Empty
  // <console>:14: error: type mismatch;
  //  found   : Empty.type
  //  required: Maybe[Int]
  // Note: Nothing <: Int (and Empty.type <: Maybe[Nothing]), but trait Maybe is invariant in type A.
  // You may wish to define A as +A instead. (SLS 4.5)
  //        val possible: Maybe[Int] = Empty
  //                                   ^

  /**
    * ここでの問題は、EmptyはMaybe[Nothing]であり、Maybe[Nothing]はMaybe[Int]のサブタイプではないということです。
    * この問題を解決するために、バリアンスアノテーションを導入する必要があります。
    */
  object chapter561 {

    /**
      * ### 5.6.1 Invariance, Covariance, and Contravariance
      * ---
      *
      * **バリアンスは難しい**
      * Variance は Scala の型システムの中でも最も厄介なものの一つです．バリアンスの存在を知っておくと便利ですが、アプリケーションコードで使うことはほとんどありません。
      *
      * ---
      */

    /**
    * Foo[A]という型があり、AがBのサブタイプである場合、Foo[A]はFoo[B]のサブタイプなのでしょうか？
    * その答えはFooという型の分散によります。一般的な型の分散は、型のパラメータによってスーパータイプとサブタイプの関係がどのように変化するかを決定します。
    *
    * Foo[T]型は，Tに関して不変です．つまり，Foo[A]型とFoo[B]型は，AとBの関係にかかわらず，無関係です．
    * これは，Scalaのあらゆる汎用型のデフォルトの分散です．
    *
    * Foo[+T]型は，Tの観点から共変であり，AがBのスーパータイプである場合，Foo[A]はFoo[B]のスーパータイプであることを意味します。
    * これらについては次の章で説明します。
    *
    * Foo[-T]型は，Tの観点からは共変であり，AがBのスーパータイプである場合，Foo[A]はFoo[B]のサブタイプであることを意味します．
    * 私が知っている唯一の共変の例は，関数の引数です．
    */
  }

  object chapter562 {

    /**
      * ### 5.6.2 Function Types
      * 関数型について説明したときに、それが具体的にどのように実装されているかを説明しました。
      * Scalaには0〜22個の引数を持つ関数のための23個の組み込み汎用クラスがあります。以下に、それらを紹介します。
      */
    trait Function0[+R] {
      def apply: R
    }

    trait Function1[-A, +B] {
      def apply(a: A): B
    }

    trait Function2[-A, -B, +C] {
      def apply(a: A, b: B): C
    }
    // and so on...

    /**
      * 関数は、引数の観点からは逆変量で、戻り値の型の観点からは共変量です。
      * これは直観的ではないように思えますが、関数の引数の観点から見ると納得がいきます。
      * Function1[A, B]を期待するコードを考えてみましょう。
      */
    case class Box[A](value: A) {

      /** func` を `value` に適用し、その結果の `Box` を返します。 */
      def map[B](func: Function1[A, B]): Box[B] =
        Box(func(value))
    }

    /**
    * 分散を理解するために、このmapメソッドにどのような関数を渡せば安全かを考えてみましょう。
    *
    * * AからBへの関数は明らかにOKです。
    *
    * * AからBのサブタイプへの関数は、その結果の型が、我々が依存するかもしれないBのすべての特性を持つので、OKです。これは、関数がその結果の型において共変的であることを示しています。
    *
    * * Aのスーパータイプを期待する関数も、BoxにあるAが関数が期待するすべてのプロパティを持っているので、OKです。
    *
    * * Aのサブタイプを期待する関数はOKではありません。なぜなら、私たちの値は実際にはAの異なるサブタイプかもしれないからです。
    */
  }

  object chapter563 {

    /**
      * ### 5.6.3 Covariant Sum Types
      * 分散アノテーションについて分かったので、共分散にすることで、Maybeの問題を解決することができます。
      */
    sealed trait Maybe[+A]
    final case class Full[A](value: A) extends Maybe[A]
    case object Empty extends Maybe[Nothing]

    /**
      * 使用時には、期待通りの動作が得られます。EmptyはすべてのFull値のサブタイプです。
      */
    val perhaps: Maybe[Int] = Empty
    // perhaps: Maybe[Int] = Empty

    /**
      * このパターンは、一般的な和の型で最もよく使われるものです。
      * 共変型は、コンテナの型が不変である場合にのみ使用すべきです。
      * コンテナが突然変異を許す場合は、不変型のみを使用すべきです。
      */
    // コンテナ＝Maybe
    /**
      * ---
      *
      * Covariant Generic Sum Type Pattern
      * **共変型汎用和型パターン**
      * 型TのAがBまたはCであり、Cが一般的でない場合、次のように書きます。
      */
    sealed trait A[+T]
    final case class B[T](t: T) extends A[T]
    case object C extends A[Nothing]
    /**
      * このパターンは、2つ以上の型パラメータにも適用されます。ある型パラメータがsum型の特定のケースで必要ない場合、そのパラメータをNothingで代用することができます。
      *
      * ---
      */
    // Emptyみたいなcase objectを作りたかったら +（共変） が必要
  }

  object chapter564 {

    /**
      * ### 5.6.4 Contravariant Position
      * 共変和の型には、共変型のパラメータとコントラバリアントのメソッドや関数のパラメータの相互作用に関わる、もうひとつのパターンがあります。
      * この問題を説明するために、共変和型を開発してみましょう。
      */
    object chapter5641 {

      /**
        * #### 5.6.4.1 Exercise: Covariant Sum
        * covariant generic sum typeパターンを使用して、covariant Sumを実装します。
        */
      /**
        * 模範
        */
      sealed trait Sum[+A, +B]
      final case class Failure[A](value: A) extends Sum[A, Nothing]
      final case class Success[B](value: B) extends Sum[Nothing, B]
    }

    object chapter5642 {

      /**
        * #### 5.6.4.2 Exercise: Some sort of flatMap
        * flatMapを実装し、以下のようなエラーが発生することを確認します。
        */
//      error: covariant type A occurs in contravariant position in type B => Sum[A,C] of value f
//      def flatMap[C](f: B => Sum[A, C]): Sum[A, C] =
//                     ^

//      sealed trait Sum[+A, +B] {
//        def flatMap[C](f: B => Sum[A, C]): Sum[A, C] =
//          this match {
//            case Failure(v) => Failure(v)
//            case Success(v) => f(v)
//          }
//      }
//      final case class Failure[A](value: A) extends Sum[A, Nothing]
//      final case class Success[B](value: B) extends Sum[Nothing, B]

      /**
        * 模範
        */
//      sealed trait Sum[+A, +B] {
//        def flatMap[C](f: B => Sum[A, C]): Sum[A, C] =
//          this match {
//            case Failure(v) => Failure(v)
//            case Success(v) => f(v)
//          }
//      }
//      final case class Failure[A](value: A) extends Sum[A, Nothing]
//      final case class Success[B](value: B) extends Sum[Nothing, B]

      /**
        * いったい何が起こっているのでしょうか？この問題を説明するために、もっと簡単な例にちょっとだけ切り替えてみましょう。
        */
//      case class Box[+A](value: A) {
//        def set(a: A): Box[A] = Box(a)
//      }

      /**
        * 以下のエラーが発生します。
        */
//      error: covariant type A occurs in contravariant position in type A of value a
//      def set(a: A): Box[A] = Box(a)
//              ^

      /**
        * 関数、そして関数と同じようなものであるメソッドは、入力パラメータが対変量であることを覚えておいてください。
        * この場合、Aは共変であると指定しましたが、setの中にはA型のパラメータがあり、型規則ではここでAが共変であることを要求しています。
        * これがコンパイラの言う「contravariant position」という意味です。
        * 解決策は、Aのスーパータイプである新しい型を導入することです。
        * これは、[AA >: A]という表記で次のように行うことができます。
        */
      case class Box[+A](value: A) {
        def set[AA >: A](a: AA): Box[AA] = Box(a)
      }

      /**
        * これでコンパイルに成功しました。
        *
        * flatMapに戻ると、関数fはパラメータであり、したがってcontravariantな位置にあります。
        * B => Sum[A, C]という型で宣言されているので、スーパータイプはBで共変、AとCで逆変ということになります。
        * Cは不変なので、それも問題ありません。一方、Aは共変ですが、逆変の位置にあります。
        * したがって、上のBoxの時と同じ解決策を適用する必要があります。
        */
      sealed trait Sum[+A, +B] {
        // >Aは共変ですが、逆変の位置にあります。
        // f: B => Sum[AA, C]　　ここのAAが元はAだった。逆変の位置
        def flatMap[AA >: A, C](f: B => Sum[AA, C]): Sum[AA, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => f(v)
          }
      }
      final case class Failure[A](value: A) extends Sum[A, Nothing]
      final case class Success[B](value: B) extends Sum[Nothing, B]

      /**
        * ---
        *
        * **Contravariant Positionパターン**
        * 共変型TのAで、Aのメソッドfが、Tが共変型の位置で使われていると訴えた場合、fにTT >: Tという型を導入する。
        */
      case class A[+T]() {
        def f[TT >: T](t: TT): A[TT] = ???
      }
      /**
      *
      *  ---
      */
    }
  }

  object chapter565 {

    /**
      * ### 5.6.5 Type Bounds
      * 先ほど、contravariant positionパターンの中で、いくつかのタイプバウンズを見ました。
      * 型の境界は、スーパータイプだけでなく、サブタイプを指定するように拡張されます。
      * 構文は A <： Type はAがTypeのサブタイプでなければならないことを宣言し、A >: Typeはスーパータイプを宣言します。
      *
      * 例えば、次のような型では、Visitorや任意のサブタイプを格納することができます。
      */
//    case class WebAnalytics[A <: Visitor](
//        visitor: A,
//        pageViews: Int,
//        searchTerms: List[String],
//        isOrganic: Boolean
//    )
  }

  object chapter566 {

    /**
      * ### 5.6.6 Exercises
      */
    object chapter5661 {

      /**
        * #### 5.6.6.1 Covariance and Contravariance
        * AがBのサブタイプであることを示す表記として、A <： BはAがBのサブタイプであることを示し、仮定する。
        *
        * * Siamese <： Cat <： Animal、そして
        * * Purr <： CatSound <： Sound
        * 以下のメソッドがあれば
        */
//      def groom(groomer: Cat => CatSound): CatSound = {
//        val oswald = Cat("Black", "Cat food")
//        groomer(oswald)
//      }

      /**
        * groomに渡すことができるのは、次のうちどれでしょうか？
        * * A function of type Animal => Purr
        * * A function of type Siamese => Purr
        * * A function of type Animal => Sound
        */
      //A function of type Animal => Sound？

      /**
        * 模範
        */
//      唯一機能するのは、Animal => Purr というタイプの関数です。
//      オズワルドはシャム猫ではないので、Siamese => Purr 関数は動作しません。
//      Animal => Sound関数は、戻り値の型がCatSoundである必要があるため、動作しません。
    }

    object chapter5662a {

      /**
        * #### 5.6.6.2 Calculator Again
        * 前章の終わりに見たインタプリタの例に戻ります。
        * 今回は、この章で作成した一般的な抽象化と、map、flatMap、foldに関する新しい知識を使用します。
        *
        * 計算を Sum[String, Double] で表し、String はエラーメッセージです。Sum を拡張して map と fold メソッドを持つようにします。
        */
      // 直近の答えっぽいやつ
      //      sealed trait Sum[+A, +B] {
      //        def flatMap[AA >: A, C](f: B => Sum[AA, C]): Sum[AA, C] =
      //          this match {
      //            case Failure(v) => Failure(v)
      //            case Success(v) => f(v)
      //          }
      //      }
      //      final case class Failure[A](value: A) extends Sum[A, Nothing]
      //      final case class Success[B](value: B) extends Sum[Nothing, B]

      /**
        * 模範
        */
      // foldとmapの実装、これまでのやつと同じ感じがするが…
      sealed trait Sum[+A, +B] {
        def fold[C](error: A => C, success: B => C): C =
          this match {
            case Failure(v) => error(v)
            case Success(v) => success(v)
          }

        def map[C](f: B => C): Sum[A, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => Success(f(v))
          }

        def flatMap[AA >: A, C](f: B => Sum[AA, C]): Sum[AA, C] =
          this match {
            case Failure(v) => Failure(v)
            case Success(v) => f(v)
          }
      }

      final case class Failure[A](value: A) extends Sum[A, Nothing]

      final case class Success[B](value: B) extends Sum[Nothing, B]
    }
    object chapter5662b {

      /**
        * それでは、前回の電卓を再実装してみましょう。次のような代数的なデータ型で定義された抽象的な構文木があります。
        */
//      sealed trait Expression
//      final case class Addition(left: Expression, right: Expression) extends Expression
//      final case class Subtraction(left: Expression, right: Expression) extends Expression
//      final case class Division(left: Expression, right: Expression) extends Expression
//      final case class SquareRoot(value: Expression) extends Expression
//      final case class Number(value: Double) extends Expression

      /**
        * ここで、メソッド eval: Sum[String, Double]を実装します。
        * Sum[String, Double] on Expression. SumにはflatMapとmapを使用し，コードをよりコンパクトにするために必要と思われるユーティリティー・メソッドを導入します．
        * ここではいくつかのテストケースを紹介します。
        */
//      assert(Addition(Number(1), Number(2)).eval == Success(3))
//      assert(SquareRoot(Number(-1)).eval == Failure("Square root of negative number"))
//      assert(Division(Number(4), Number(0)).eval == Failure("Division by zero"))
//      assert(
//        Division(Addition(Subtraction(Number(8), Number(6)), Number(2)), Number(2)).eval == Success(
//          2.0))

      /**
        * 模範
        */
      /**
        * これが私の解決策です。ヘルパー・メソッドlift2を使って、関数を2つの式の結果に「持ち上げる」ようにしました。
        * 以前の解決策よりもコードがコンパクトになり、読みやすくなったことをご理解いただけると思います。
        */
      import chapter56.chapter566.chapter5662a._
      sealed trait Expression {
        def eval: Sum[String, Double] =
          this match {
            case Addition(l, r)    => lift2(l, r, (left, right) => Success(left + right))
            case Subtraction(l, r) => lift2(l, r, (left, right) => Success(left - right))
            case Division(l, r) =>
              lift2(l,
                    r,
                    (left, right) =>
                      if (right == 0)
                        Failure("Division by zero")
                      else
                        Success(left / right))
            case SquareRoot(v) =>
              v.eval flatMap { value =>
                if (value < 0)
                  Failure("Square root of negative number")
                else
                  Success(Math.sqrt(value))
              }
            case Number(v) => Success(v)
          }

        def lift2(l: Expression, r: Expression, f: (Double, Double) => Sum[String, Double]) =
          l.eval.flatMap { left =>
            r.eval.flatMap { right =>
              f(left, right)
            }
          }
      }
      final case class Addition(left: Expression, right: Expression) extends Expression
      final case class Subtraction(left: Expression, right: Expression) extends Expression
      final case class Division(left: Expression, right: Expression) extends Expression
      final case class SquareRoot(value: Expression) extends Expression
      final case class Number(value: Int) extends Expression
    }
  }
}

object chapter57 {

  /**
  * ## 5.7 Conclusions
  * このセクションでは、型とメソッドをそれぞれ抽象化することができる、ジェネリックな型と関数について説明しました。
  *
  * ジェネリックな代数型とジェネリックな構造的再帰の新しいパターンを見てきました。
  * これらのビルディングブロックを使って、ジェネリック型を扱うためのいくつかの一般的なパターン、すなわちfold、map、flatMapを見てきました。
  *
  * 次のセクションでは，Scalaのコレクションクラスを使って，これらのトピックをさらに掘り下げます。
  */
}
