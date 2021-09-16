package Chapter3

/**
  * # 3 Objects and Classes
  * 前の章では、オブジェクトを作成し、メソッドコールによってオブジェクトを操作する方法を説明しました。
  * この章では、クラスを使ってオブジェクトを抽象化する方法を見ていきます。
  * クラスは、オブジェクトを作成するためのテンプレートです。
  * クラスがあれば、同じ型で共通の性質を持つ多くのオブジェクトを作ることができます。
  */
object chapter31 {

  /**
    * ## 3.1 Classes
    * クラスとは、似たようなメソッドやフィールドを持つオブジェクトを作るためのテンプレートのことです。
    * Scalaでは、クラスは型も定義しており、クラスから作られたオブジェクトはすべて同じ型を共有します。
    * これにより、前章の挨拶、Humanの演習で抱えていた問題を解決することができます。
    */
  object chapter311 {

    /**
      * ### 3.1.1 Defining a Class
      * 以下は、シンプルなPersonクラスの宣言です。
      */
    class Person {
      val firstName = "Noel"
      val lastName = "Welsh"
      def name = firstName + " " + lastName
    }

    /**
      * オブジェクト宣言と同様に、クラス宣言は名前（ここではPerson）を束ねるものであり、式ではありません。
      * ただし、オブジェクト名とは異なり、クラス名を式の中で使用することはできません。
      * クラスは値ではありませんし、クラスが存在する名前空間は別にあります。
      */
//    Person
    // <console>:13: error: not found: value Person
    //        Person
    //        ^

    /**
      * new 演算子を使って、新しい Person オブジェクトを作成することができます。
      * オブジェクトは値であり、通常の方法でそのメソッドやフィールドにアクセスします。
      */
    val noel = new Person
    // noel: Person = Person@3a1d255f

    noel.firstName
    // res1: String = Noel

    /**
      * オブジェクトのタイプがPersonであることに注目してください。
      * 印刷された値には @xxxxxxxx という形式のコードが含まれており、これは特定のオブジェクトに固有の識別子です。
      * newを呼び出すたびに、同じ型の異なるオブジェクトが作成されます。
      */
    noel
    // res2: Person = Person@3a1d255f

    val newNoel = new Person
    // newNoel: Person = Person@d31acc3

    val anotherNewNoel = new Person
    // anotherNewNoel: Person = Person@73b7d0e4

    /**
      * つまり、任意のPersonをパラメータとして受け取るメソッドを書くことができるのです。
      */
    object alien {
      def greet(p: Person) =
        "Greetings, " + p.firstName + " " + p.lastName
    }
    alien.greet(noel)
    // res3: String = Greetings, Noel Welsh

    alien.greet(newNoel)
    // res4: String = Greetings, Noel Welsh

    /**
    * ---
    * **Java Tip**
    * Scalaのクラスは、すべてjava.lang.Objectのサブクラスであり、ほとんどの場合、JavaからもScalaからも使用可能です。
    * Personのデフォルトの印刷動作は、java.lang.Objectで定義されているtoStringメソッドに由来しています。
    *
    * ---
    */
  }

  object chapter312 {

    /**
      * ### 3.1.2 Constructors
      * このままではPersonクラスは使い物になりません。
      * 好きなだけ新しいオブジェクトを作ることができますが、それらはすべて同じfirstNameとlastNameを持っています。
      * それぞれの人に違う名前をつけたい場合はどうすればいいでしょうか？
      *
      * 解決策としては、コンストラクタを導入することで、新しいオブジェクトを作成する際にパラメータを渡すことができます。
      */
    class Person1(first: String, last: String) {
      val firstName = first
      val lastName = last
      def name = firstName + " " + lastName
    }
    val dave = new Person1("Dave", "Gurnell")
    // dave: Person = Person@24718052

    dave.name
    // res5: String = Dave Gurnell

    /**
      * コンストラクタのパラメータ first および last は、クラスの本体内でのみ使用できます。
      * オブジェクトの外部からデータにアクセスするためには、val や def を使ってフィールドやメソッドを宣言しなければなりません。
      *
      * コンストラクタの引数とフィールドは，しばしば冗長になります．
      * 幸いなことに、Scalaは両方を一度に宣言する便利な省略方法を提供しています。
      * コンストラクタの引数にvalキーワードを前置することで、Scalaが自動的にフィールドを定義することができます。
      */
    class Person2(val firstName: String, val lastName: String) {
      def name = firstName + " " + lastName
    }
    new Person2("Dave", "Gurnell").firstName
    // res6: String = Dave

    /**
      * valフィールドは不変であり、一度初期化されるとその後は値を変更することができません。
      * Scalaには、可変型フィールドを定義するためのvarキーワードもあります。
      *
      * Scalaのプログラマーは、不変性と副作用のないコードを書きたがる傾向があるので、置換モデルを使ってそれを推論することができます。
      * このコースでは、ほとんどの場合、不変的なvalフィールドに集中します。
      */

    /**
      * ---
      *
      * **クラス宣言の構文**
      * クラスを宣言する構文は以下の通りです。
      */
//    class Name(parameter: type, ...) {
//      declarationOrExpression ...
//    }
    /**
      * or
      */
//    class Name(val parameter: type, ...) {
//      declarationOrExpression ...
//    }
    /**
    * ここで
    * - Name は、クラスの名前です。
    * - オプションのパラメータは、コンストラクタのパラメータに与えられる名前です。
    * - type は、コンストラクタのパラメータの型です。
    * - オプションの declarationOrExpressions は、宣言または式です。
    *
    * ---
    */
  }

  object chapter313 {

    /**
      * ### 3.1.3 Default and Keyword Parameters
      * すべてのScalaのメソッドとコンストラクタは、キーワードパラメータとデフォルトパラメータ値をサポートしています。
      *
      * メソッドやコンストラクタを呼び出す際に、パラメータ名をキーワードにして、任意の順序でパラメータを指定することができます。
      */
//    new Person(lastName = "Last", firstName = "First")
//    // res7: Person = Person@58a18e0f

    /**
      * これは、次のように定義されたデフォルトのパラメータ値と組み合わせて使用すると、二重に便利です。
      */
    def greet(firstName: String = "Some", lastName: String = "Body") =
      "Greetings, " + firstName + " " + lastName + "!"

    /**
      * パラメータにデフォルト値がある場合は、メソッドコールで省略することができます。
      */
    greet("Busy")
    // res8: String = Greetings, Busy Body!

    /**
      * キーワードとデフォルトのパラメータ値を組み合わせることで、初期のパラメータをスキップし、後のパラメータに値を与えることができます。
      */
    greet(lastName = "Dave")
    // res9: String = Greetings, Some Dave!

    /**
      * ---
      *
      * **キーワードパラメータ**
      * キーワードパラメータは、パラメータの数や順番が変わっても大丈夫です。
      * 例えば、greetメソッドにtitleパラメータを追加した場合、キーワードなしのメソッド呼び出しの意味は変わりますが、キーワード付きの呼び出しは変わりません。
      */
//    def greet(title: String = "Citizen", firstName: String = "Some", lastName: String = "Body") =
//      "Greetings, " + title + " " + firstName + " " + lastName + "!"
//
//    greet("Busy") // this is now incorrect
//    // res10: String = Greetings, Busy Some Body!
//
//    greet(firstName = "Busy") // this is still correct
//    // res11: String = Greetings, Citizen Busy Body!

