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

  object chapter315 {}
}
