package Chapter4

/**
  * # 4 Modelling Data with Traits
  * 前章では、クラスについて詳しく説明しました。
  * クラスは、類似した特性を持つオブジェクトを抽象化する方法を提供し、クラス内のどのオブジェクトでも動作するコードを書くことができます。
  *
  * 本章では、クラスの抽象化について検討し、異なるクラスのオブジェクトを扱うコードを書けるようにします。
  * これを実現するのが traits と呼ばれるメカニズムです。
  *
  * この章では、私たちの焦点が変わります。
  * これまでの章では、Scalaのコードを構築するための技術的な側面を取り上げてきました。
  * この章では，まず traits の技術的な側面に注目します。その後，自分の考えを表現する手段としてScalaを使うことに焦点を当てます。
  *
  * 代数的データ型と呼ばれるデータの記述を、どのようにしてコードに機械的に変換するかを見ていきます。
  * 構造的再帰を用いて，代数的データ型を変換するコードを機械的に書くことができます．
  */
object chapter41 {

  /**
    * ## 4.1 Traits
    * クラスがオブジェクトを作るためのテンプレートであるのと同様に、Traitsはクラスを作るためのテンプレートです。
    * Traitsを使うと、2つ以上のクラスを同じものとみなして、同じ操作を実装することを表現することができます。
    * 言い換えれば、traitは、複数のクラスが共通のスーパータイプを共有することを表現することができます（すべてのクラスが共有するAnyスーパータイプを除く）。
    */
  /**
    * ---
    *
    * **TraitsとJavaインターフェイスの比較**
    * Traitsは、デフォルトのメソッドを持つJava 8のインターフェイスによく似ています。
    * Java 8を使ったことがない方は、Traitsをインターフェイスと抽象クラスを掛け合わせたようなものだと思ってください。
    *
    * ---
    */
  object chapter411 {

    /**
      * ### 4.1.1 An Example of Traits
      * まず、特徴の例を挙げてみましょう。あるウェブサイトへの訪問者をモデル化しているとします。
      * 訪問者には、サイトに登録した人と、匿名の人の2種類があります。これを2つのクラスでモデル化します。
      */
//    import java.util.Date
//
//    case class Anonymous(id: String, createdAt: Date = new Date())
//
//    case class User(
//        id: String,
//        email: String,
//        createdAt: Date = new Date()
//    )

    /**
      * これらのクラス定義では、匿名の訪問者と登録された訪問者の両方が、IDと作成日を持っているということになります。
      * しかし、私たちが知っているのは、登録された訪問者の電子メールアドレスだけです。
      */
    /**
      * ここには明らかな重複があり、同じ定義を2度書かなくて済むのはありがたいことです。しかし、もっと重要なことは、2種類の訪問者に共通の型を作ることです。
      * もし、（AnyRefやAny以外の）共通の型があれば、どんな種類の訪問者にも対応するメソッドを書くことができます。
      * これを実現するには、次のような trait を使用します。
      */
    import java.util.Date

    trait Visitor {
      def id: String // Unique id assigned to each user
      def createdAt: Date // Date this user first visited the site

      // How long has this visitor been around?
      def age: Long = new Date().getTime - createdAt.getTime
    }

    case class Anonymous(id: String, createdAt: Date = new Date()) extends Visitor

    case class User(id: String, email: String, createdAt: Date = new Date()) extends Visitor

    /**
      * 2つの変更点に注目してください。
      * - Visitorというtraitを定義したこと。
      * - extendsキーワードを使ってAnonymousとUserがVisitorというtraitのサブタイプであることを宣言したこと。
      */
    /**
      * Visitorのtraitは、どのサブタイプも実装しなければならないインターフェースを表現しています。
      * つまり、idというStringとcreatedAt Dateを実装しなければなりません。
      * また、Visitorのサブタイプは、Visitorで定義されているageというメソッドを自動的に持ちます。
      */
    /**
      * Visitor traitを定義することで、以下のようにVisitorのどのサブタイプでも動作するメソッドを書くことができます。
      */
    def older(v1: Visitor, v2: Visitor): Boolean =
      v1.createdAt.before(v2.createdAt)

    older(Anonymous("1"), User("2", "test@example.com"))
    // res5: Boolean = true

    /**
      * olderメソッドは、AnonymousとUserのどちらもVisitorのサブタイプとして呼び出すことができます。
      */
    /**
      * ---
      *
      * **Trait Syntax**
      * traitを宣言するには、次のように書きます。
      */
//    trait TraitName {
//      declarationOrExpression ...
//    }
    /**
      * クラスがtraitのサブタイプであることを宣言するには、次のように書きます。
      */
//    class Name(...) extends TraitName {
//      ...
//    }
    /**
      * より一般的には、caseクラスを使用しますが、構文は同じです。
      */
//    case class Name(...) extends TraitName {
//      ...
//    }
    /**
    *
    * ---
    */
  }

  object chapter412 {

    /**
      * ### 4.1.2 Traits Compared to Classes
      * traitは、クラスと同様に、フィールドやメソッドの定義を名前付きでまとめたものです。
      * **しかし、いくつかの重要な点でクラスとは異なります。**
      *
      * - trait はコンストラクタを持たず、trait から直接オブジェクトを生成することはできません。
      *    その代わり、traitを使ってクラスを作成し、そのクラスからオブジェクトを作成することができます。
      *    traitをベースにしたクラスはいくつでも作れます。
      *
      * - trait は、名前と型のシグネチャを持つが実装を持たない抽象メソッドを定義できます。
      *    これはVisitor traitで見られました。
      *    traitを継承したクラスを作るときには、実装を指定しなければなりませんが、それまでは抽象的な定義のままでも自由です。
      *
      * それでは、Visitor traitに戻って、抽象的な定義をさらに探ってみましょう。Visitorの定義を思い出してください。
      */
    import java.util.Date