    /**
    * この機能は、多数のパラメータを持つメソッドやコンストラクタを作成する際に特に有効です。
    *
    * ---
    */
  }

  object chapter314 {

    /**
      * ### 3.1.4 Scala’s Type Hierarchy
      * プリミティブ型とオブジェクト型を分けているJavaとは異なり、Scalaではすべてがオブジェクトです。
      * そのため、IntやBooleanなどの「プリミティブ」な値の型は、クラスやtraitと同じ型階層を形成します。
      */
    // 図

    /**
      * Scala には Any という壮大なスーパータイプがあり、その下に AnyVal と AnyRef という 2 つの型があります。
      * AnyVal はすべての値の型のスーパータイプであり、AnyRef はすべての「参照型」またはクラスのスーパータイプです。
      * すべての Scala と Java のクラスは、AnyRef ※4 のサブタイプです。
      * - ※4
      * 実際には、AnyValのサブタイプを定義することができ、それは値クラスとして知られています。
      * これらは特殊な状況下で有用ですが、ここでは説明しません。
      *
      * これらの型の中には、Java に存在する型の Scala でのエイリアスに過ぎないものもあります。
      * Intはint、Booleanはboolean、そしてAnyRefはjava.lang.Objectです。
      *
      * 階層の一番下には、2つの特別な型があります。
      * Nothingはthrow式の型、Nullはnullの値の型です。これらの特殊な型は、他のすべての型のサブタイプです。
      * これにより、throw と null に型を割り当てながら、コード内の他の型を正常に保つことができます。以下のコードで説明します。
      */
    def badness = throw new Exception("Error")
    // badness: Nothing

    def otherbadness = null
    // otherbadness: Null

    val bar = if (true) 123 else badness
    // bar: Int = 123

    val baz = if (false) "it worked" else otherbadness
    // baz: String = null

    /**
    * badnessとresの型はそれぞれNothingとNullですが、barとbazの型はまだ分別があります。
    * これは、IntはIntとNothingの最小公倍数のスーパータイプであり、StringはStringとNullの最小公倍数のスーパータイプだからです。
    */
  }

  object chapter315 {

    /**
      * ### 3.1.5 Take Home Points
      * この章では、同じ型のオブジェクトをたくさん作ることができるクラスの定義方法を学びました。
      * クラスを定義すると、同じ型のオブジェクトをたくさん作ることができます。
      *
      * クラスのオブジェクトのプロパティは、フィールドとメソッドの形をしています。
      * フィールドはオブジェクト内に格納されている事前に計算された値で、メソッドは呼び出し可能な計算です。
      *
      * クラスを宣言する構文は次のとおりです。
      */
//    class Name(parameter: type, ...) {
//      declarationOrExpression ...
//    }
    /**
    * キーワード new を使ってコンストラクタを呼び出し、クラスからオブジェクトを生成します。
    *
    * また、キーワードパラメータとデフォルトパラメータについても学びました。
    *
    * 最後に、Javaの型階層との重複、特殊な型であるAny、AnyRef、AnyVal、Nothing、Null、Unit、そしてJavaとScalaのクラスが型階層の同じサブツリーを占めることなど、Scalaの型階層について学びました。
    */
  }

  object chapter316 {

    /**
      * ### 3.1.6 Exercises
      * 今では、クラスを使って楽しく遊べるだけの機械が揃っています。
      */
    object chapter3161 {

      /**
        * #### 3.1.6.1 Cats, Again
        * 前の練習の時の猫を思い出してください。
        * Catというクラスを定義し、上の表の各猫に対応するオブジェクトを作成します。
        */
      class Cat(val Colour: String, val Food: String)

      new Cat("Black", "Milk")
      new Cat("Ginger", "Chips")
      new Cat("Tabby and white", "Curry")

      /**
        * 模範
        */
      /**
        * これは、クラスを定義するための構文に慣れるための指の運動です。
        */
//      class Cat(val colour: String, val food: String)
//
//      val oswald = new Cat("Black", "Milk")
//      val henderson = new Cat("Ginger", "Chips")
//      val quentin = new Cat("Tabby and white", "Curry")
    }

    object chapter3162 {

      /**
        * #### 3.1.6.2 Cats on the Prowl
        * オブジェクト ChipShop を定義し、メソッド willServe を用意します。
        * このメソッドは、Catを受け取り、その猫の好物がチップスであればtrueを、そうでなければfalseを返す必要があります。
        */
      import Chapter3.chapter31.chapter316.chapter3161.Cat

      object ChipShop {
        def willServe(c: Cat): Boolean = if (c.Food == "Chips") true else false
      }

      /**
        * 模範
        */
//      object ChipShop {
//        def willServe(cat: Cat): Boolean =
//          if(cat.food == "Chips")
//            true
//          else
//            false
//      }
    }

    object chapter3163 {

      /**
        * #### 3.1.6.3 Directorial Debut
        * DirectorとFilmという2つのクラスを、以下のようなフィールドとメソッドで記述します。
        *
        * - Directorは以下を含むべきです。
        * - フィールド firstName（String型
        * - フィールド lastName（String型
        * - フィールド yearOfBirth（Int型
        * - パラメータを受け取らずにフルネームを返すnameというメソッド
        * - Filmは以下を含むべきです。
        * - String型のフィールド name
        *  フィールド yearOfRelease： Int型
        *  - フィールド imdbRating の型 Double
        *  - フィールド director（Director）：Director型
        *  - 公開時の監督の年齢を返すメソッド directorsAge
        *  - Director をパラメータとして受け取り、Boolean を返す isDirectedBy メソッド
        *
        *  以下のデモデータをコピーしてコードに貼り付け、そのまま動作するようにコンストラクタを調整してください。
        */
//      val eastwood          = new Director("Clint", "Eastwood", 1930)
//      val mcTiernan         = new Director("John", "McTiernan", 1951)
//      val nolan             = new Director("Christopher", "Nolan", 1970)
//      val someBody          = new Director("Just", "Some Body", 1990)
//
//      val memento           = new Film("Memento", 2000, 8.5, nolan)
//      val darkKnight        = new Film("Dark Knight", 2008, 9.0, nolan)
//      val inception         = new Film("Inception", 2010, 8.8, nolan)
//
//      val highPlainsDrifter = new Film("High Plains Drifter", 1973, 7.7, eastwood)
//      val outlawJoseyWales  = new Film("The Outlaw Josey Wales", 1976, 7.9, eastwood)
//      val unforgiven        = new Film("Unforgiven", 1992, 8.3, eastwood)
//      val granTorino        = new Film("Gran Torino", 2008, 8.2, eastwood)
//      val invictus          = new Film("Invictus", 2009, 7.4, eastwood)
//
//      val predator          = new Film("Predator", 1987, 7.9, mcTiernan)
//      val dieHard           = new Film("Die Hard", 1988, 8.3, mcTiernan)
//      val huntForRedOctober = new Film("The Hunt for Red October", 1990, 7.6, mcTiernan)
//      val thomasCrownAffair = new Film("The Thomas Crown Affair", 1999, 6.8, mcTiernan)

//      eastwood.yearOfBirth
//      // res16: Int = 1930
//
//      dieHard.director.name
//      // res17: String = John McTiernan
//
//      invictus.isDirectedBy(nolan)
//      // res18: Boolean = false

