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
      * 印刷された値には @xxxxxxxx という形式のコードが含まれており、これは特定のオブジェクトに固有の識別子です。newを呼び出すたびに、同じ型の異なるオブジェクトが作成されます。
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