    trait Visitor {
      def id: String // Unique id assigned to each user

      def createdAt: Date // Date this user first visited the site

      // How long has this visitor been around?
      def age: Long = new Date().getTime - createdAt.getTime
    }

    /**
      * Visitorは2つの抽象メソッドを規定しています。
      * TODO: たぶｎidとcreatedAt
      *
      * つまり、実装を持たず、拡張クラスで実装しなければならないメソッドです。それがidとcreatedAtです。
      * また、具体的なメソッドであるageも定義していますが、これは抽象的なメソッドの1つを使って定義されています。
      *
      * Visitorは2つのクラスのビルディングブロックとして使われています。AnonymousとUserです。
      * 各クラスはVisitorを継承しており、Visitorのすべてのフィールドとメソッドを継承しています。
      */
    import Chapter4.chapter41.chapter411.Anonymous

    val anon = Anonymous("anon1")
    // anon: Anonymous = Anonymous(anon1,Mon Jul 06 10:51:40 UTC 2020)

    anon.createdAt
    // res7: java.util.Date = Mon Jul 06 10:51:40 UTC 2020

    anon.age
    // res8: Long = 65

    /**
      * idとcreatedAtは抽象的なので、拡張クラスで定義する必要があります。
      * 私たちのクラスは，これらをdefではなくvalとして実装しています．
      * これは，def を val※6 のより一般的なバージョンとみなす Scala では，合法です．
      * 具体的な実装では，必要に応じてdefやvalを使って実装することができます。
      */
    /**
    * - ※6
    * これは、オブジェクトリテラルの演習で見たユニフォームアクセスの原則の一部です。
    */
  }

  object chapter413 {

    /**
      * ### 4.1.3 Take Home Points
      * クラスがオブジェクトを抽象化する手段であるのと同様に、traitは類似した特性を持つクラスを抽象化する手段です。
      *
      * Traitsを使うには2つの部分があります。トレイトの宣言
      */
//    trait TraitName {
//      declarationOrExpression ...
//    }

    /**
      * と、クラス（通常はケースクラス）からtraitを拡張します
      */
//    case class Name(...) extends TraitName {
//      ...
//    }
  }

  object chapter414 {

    /**
      * ### 4.1.4 Exercises
      */
    object chapter4141 {

      /**
        * #### 4.1.4.1 Cats, and More Cats
        * Cat Simulator 1.0の需要は爆発的に増加しています。
        * v2では、ネコに加えて、トラ、ライオン、パンサーをモデル化する予定です。
        * Felineというtraitを定義し、すべての異なる種をFelineのサブタイプとして定義します。面白くするために、次のように定義します。
        *
        * - Felineには従来通りの色を。
        * - ネコの場合は「ニャー」、それ以外のネコの場合は「ロアー」と呼ばれる。
        * - Catだけが好きな食べ物を持っています。
        * - LionsにはInt maneSizeがあります。
        */
//      trait Feline {
//        def colour: String
//        def sound: String
//      }
//      case class Cat(colour: String, sound: String = "meow", food: String) extends Feline
//      case class Tiger(colour: String, sound: String = "roar") extends Feline
//      case class Lion(colour: String, sound: String = "roar", maneSize: Int) extends Feline
//      case class Panther(colour: String, sound: String = "roar") extends Feline

      /**
        * 模範
        */
      /**
        * これはほとんど、traitの構文に慣れるための指の運動ですが、ソリューションにはいくつかの興味深い点があります。
        */
      trait Feline {
        def colour: String
        def sound: String
      }

      case class Lion(colour: String, maneSize: Int) extends Feline {
        val sound = "roar"
      }

      case class Tiger(colour: String) extends Feline {
        val sound = "roar"
      }

      case class Panther(colour: String) extends Feline {
        val sound = "roar"
      }

      case class Cat(colour: String, food: String) extends Feline {
        val sound = "meow"
      }

      /**
        * soundはコンストラクタの引数として定義されていないことに注意してください。
        * これは定数なので、ユーザーが修正する機会を与えることは意味がありません。
        * soundの定義には重複する部分が多くあります。Felineのデフォルト値を次のように定義することができます。
        */
//      trait Feline {
//        def colour: String
//        def sound: String = "roar"
//      }
      /**
        * これは一般的には悪い習慣です。デフォルトの実装を定義するのであれば、すべてのサブタイプに適した実装にすべきです。
        *
        * もうひとつの方法は、音を「roar」と定義するBigCatと呼ばれる中間型を定義することです。これはより良い解決策です。
        */
      trait BigCat extends Feline {
        override val sound = "roar"
      }

//      case class Tiger(...) extends BigCat
//      case class Lion(...) extends BigCat
//      case class Panther(...) extends BigCat
    }

    object chapter4142 {

      /**
        * #### 4.1.4.2 Shaping Up With Traits
        * Shapeというtraitを定義し、3つの抽象メソッドを与えます。
        *
        * - sidesは、辺の数を返します。
        * - perimeterは、辺の長さの合計を返します。
        * - areaは面積を返します。
        *
        * Shapeを3つのクラスで実装します。Circle、Rectangle、Squareの3つのクラスでShapeを実装します。
        * それぞれのクラスに3つのメソッドの実装を用意します。
        * 各Shapeのメインコンストラクタのパラメータ（例：円の半径）がフィールドとしてアクセスできることを確認してください。
        *
        * ヒント：πの値はmath.Piとしてアクセスできます。
        */
      trait Shape {
        def slides: Int
        def perimeter: Int
        def area: Int
      }

      case class Circle(radius: Int) extends Shape
      case class Rectangle
      case class Square

    }
  }
}