      /**
        * copyというFilmのメソッドを実装します。
        * このメソッドは、コンストラクタと同じパラメータを受け取り、フィルムの新しいコピーを作成します。
        * 各パラメータにはデフォルト値を与えて、値の任意のサブセットを変更してフィルムをコピーできるようにします。
        */
//      highPlainsDrifter.copy(name = "L'homme des hautes plaines")
//      // res19: Film = Film(L'homme des hautes plaines,1973,7.7,Director(Clint,Eastwood,1930))
//
//      thomasCrownAffair.copy(yearOfRelease = 1968,
//        director = new Director("Norman", "Jewison", 1926))
//      // res20: Film = Film(The Thomas Crown Affair,1968,6.8,Director(Norman,Jewison,1926))
//
//      inception.copy().copy().copy()
//      // res21: Film = Film(Inception,2010,8.8,Director(Christopher,Nolan,1970))

      class Director(val firstName: String, val lastName: String, val yearOfBirth: Int) {
        def name: String = s"$firstName $lastName"
      }
      class Film(val name: String,
                 val yearOfRelease: Int,
                 val imdbRating: Double,
                 val director: Director) {
        def directorsAge: Int = yearOfRelease - director.yearOfBirth
        def isDirectedBy(d: Director): Boolean = if (d.name == director.name) true else false
        def copy(name: String = this.name,
                 yearOfRelease: Int = this.yearOfRelease,
                 imdbRating: Double = this.imdbRating,
                 director: Director = this.director) =
          new Film(name, yearOfRelease, imdbRating, director)
      }

      /**
        * 模範
        */
//      class Director(val firstName: String, val lastName: String, val yearOfBirth: Int) {
//        def name: String =
//          s"$firstName $lastName"
//
//        def copy(firstName: String = this.firstName,
//                 lastName: String = this.lastName,
//                 yearOfBirth: Int = this.yearOfBirth): Director =
//          new Director(firstName, lastName, yearOfBirth)
//      }
//
//      class Film(val name: String,
//                 val yearOfRelease: Int,
//                 val imdbRating: Double,
//                 val director: Director) {
//        def directorsAge =
//          yearOfRelease - director.yearOfBirth
//
//        def isDirectedBy(director: Director) =
//          this.director == director
//
//        def copy(name: String = this.name,
//                 yearOfRelease: Int = this.yearOfRelease,
//                 imdbRating: Double = this.imdbRating,
//                 director: Director = this.director): Film =
//          new Film(name, yearOfRelease, imdbRating, director)
//      }
    }

    object chapter3164 {

      /**
        * #### 3.1.6.4 A Simple Counter
        * Counterクラスを実装します。コンストラクタは Int を受け取ります。
        * メソッドincとdecは、それぞれカウンタをインクリメントとデクリメントして、新しいカウンタを返します。使い方の例を示します。
        */
//      new Counter(10).inc.dec.inc.inc.count
//      // res23: Int = 12

//      class Counter(i: Int) {
//        def inc: Counter = new Counter(i = i + 1)
//        def dec: Counter = new Counter(i = i - 1)
//        def count: Int = i
//      }
      /**
        * 模範
        */
      class Counter(val count: Int) {
        def dec = new Counter(count - 1)
        def inc = new Counter(count + 1)
      }
      /**
      * クラスやオブジェクトを使った練習の他に、この練習には2つ目の目的があります。
      * それは、なぜincとdecは同じカウンタを直接更新するのではなく、新しいカウンタを返すのかを考えることです。
      *
      * valフィールドは不変であるため、countの新しい値を伝達するための他の方法を考えなければなりません。
      * 新しいCounterオブジェクトを返すメソッドは、代入の副作用なしに新しい状態を返すことができます。
      * また、メソッドを連鎖させることができるので、更新のシーケンス全体を1つの式で書くことができます。
      *
      * 使用例のnew Counter(10).inc.dec.inc.countは、実際にはCounterのインスタンスを5つ作成してから最終的なInt値を返しています。
      * このような単純な計算のために、メモリやCPUのオーバーヘッドが増えることを心配されるかもしれませんが、その必要はありません。
      * JVMのような最新の実行環境では、このようなスタイルのプログラミングによる余分なオーバーヘッドは、パフォーマンスが最も重要なコードを除いて、無視できる程度のものです。
      */
    }

    object chapter3165 {

      /**
        * #### 3.1.6.5 Counting Faster
        * 前の課題のCounterを拡張して、incとdecにオプションでIntパラメータを渡せるようにします。
        * パラメータを省略した場合は、デフォルトで1になります。
        */
//      class Counter(val count: Int) {
//        def dec(i: Int = 1) = new Counter(count - i)
//        def inc(i: Int = 1) = new Counter(count + i)
//      }

      /**
        * 模範
        */
      /**
        * 最もシンプルな解決策はこれです。
        */
//      class Counter(val count: Int) {
//        def dec(amount: Int = 1) = new Counter(count - amount)
//        def inc(amount: Int = 1) = new Counter(count + amount)
//      }
      /**
        * しかし、これではincとdecに括弧が追加されてしまいます。
        * パラメータを省略すると、空の括弧を用意しなければなりません。
        */
//      new Counter(10).inc
//      // <console>:14: error: missing argument list for method inc in class Counter
//      // Unapplied methods are only converted to functions when a function type is expected.
//      // You can make this conversion explicit by writing `inc _` or `inc(_)` instead of `inc`.
//      //        new Counter(10).inc
//      //                        ^

      /**
        * これを回避するには、メソッドのオーバーロードを使用して、元の括弧なしのメソッドを再現します。
        * ただし、メソッドのオーバーロードには戻り値の型を指定する必要があります。
        */
      class Counter(val count: Int) {
        def dec: Counter = dec()
        def inc: Counter = inc()
        def dec(amount: Int = 1): Counter = new Counter(count - amount)
        def inc(amount: Int = 1): Counter = new Counter(count + amount)
      }
//      new Counter(10).inc.inc(10).count
//      // res25: Int = 21
    }

    object chapter3166 {

      /**
        * #### 3.1.6.6 Additional Counting
        * ここでは、Adderというシンプルなクラスを紹介します。
        */
//      class Adder(amount: Int) {
//        def add(in: Int) = in + amount
//      }
      /**
        * Counterを拡張し、adjustというメソッドを追加します。
        * このメソッドはAdderを受け取り、Adderをカウントに適用した結果の新しいCounterを返す必要があります。
        */
      class Adder(amount: Int) {
        def add(in: Int) = in + amount
      }
//      class Counter(val count: Int) {
//        def dec: Counter = dec()
//        def inc: Counter = inc()
//        def dec(amount: Int = 1): Counter = new Counter(count - amount)
//        def inc(amount: Int = 1): Counter = new Counter(count + amount)
//        def adjust(a: Adder): Counter = new Counter(a.add(count))
//      }

      /**
        * 模範
        */
      class Counter(val count: Int) {
        def dec = new Counter(count - 1)
        def inc = new Counter(count + 1)
        def adjust(adder: Adder) =
          new Counter(adder.add(count))
      }

      /**
        * これは興味深いパターンで、Scalaの機能を学ぶにつれてより強力になっていきます。
        * ここでは，Adder を使って計算を取り込み，それを Counter に渡しています．
        * 先ほどの説明で、メソッドは式ではなく、フィールドに格納したり、データとして渡したりできないことを思い出してください。
        * しかし、Adders はオブジェクトであり、計算でもあります。
        *
        * オブジェクトを計算機として使用することは、オブジェクト指向プログラミング言語では一般的なパラダイムです。
        * 例えば、JavaのSwingに搭載されている古典的なActionListenerを考えてみましょう。
        */
//      public class MyActionListener implements ActionListener {
//        public void actionPerformed(ActionEvent evt) {
//          // Do some computation
//        }
//      }
      /**
      * AdderやActionListenersのようなオブジェクトの欠点は、ある特定の状況での使用に限定されていることです。
      * Scalaには、関数と呼ばれるより一般的な概念があり、あらゆる種類の計算をオブジェクトとして表現することができます。
      * この章では、関数の背後にある概念のいくつかを紹介します。
      */
    }
  }
  def main(args: Array[String]): Unit = {
    println("chapter31")

    /**
      * 3.1.6.3 Directorial Debut
      */
    import Chapter3.chapter31.chapter316.chapter3163._
    val eastwood = new Director("Clint", "Eastwood", 1930)
    val mcTiernan = new Director("John", "McTiernan", 1951)
    val nolan = new Director("Christopher", "Nolan", 1970)
    val someBody = new Director("Just", "Some Body", 1990)

    val memento = new Film("Memento", 2000, 8.5, nolan)
    val darkKnight = new Film("Dark Knight", 2008, 9.0, nolan)
    val inception = new Film("Inception", 2010, 8.8, nolan)

    val highPlainsDrifter = new Film("High Plains Drifter", 1973, 7.7, eastwood)
    val outlawJoseyWales = new Film("The Outlaw Josey Wales", 1976, 7.9, eastwood)
    val unforgiven = new Film("Unforgiven", 1992, 8.3, eastwood)
    val granTorino = new Film("Gran Torino", 2008, 8.2, eastwood)
    val invictus = new Film("Invictus", 2009, 7.4, eastwood)

    val predator = new Film("Predator", 1987, 7.9, mcTiernan)
    val dieHard = new Film("Die Hard", 1988, 8.3, mcTiernan)
    val huntForRedOctober = new Film("The Hunt for Red October", 1990, 7.6, mcTiernan)
    val thomasCrownAffair = new Film("The Thomas Crown Affair", 1999, 6.8, mcTiernan)

//    println(eastwood.yearOfBirth)
//    // res16: Int = 1930
//
//    println(dieHard.director.name)
//    // res17: String = John McTiernan
//
//    println(invictus.isDirectedBy(nolan))
//    // res18: Boolean = false

//    println(highPlainsDrifter.copy(name = "L'homme des hautes plaines"))
//    // res19: Film = Film(L'homme des hautes plaines,1973,7.7,Director(Clint,Eastwood,1930))
//
//    println(
//      thomasCrownAffair.copy(yearOfRelease = 1968,
//                             director = new Director("Norman", "Jewison", 1926)))
//    // res20: Film = Film(The Thomas Crown Affair,1968,6.8,Director(Norman,Jewison,1926))
//
//    println(inception.copy().copy().copy())
//    // res21: Film = Film(Inception,2010,8.8,Director(Christopher,Nolan,1970))

    /**
      * 3.1.6.4 A Simple Counter
      */
    import Chapter3.chapter31.chapter316.chapter3164.Counter
    println(new Counter(10).inc.dec.inc.inc.count)
  }
}

object chapter32 {

  /**
    * ## 3.2 Objects as Functions
    * 前節の最後の演習では、Adderというクラスを定義しました。
    */
  class Adder(amount: Int) {
    def add(in: Int): Int = in + amount
  }

  /**
    * 議論の中で、Adderを計算を表すオブジェクトと説明しました。これは、値として渡すことができるメソッドを持つようなものです。
    *
    * これは非常に強力な概念であり、Scalaには計算のように振る舞うオブジェクトを作成するための言語機能が完備されています。
    * これらのオブジェクトは関数と呼ばれ，関数型プログラミングの基礎となっています。
    */
  object chapter321 {

    /**
      * ### 3.2.1 The apply method
      * ここでは、関数型プログラミングをサポートするScalaの機能の1つである、関数応用構文を見てみましょう。
      *
      * Scalaでは，applyという名前のメソッドを持つオブジェクトを関数のように「呼び出す」ことができるようになっています．
      * メソッド名を apply とすることで，特別に短縮された呼び出し構文を使うことができます：foo.apply(args) は foo(args) になります．
      *
      * 例えば、Adderのaddメソッドをapplyに変更してみましょう。
      */
    class Adder(amount: Int) {
      def apply(in: Int): Int = in + amount
    }
    val add3 = new Adder(3)
    // add3: Adder = Adder@4185f338

    add3.apply(2)
    // res0: Int = 5

    add3(4) // shorthand for add3.apply(4)
    // res1: Int = 7

    /**
      * この一手間で、オブジェクトは構文上、関数のように「見せる」ことができます。
      * オブジェクトを変数に代入したり、引数として渡したりするなど、メソッドではできないことがたくさんあります。
      */

    /**
    * ---
    *
    * **Function Application Syntax**
    * メソッドコールのobject.apply(parameter, ...)は、object(parameter, ...)と書くこともできます。
    *
    * ---
    */
  }

  object chapter322 {

    /**
    * ### 3.2.2 Take home points
    * このセクションでは、オブジェクトをあたかも関数のように「呼び出す」ことができる、関数適用構文について説明しました。
    *
    * 関数応用構文は、applyメソッドを定義しているすべてのオブジェクトで使用できます。
    *
    * 関数応用構文では、計算のように振る舞う第一級の値を持つことができます。
    * メソッドとは異なり、オブジェクトはデータとして渡すことができます。これで、Scalaにおける真の関数型プログラミングに一歩近づきました。
    */
  }

  object chapter323 {

    /**
      * ### 3.2.3 Exercises
      */
    object chapter3231 {

      /**
        * #### 3.2.3.1 When is a Function not a Function?
        * 次のセクションの終わりには、いくつかのコードを書く機会があるでしょう。
        * 今のところ、重要な理論的疑問について考えてみましょう。
        *
        * 関数応用構文は、計算をしてくれる真に再利用可能なオブジェクトを作るのに、どの程度まで近づいているのでしょうか？ 私たちには何が足りないのでしょうか？
        */

      /**
        * 模範
        */
      /**
      * 主に欠けているのは、値を適切に抽象化する方法である型です。
      *
      * 現時点では、Adderというクラスを定義して、数字に足すというアイデアを表現することができますが、そのコードはコードベース間で適切にポータブルではありません - 他の開発者は、私たちの特定のクラスについて知る必要があります。
      *
      * Handler，Callback，Adder，BinaryAdderなどの名前で共通の関数型のライブラリを定義することもできますが，これはすぐに実用的ではなくなります。
      *
      * 後ほど，Scala がこの問題にどのように対処しているのか，さまざまな状況で使用できる一般的な関数型のセットを定義することで見てみましょう。
      */
    }
  }
}

object chapter33 {

  /**
    * ## 3.3 Companion Objects
    * 論理的にはクラスに属していても、特定のオブジェクトに依存しないメソッドを作りたいことがあります。
    * Javaでは，このような場合，スタティックメソッドを使用しますが，Scalaには，シングルトンオブジェクトという，よりシンプルな解決策があります．
    *
    * 一般的な使用例として、補助コンストラクタがあります。
    * Scalaにはクラスに複数のコンストラクタを定義できる構文がありますが、Scalaのプログラマはほとんどの場合、追加のコンストラクタをクラスと同じ名前のオブジェクトの適用メソッドとして実装することを好みます。
    * このオブジェクトをクラスのコンパニオンオブジェクトと呼んでいます。例えば，以下のようになります．
    */
  class Timestamp(val seconds: Long)

  object Timestamp {
    def apply(hours: Int, minutes: Int, seconds: Int): Timestamp =
      new Timestamp(hours * 60 * 60 + minutes * 60 + seconds)
  }
  Timestamp(1, 1, 1).seconds
  // res1: Long = 3661

  /**
    * ---
    *
    * **コンソールの効果的な使い方**
    * 上のトランスクリプトでは、:pasteコマンドを使用しています。
    * コンパニオンオブジェクトは、サポートするクラスと同じコンパイルユニットで定義する必要があります。
    * 通常のコードベースでは、これは単にクラスとオブジェクトを同じファイルに定義することを意味しますが、REPLでは :paste を使用して1つのコマンドにそれらを入力しなければなりません。
    *
    * 詳細については、REPLで:helpと入力することができます。
    *
    * ---
    */
  /**
    * 先ほど見たように、Scalaには型名の空間と値名の空間という2つの名前空間があります。
    * この分離により，クラスとコンパニオン・オブジェクトに同じ名前を付けても矛盾しないようになっています．
    *
    * ここで重要なのは，コンパニオン・オブジェクトはクラスのインスタンスではなく，独自の型を持つシングルトン・オブジェクトであるということです．
    */
  Timestamp // note that the type is `Timestamp.type`, not `Timestamp`
  // res2: Timestamp.type = Timestamp$@137bf92e

  /**
    * ---
    *
    * **コンパニオンオブジェクトの構文**
    * クラスのコンパニオン・オブジェクトを定義するには、そのクラスと同じファイルに、同じ名前のオブジェクトを定義します。
    */
//  class Name {
//    ...
//  }
//
//  object Name {
//    ...
//  }
  /**
    *
    * ---
    */
  object chapter331 {

    /**
    * ### 3.3.1 Take home points
    * コンパニオンオブジェクトは、機能をクラスのインスタンスに関連付けることなく、クラスに関連付ける手段となります。
    * コンパニオン・オブジェクトは、コンストラクタの追加によく使われます。
    *
    * コンパニオン・オブジェクトは、Javaのスタティック・メソッドに代わるものです。
    * コンパニオン・オブジェクトは、同等の機能を提供し、より柔軟性があります。
    *
    * コンパニオン・オブジェクトは、関連付けられたクラスと同じ名前を持っています。
    * Scalaには、値の名前空間と型の名前空間の2つの名前空間があるので、これは名前の衝突を引き起こしません。
    *
    * コンパニオン・オブジェクトは、関連するクラスと同じファイルで定義しなければなりません。
    * REPLで入力する際には、クラスとコンパニオンオブジェクトは、:pasteモードを使って同じコードブロックに入力する必要があります。
    */
  }

  object chapter332 {

    /**
      * ### 3.3.2 Exercises
      */
    object chapter3321 {

      /**
        * #### 3.3.2.1 Friendly Person Factory
        */
//      class Person(val name: String)
//
//      object Person {
//        def apply(firstName: String, lastName: String): Person =
//          new Person(firstName + " " + lastName)
//      }

      /**
        * 模範
        */
      class Person(val firstName: String, val lastName: String) {
        def name: String =
          s"$firstName $lastName"
      }

      object Person {
        def apply(name: String): Person = {
          val parts = name.split(" ")
          new Person(parts(0), parts(1))
        }
      }
    }

//    object chapter3322 {
//      class Director(val firstName: String, val lastName: String, val yearOfBirth: Int) {
//        def name: String = s"$firstName $lastName"
//      }
//
//      object Director {
//        def apply(firstName: String, lastName: String, yearOfBirth: Int): Director =
//          new Director(firstName, lastName, yearOfBirth)
//        def older(d1: Director, d2: Director) = if (d1.yearOfBirth >= d2.yearOfBirth) d1 else d2
//      }
//
//      class Film(val name: String,
//                 val yearOfRelease: Int,
//                 val imdbRating: Double,
//                 val director: Director) {
//        def directorsAge: Int = yearOfRelease - director.yearOfBirth
//        def isDirectedBy(d: Director): Boolean = if (d.name == director.name) true else false
//        def copy(name: String = this.name,
//                 yearOfRelease: Int = this.yearOfRelease,
//                 imdbRating: Double = this.imdbRating,
//                 director: Director = this.director) =
//          new Film(name, yearOfRelease, imdbRating, director)
//      }
//
//      object Film {
//        def apply(name: String, yearOfRelease: Int, imdbRating: Double, director: Director): Film =
//          new Film(name, yearOfRelease, imdbRating, director)
//
//        def highestRating(f1: Film, f2: Film): Film = if (f1.imdbRating >= f2.imdbRating) f1 else f2
//        def oldestDirectorAtTheTime(f1: Film, f2: Film): Film =
//          if (f1.directorsAge >= f2.directorsAge)
//            f1
//          else f2
//      }
    class Director(val firstName: String, val lastName: String, val yearOfBirth: Int) {

      def name: String =
        s"$firstName $lastName"

      def copy(firstName: String = this.firstName,
               lastName: String = this.lastName,
               yearOfBirth: Int = this.yearOfBirth) =
        new Director(firstName, lastName, yearOfBirth)
    }

    object Director {
      def apply(firstName: String, lastName: String, yearOfBirth: Int): Director =
        new Director(firstName, lastName, yearOfBirth)

      def older(director1: Director, director2: Director): Director =
        if (director1.yearOfBirth < director2.yearOfBirth) director1 else director2
    }

    class Film(val name: String,
               val yearOfRelease: Int,
               val imdbRating: Double,
               val director: Director) {
      def directorsAge =
        director.yearOfBirth - yearOfRelease

      def isDirectedBy(director: Director) =
        this.director == director

      def copy(name: String = this.name,
               yearOfRelease: Int = this.yearOfRelease,
               imdbRating: Double = this.imdbRating,
               director: Director = this.director) =
        new Film(name, yearOfRelease, imdbRating, director)
    }

    object Film {
      def apply(name: String, yearOfRelease: Int, imdbRating: Double, director: Director): Film =
        new Film(name, yearOfRelease, imdbRating, director)

      def newer(film1: Film, film2: Film): Film =
        if (film1.yearOfRelease < film2.yearOfRelease) film1 else film2

      def highestRating(film1: Film, film2: Film): Double = {
        val rating1 = film1.imdbRating
        val rating2 = film2.imdbRating
        if (rating1 > rating2) rating1 else rating2
      }

      def oldestDirectorAtTheTime(film1: Film, film2: Film): Director =
        if (film1.directorsAge > film2.directorsAge) film1.director else film2.director
    }
  }
  def main(args: Array[String]): Unit = {
    import Chapter3.chapter33.chapter332.chapter3321.Person
//    println(Person("Jane", "Doe").name)
    Person.apply("John Doe").firstName // full method call
    // res5: String = John

    Person("John Doe").firstName // sugared apply syntax
    // res6: String = John

    /**
    * **************************************************
    */
  }
}

object chapter34 {

  /**
    * ## 3.4 Case Classes
    * ケース・クラスは、クラス、コンパニオン・オブジェクト、および多くの適切なデフォルトを一度に定義するための非常に便利な略記法です。
    * ケースクラスは、データを保持する軽量なクラスを最小限の手間で作成するのに最適です。
    *
    * ケースクラスは、クラス定義の前にキーワードcaseを付けるだけで作成できます。
    */
  case class Person(firstName: String, lastName: String) {
    def name = firstName + " " + lastName
  }

  /**
    * **caseクラスを宣言すると、Scalaは自動的にクラスとコンパニオン・オブジェクトを生成します。**
    */
  val dave = new Person("Dave", "Gurnell") // we have a class
  // dave: Person = Person(Dave,Gurnell)

  Person // and a companion object too
  // res0: Person.type = Person

  /**
    * さらに、クラスとコンパニオンには、非常に便利な機能があらかじめ用意されています。
    */
  object chapter341 {

    /**
      * ### 3.4.1 Features of a case class
      * 1 コンストラクタの引数ごとのフィールドです。コンストラクタの定義で val を記述する必要はありませんが、記述しても問題はありません。
      */
    dave.firstName
    // res1: String = Dave

    /**
      * 2 デフォルトのtoStringメソッドは、コンストラクタのようなクラスの表現を表示します（@記号や暗号のような16進数は使用しません）。
      */
    dave
    // res2: Person = Person(Dave,Gurnell)

    /**
      * 3 オブジェクトのフィールド値を操作するSensible equals、およびhashCodeメソッド。
      * これにより、リスト、セット、マップなどのコレクションでケースクラスを簡単に使用することができます。
      * また、オブジェクトを比較する際に、参照元のアイデンティティではなく、そのコンテンツに基づいて比較することができます。
      */
    new Person("Noel", "Welsh").equals(new Person("Noel", "Welsh"))
    // res3: Boolean = true

    new Person("Noel", "Welsh") == new Person("Noel", "Welsh")
    // res4: Boolean = true

    /**
      * 4 現在のオブジェクトと同じフィールド値を持つ新しいオブジェクトを作成するコピーメソッドです。
      */
    dave.copy()
    // res5: Person = Person(Dave,Gurnell)

    /**
      * copy メソッドは、現在のオブジェクトを返すのではなく、新しいクラスのオブジェクトを作成して返すことに注意してください。
      *
      * copy メソッドには、コンストラクタの各パラメータに対応するオプションのパラメータを指定できます。
      * パラメータが指定された場合、新しいオブジェクトは現在のオブジェクトの既存の値ではなく、その値を使用します。
      * このメソッドは、キーワードパラメータを使用して、1つまたは複数のフィールドの値を変更しながらオブジェクトをコピーする場合に最適です。
      */
    dave.copy(firstName = "Dave2")
    // res6: Person = Person(Dave2,Gurnell)

    dave.copy(lastName = "Gurnell2")
    // res7: Person = Person(Dave,Gurnell2)

    /**
      * ---
      *
      * **値と基準値の均等性**
      * Scalaの==演算子はJavaのものとは異なり、参照の同一性で値を比較するのではなく、equalsに委ねられます。
      * Scalaには、Javaの==と同じ動作をするeqという演算子がありますが、アプリケーションコードで使われることはほとんどありません。
      */
    new Person("Noel", "Welsh") eq (new Person("Noel", "Welsh"))
    // res8: Boolean = false

    dave eq dave
    // res9: Boolean = true

    // eqだと厳密な比較（同一。オブジェクトが
    /**
      *
      *  ---
      */

    /**
      * 5 Caseクラスは、java.io.Serializableとscala.Productの2つのtraitを実装しています。どちらも直接は使用しません。
      * 後者は，フィールドの数とケースクラスの名前を調べるためのメソッドを提供しています。
      */
    // アクション履歴のところで↑使ってるらしい
  }

  object chapter342 {

    /**
      * ### 3.4.2 Features of a case class companion object
      * コンパニオンオブジェクトには，クラスのコンストラクタと同じ引数を持つ apply メソッドが含まれています．
      * Scalaプログラマはコンストラクタよりもapplyメソッドを好む傾向があります。
      * newを省略することで簡潔になり、コンストラクタが式の中で読みやすくなるからです。
      */
    Person("Dave", "Gurnell") == Person("Noel", "Welsh")
    // res10: Boolean = false

    Person("Dave", "Gurnell") == Person("Dave", "Gurnell")
    // res11: Boolean = true

    /**
      * 最後に、コンパニオン・オブジェクトには、パターン・マッチングで使用する抽出パターンを実装するコードも含まれています。
      * これはこの章の後半で紹介します。
      */

    /**
      * ---
      *
      * **ケースクラス宣言の構文**
      * ケースクラスを宣言する構文は
      */
//    case class Name(parameter: type, ...) {
//      declarationOrExpression ...
//    }
    /**
    * ここで
    * - Name は、ケースクラスの名前です。
    * - オプションのパラメータは、コンストラクタのパラメータに与えられる名前です。
    * - type は、コンストラクタのパラメータの型です。
    * - オプションの declarationOrExpressions は、宣言または式です。
    *
    * ---
    */
  }

  object chapter343 {

    /**
      * ### 3.4.3 Case objects
      * 最後の注意点です。コンストラクタの引数がない case クラスを定義してしまった場合、代わりに case オブジェクトを定義することができます。
      * ケース・オブジェクトは通常のシングルトン・オブジェクトと同様に定義されますが、より意味のある toString メソッドを持ち、Product および Serializable traitを拡張します。
      */
    case object Citizen {
      def firstName = "John"
      def lastName = "Doe"
      def name = firstName + " " + lastName
    }
    Citizen.toString
    // res12: String = Citizen
  }

  object chapter344 {

    /**
      * ### 3.4.4 Take Home Points
      * Caseクラスは、Scalaのデータ型の基本中の基本です。使って、学んで、愛してください。
      * ケースクラスを宣言する構文は、クラスを宣言する構文と同じですが、ケースが付加されています。
      */
//    case class Name(parameter: type, ...) {
//      declarationOrExpression ...
//    }
    /**
    * Caseクラスには、タイプミスを防ぐために自動生成されるメソッドや機能が数多くあります。
    * 関連するメソッドを自分で実装することで、この動作を部分的にオーバーライドすることができます。
    * Scala 2.10およびそれ以前のバージョンでは、0から22個のフィールドを含むケースクラスを定義できます。
    * Scala 2.11では、任意のサイズのケースクラスを定義できるようになりました。
    */
  }

  object chapter345 {

    /**
      * ### 3.4.5 Exercises
      */
    object chapter3451 {

      /**
        * #### 3.4.5.1 Case Cats
        */
      case class Cat(Colour: String, Food: String)

      /**
        * 模範
        */
      /**
        * もう一つの簡単な指の運動。
        */
//      case class Cat(colour: String, food: String)
    }

    object chapter3452 {

      /**
        * #### 3.4.5.2 Roger Ebert Said it Best…
        */
      case class Director(firstName: String, lastName: String, yearOfBirth: Int) {
        def name: String = s"$firstName $lastName"
      }

      object Director {
        def older(director1: Director, director2: Director): Director =
          if (director1.yearOfBirth < director2.yearOfBirth) director1 else director2
      }

      case class Film(name: String, yearOfRelease: Int, imdbRating: Double, director: Director) {
        def directorsAge = director.yearOfBirth - yearOfRelease
        def isDirectedBy(director: Director) = this.director == director
      }

      object Film {
        def newer(film1: Film, film2: Film): Film =
          if (film1.yearOfRelease < film2.yearOfRelease) film1 else film2

        def highestRating(film1: Film, film2: Film): Double = {
          val rating1 = film1.imdbRating
          val rating2 = film2.imdbRating
          if (rating1 > rating2) rating1 else rating2
        }

        def oldestDirectorAtTheTime(film1: Film, film2: Film): Director =
          if (film1.directorsAge > film2.directorsAge) film1.director else film2.director
      }

      /**
        * 模範
        */
      /**
        * Caseクラスは、copyメソッドとapplyメソッドを提供し、コンストラクタの各引数の前にvalを書く必要がありません。
        * 最終的なコードベースは以下のようになります。
        */
//      case class Director(firstName: String, lastName: String, yearOfBirth: Int) {
//        def name: String =
//          s"$firstName $lastName"
//      }
//
//      object Director {
//        def older(director1: Director, director2: Director): Director =
//          if (director1.yearOfBirth < director2.yearOfBirth) director1 else director2
//      }
//
//      case class Film(name: String, yearOfRelease: Int, imdbRating: Double, director: Director) {
//
//        def directorsAge =
//          yearOfRelease - director.yearOfBirth
//
//        def isDirectedBy(director: Director) =
//          this.director == director
//      }
//
//      object Film {
//        def newer(film1: Film, film2: Film): Film =
//          if (film1.yearOfRelease < film2.yearOfRelease) film1 else film2
//
//        def highestRating(film1: Film, film2: Film): Double = {
//          val rating1 = film1.imdbRating
//          val rating2 = film2.imdbRating
//          if (rating1 > rating2) rating1 else rating2
//        }
//
//        def oldestDirectorAtTheTime(film1: Film, film2: Film): Director =
//          if (film1.directorsAge > film2.directorsAge) film1.director else film2.director
//      }
      /**
      * このコードは大幅に短くなっただけでなく、equalsメソッド、toStringメソッド、パターンマッチ機能を備えており、後の演習に備えています。
      */
    }

    object chapter3453 {

      /**
        * 必要に応じてcopyを使用して、Counterをケースクラスとして再実装します。さらに、countをデフォルト値の0に初期化します。
        */
      // 不要だった
//      case class Adder(amount: Int) {
//        def add(in: Int) = in + amount
//      }

      case class Counter(count: Int = 0) {
        def dec = this.copy(count - 1)
        def inc = this.copy(count + 1)
//        def adjust(adder: Adder) = this.copy(adder.add(count))
      }

      /**
        * 模範
        */
//      case class Counter(count: Int = 0) {
//        def dec = copy(count = count - 1)
//        def inc = copy(count = count + 1)
//      }
      /**
        * これはほとんどトリックの練習で、以前の実装との違いはほとんどありません。
        * しかし、私たちが無料で手に入れた追加機能に注目してください。
        */
      Counter(0) // `new` を使わずにオブジェクトを構築する
      // res16: Counter = Counter(0)
      // Argument duplicates corresponding parameter default value -> 引数は対応するパラメータのデフォルト値を複製

      Counter().inc // プリントアウトは`count`の値を表示します。
      // res17: Counter = Counter(1)

      Counter().inc.dec == Counter().dec.inc // セマンティック・イコール・チェック
      // res18: Boolean = true
    }

    object chapter3454 {

      /**
        * Caseクラスにコンパニオンオブジェクトを定義するとどうなるのでしょうか？見てみましょう。
        * 前節のPersonクラスをcaseクラスにしてみましょう（ヒント：コードは上にあります）。
        * 別の適用方法でもコンパニオン・オブジェクトがあることを確認してください。
        */
//      case class Person(firstName: String, lastName: String) {
//        def name: String = s"$firstName $lastName"
//      }
//
//      object Person {
//        def apply(name: String): Person = {
//          val parts = name.split(" ")
//          new Person(parts(0), parts(1))
//        }
//      }

      /**
        * 模範
        */
      /**
        * これがそのコードです。
        */
      case class Person(firstName: String, lastName: String) {
        def name = firstName + " " + lastName
      }

      object Person {
        def apply(name: String): Person = {
          val parts = name.split(" ")
          apply(parts(0), parts(1))
        }
      }

      /**
        * Personのコンパニオンオブジェクトを定義しているにもかかわらず、Scalaのケースクラスコード生成機能は期待通りに動作しています。
        * これは、自動生成されたコンパニオンメソッドを定義したオブジェクトに追加するものです。
        *
        * つまり，2つの可能な型署名を持つオーバーロードされた apply メソッドを持つコンパニオン・オブジェクトができあがります．
        */
//      def apply(name: String): Person =
//      // etc...
//
//      def apply(firstName: String, lastName: String): Person =
//      // etc...
    }
  }
}

object chapter35 {

  /**
    * ## 3.5 Pattern Matching
    * これまでは、メソッドを呼び出したり、フィールドにアクセスしたりして、オブジェクトを操作してきました。
    * ケース・クラスでは、パターン・マッチングという別の方法でオブジェクトを操作することができます。
    *
    * パターン・マッチングは if 式を拡張したようなもので、データの「形状」に応じて式を評価することができます。
    * これまでの例で見たPersonのケースクラスを思い出してください。
    */
  case class Person(firstName: String, lastName: String)
  case class AAA(firstName: String, lastName: String)

  /**
    * 例えば、反乱軍のメンバーを探すストームトルーパーを実装したいとします。この場合、次のようなパターンマッチングが使えます。
    */
  object Stormtrooper {
    def inspect(person: Person): String =
      person match {
        case Person("Luke", "Skywalker") => "Stop, rebel scum!"
        case Person("Han", "Solo")       => "Stop, rebel scum!"
        case Person(first, last)         => s"Move along, $first"
      }
  }

  /**
    * パターン(Person("Luke", "Skywalker"))を表す構文と、パターンがマッチするオブジェクト(Person("Luke", "Skywalker"))を構築する構文が一致していることに注目してください。
    * ここではそれを使用しています。
    */
  Stormtrooper.inspect(Person("Noel", "Welsh"))
  // res0: String = Move along, Noel

  Stormtrooper.inspect(Person("Han", "Solo"))
  // res1: String = Stop, rebel scum!

  /**
    * ---
    *
    * **パターン・マッチングの構文**
    * パターンマッチ式の構文は
    */
//  expr0 match {
//    case pattern1 => expr1
//    case pattern2 => expr2
//      ...
//  }
  /**
    * ここで
    * - 式expr0が評価され、マッチする値になる。
    * - この値に対して，パターン（ガード）pattern1，pattern2，...が順にチェックされる．
    * - 最初にマッチしたパターンの右辺の式(expr1, expr2など)が評価される5。
    * - パターンマッチングは、それ自体が式であり、評価されて値（マッチした式の値）になります。
    *
    * ---
    */
  object chapter351 {

    /**
      * ### 3.5.1 Pattern Syntax
      * Scalaには、パターンやガードを書くための表現力豊かな構文があります。
      * ケースクラスの場合、パターンの構文はコンストラクタの構文と一致します。例えば，データ
      */
    Person("Noel", "Welsh")
    // res2: Person = Person(Noel,Welsh)

    /**
      * Personタイプにマッチするパターンが書かれています。
      */
//    Person(pat0, pat1)

    /**
    * ここで、pat0 と pat1 はそれぞれ firstName と lastName に対してマッチするパターンです。pat0やpat1の代わりに使用できるパターンは4つあります。
    * 1. 名前。その位置にある任意の値とマッチし、与えられた名前にバインドされます。
    * 例えば、Person(first, last)というパターンは、firstという名前を "Noel "という値に、lastという名前を "Welsh "という値に結びつけます。
    *
    * 2. アンダースコア(_)は、任意の値にマッチし、それを無視します。
    * 例えば、ストームトルーパーは一般市民のファーストネームしか気にしないので、Person(first, _)と書けば、ラストネームの値に名前を束縛することはありません。
    *
    * 3. リテラルとは、そのリテラルが表す値のみにマッチすることを意味します。
    * たとえば、Person("Han", "Solo")というパターンは、ファーストネームが "Han"、ラストネームが "Solo "の人物にマッチします。
    *
    * 4. 同じコンストラクタ形式の構文を使用した別の case クラスです。
    *
    * パターン マッチングでできることは他にもたくさんあり、パターン マッチングは実際に拡張可能です。これらの機能については、後のセクションで説明します。
    */
  }

  object chapter352 {

    /**
      * ### 3.5.2 Take Home Points
      * ケース・クラスは、パターン・マッチングと呼ばれる新しい形式のインタラクションを可能にします。
      * パターン・マッチングでは、ケース・クラスを分解して、ケース・クラスに含まれるものに応じて異なる式を評価することができます。
      *
      * パターン・マッチングの構文は次のとおりです。
      */
//    expr0 match {
//      case pattern1 => expr1
//      case pattern2 => expr2
//        ...
//    }
    /**
    * パターンには次のようなものがあります。
    * - 名前：任意の値をその名前に結びつける。
    * - アンダースコア：任意の値にマッチし、それを無視する。
    * - リテラル（そのリテラルが示す値に一致）、または
    * - ケースクラスのコンストラクタ形式のパターン。
    */
  }

  object chapter353 {

    /**
      * ### 3.5.3 Exercises
      */
    object chapter3531 {

      /**
        * #### 3.5.3.1 Feed the Cats
        * オブジェクト ChipShop を定義し、メソッド willServe を用意します。
        * このメソッドは、Catを受け取り、その猫の好物がチップスであればtrueを、そうでなければfalseを返す必要があります。
        * パターンマッチングを使用します。
        */
//      import Chapter3.chapter34.chapter345.chapter3451.Cat
//      object ChipShop {
//        def willServe(cat: Cat): Boolean = {
//          cat match {
//            case Cat(_, "Chips") => true
//            case _               => false
//          }
//        }
//      }

      /**
        * 模範
        */
      /**
        * まずは、問題文で提案されているスケルトンを書いてみましょう。
        */
      case class Cat(name: String, colour: String, food: String)
//
//      object ChipShop {
//        def willServe(cat: Cat): Boolean =
//          cat match {
//            case Cat(???, ???, ???) => ???
//          }
//      }
      /**
        * 戻り値の型がブール値であることから、少なくとも2つのケースが必要であることがわかります。
        * 練習問題のテキストには、「チップスが好きな猫」と「その他の猫」というケースが書かれています。これは、リテラルパターンと_パターンで実装できます。
        */
      object ChipShop {
        def willServe(cat: Cat): Boolean =
          cat match {
            case Cat(_, _, "Chips") => true
            case Cat(_, _, _)       => false
          }
      }
    }

    object chapter3532 {

      /**
        * #### 3.5.3.2 Get Off My Lawn!
        * この演習では、映画評論家である私の父のシミュレーターを書きます。
        * クリント・イーストウッド監督の映画には10.0、ジョン・マクティアナン監督の映画には7.0、その他の映画には3.0の評価をつけるという、とてもシンプルなものです。
        * Dadというオブジェクトを実装し、Filmを受け取ってDoubleを返すメソッドrateを用意します。パターンマッチングを使用します。
        */
      import Chapter3.chapter34.chapter345.chapter3452.Film
      object Dad {
        def rate(film: Film): Double = {
          film.director match {
            case "Clint Eastwood" => 10.0
            case "John McTiernan" => 7.0
            case _                => 3.0
          }
        }
      }

      /**
        * 模範
        */
//      object Dad {
//        def rate(film: Film): Double =
//          film match {
//            case Film(_, _, _, Director("Clint", "Eastwood", _)) => 10.0
//            case Film(_, _, _, Director("John", "McTiernan", _)) => 7.0
//            case _ => 3.0
//          }
//      }
      /**
      * この場合、パターンマッチはかなり冗長になっています。後ほど、定数パターンと呼ばれる、特定の値にマッチするパターンマッチの使い方をご紹介します。
      */

    }
  }
}

object chapter36 {

  /**
  * ## 3.6 Conclusions
  * このセクションでは、クラスについて説明しました。
  * クラスを使うことで、オブジェクトの抽象化ができることを説明しました。
  * つまり、共通のプロパティを持ち、共通の型を持つオブジェクトを定義することができるのです。
  *
  * また、コンパニオン・オブジェクトについても見てきました。
  * コンパニオン・オブジェクトは、クラスに属さない補助的なコンストラクタやその他のユーティリティ・メソッドを定義するためにScalaで使用されます。
  *
  * 最後に、ケースクラスを紹介しました。
  * ケースクラスは、定型的なコードを大幅に削減し、メソッドの呼び出しに加えて、パターンマッチという新しいオブジェクトの操作方法を可能にします。
  */
}
