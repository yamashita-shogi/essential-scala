package Chapter4

import scala.annotation.tailrec

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
  * この章では，まず traits の技術的な側面に注目します。
  * その後，自分の考えを表現する手段としてScalaを使うことに焦点を当てます。
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
      * まず、traitの例を挙げてみましょう。あるウェブサイトへの訪問者をモデル化しているとします。
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
      * ここには明らかな重複があり、同じ定義を2度書かなくて済むのはありがたいことです。
      * しかし、もっと重要なことは、2種類の訪問者に共通の型を作ることです。
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
      def id: String // 各ユーザーに割り当てられたユニークなID

      def createdAt: Date // このユーザーが初めてサイトを訪れた日

      // このお客様はいつからいらっしゃるのでしょうか？
      def age: Long = new Date().getTime - createdAt.getTime
    }

    /**
      * Visitorは2つの抽象メソッドを規定しています。
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
        * soundの定義には重複する部分が多くあります。
        * Felineのデフォルト値を次のように定義することができます。
        */
//      trait Feline {
//        def colour: String
//        def sound: String = "roar"
//      }
      /**
        * これは一般的には悪い習慣です。
        * デフォルトの実装を定義するのであれば、すべてのサブタイプに適した実装にすべきです。
        *
        * もうひとつの方法は、soundを「roar」と定義するBigCatと呼ばれる中間型を定義することです。
        * これはより良い解決策です。
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
//      trait Shape {
//        def slides: Int
//        def perimeter: Int
//        def area: Double
//      }

//      case class Circle(radius: Int) extends Shape {
//        val slides = 0
//        val perimeter = 0
//        val area = radius * radius * math.Pi
//      }
//
//      case class Rectangle() extends Shape {
//        val slides = 3
//        val perimeter = 0
//        val area = 1.0
//
//      }
//      case class Square() extends Shape {
//        val slides = 0
//        val perimeter = 0
//        val area = 1.0
//      }

      /**
        * 模範
        */
      trait Shape {
        def sides: Int
        def perimeter: Double
        def area: Double
      }

      case class Circle(radius: Double) extends Shape {
        val sides = 1
        val perimeter = 2 * math.Pi * radius
        val area = math.Pi * radius * radius
      }

      // 正方形
      case class Rectangle(width: Double, height: Double) extends Shape {
        val sides = 4
        val perimeter = 2 * width + 2 * height
        val area = width * height
      }

      // 四角形
      case class Square(size: Double) extends Shape {
        val sides = 4
        val perimeter = 4 * size
        val area = size * size
      }
    }

    object chapter4143 {

      /**
        * #### 4.1.4.3 Shaping Up 2 (Da Streets)
        * 前回の解答では、3つの異なるタイプのtraitが提示されました。
        * しかし、3つのタイプの関係を正しくモデル化していません。
        * Squareは単なるShapeではなく、幅と高さが同じであるRectangleのタイプでもあります。
        *
        * SquareとRectangleが共通の型であるRectangularのサブタイプであるように、練習問題の解答をリファクタリングしてください。
        * ヒント trait は別の trait を拡張することができます。
        */
//      trait Shape {
//        def sides: Int
//        def perimeter: Double
//        def area: Double
//      }
//
//      trait Rectangular extends Shape {
//        override val sides = 4
//      }
//
//      case class Circle(radius: Double) extends Shape {
//        val sides = 1
//        val perimeter = 2 * math.Pi * radius
//        val area = math.Pi * radius * radius
//      }
//
//      // 正方形
//      case class Rectangle(width: Double, height: Double) extends Rectangular {
//        val perimeter = 2 * width + 2 * height
//        val area = width * height
//      }
//
//      // 四角形
//      case class Square(size: Double) extends Rectangular {
//        val perimeter = 4 * size
//        val area = size * size
//      }

      /**
        * 模範
        */
      /**
        * 新しいコードは以下のようになります。
        */
      // trait Shape ...
      trait Shape {
        def sides: Int
        def perimeter: Double
        def area: Double
      }

      // case class Circle ...

      // TODO: sealed
      sealed trait Rectangular extends Shape {
        def width: Double
        def height: Double
        val sides = 4
        override val perimeter = 2 * width + 2 * height
        override val area = width * height
      }

      case class Square(size: Double) extends Rectangular {
        val width = size
        val height = size
      }

      // TODO: val
      case class Rectangle(val width: Double, val height: Double) extends Rectangular
    }

    /**
    * traitがsealedされていることを確認してください。
    * そうすれば、Rectangular型やShape型のオブジェクトを扱うコードを書いても、コンパイラがその網羅性（exhaustiveness）をチェックすることができます。
    */
  }
}

object chapter42 {

  /**
    * ## 4.2 This or That and Nothing Else: Sealed Traits
    * 多くの場合、あるtraitを拡張することができるすべての可能なクラスを列挙することができます。
    * 例えば、私たちは以前、ウェブサイトの訪問者をAnonymousかLogin Userとしてモデル化しました。
    * この2つのケースは、一方が他方の否定であるため、すべての可能性を網羅しています。
    * このケースを sealed trait でモデル化すると、コンパイラが追加のチェックをしてくれるようになります。
    *
    * sealed trait を作成するには、 trait 宣言の前に sealed と書くだけです。
    */
  //  import java.util.Date
  //
  //  sealed trait Visitor {
  //    def id: String
  //    def createdAt: Date
  //    def age: Long = new Date().getTime() - createdAt.getTime()
  //  }
  //
  //  case class Anonymous(id: String, createdAt: Date = new Date()) extends Visitor
  //  case class User(id: String, email: String, createdAt: Date = new Date()) extends Visitor

  /**
    * trait を sealed とするときは、そのサブタイプをすべて同じファイルで定義しなければなりません。
    * traitがsealedされると、コンパイラはサブタイプの完全なセットを知ることができ、パターンマッチ式にケースが欠けていると警告してくれます。
    */
  //  import Chapter4.chapter41.chapter411.User

  // TODO: 実行じゃ再現しないか？
  //  def missingCase(v: Visitor) =
  //    v match {
  //      case User(_, _, _) => "Got a user"
  //    }
  //  // <console>:17: warning: match may not be exhaustive.
  //  // It would fail on the following input: Anonymous(_, _)
  //  //          v match {
  //  //          ^
  //  // error: No warnings can be incurred under -Xfatal-warnings.

  // sealed traitをextendsしているcase classを網羅していないといけない

  /**
    * 封印されていない trait で同様の警告が出ることはありません。
    *
    * sealed trait のサブタイプは、それらが定義されているファイルの外で拡張することができます。
    * 例えば、UserやAnonymousをさらに別の場所で拡張することができます。
    * この可能性を防ぎたいのであれば、ファイル内での拡張を許可する場合はsealed、すべての拡張を許可しない場合はfinalと宣言する必要があります。
    * 訪問者の例では、UserやAnonymousの拡張を許可することはおそらく意味がないので、簡略化したコードは次のようになります。
    */
  sealed trait Visitor {
    /* ... */
  }

  final case class User( /* ... */ ) extends Visitor

  final case class Anonymous( /* ... */ ) extends Visitor

  /**
    * これは非常に強力なパターンで、頻繁に使用することになるでしょう。
    */
  /**
    * ---
    *
    * **Sealed Trait Pattern**
    * traitのすべてのサブタイプがわかっている場合は、traitを封印します
    */
  //  sealed trait TraitName {
  //    ...
  //  }

  /**
    * サブタイプを拡張するケースがない場合、サブタイプを final にすることを検討する。
    */
  //  final case class Name(...) extends TraitName {
  //    ...
  //  }

  /**
    * サブタイプはsealed traitと同じファイルで定義しなければならないことを覚えておいてください。
    *
    * ---
    */
  object chapter421 {

    /**
      * ### 4.2.1 Take home points
      * sealedされたtraitとfinal（ケース）クラスによって、型の拡張性をコントロールすることができます。
      * 大部分のケースでは、sealed trait / final case class パターンを使用する必要があります。
      */
    //    sealed trait TraitName { ... }
    //    final case class Name(...) extends TraitName

    /**
    * このパターンの主な利点は次のとおりです。
    * - パターン・マッチングでケースを見落とした場合、コンパイラが警告してくれること。
    * - 密封されたtraitの拡張ポイントを制御できるので、サブタイプの動作をより強く保証できる。
    */
  }

  object chapter422 {

    /**
      * ### 4.2.2 Exercises
      */
    object chapter4221 {

      /**
        * #### 4.2.2.1 Printing Shapes
        * Shapesの例をもう一度見てみましょう。
        * まず Shape を sealed trait にします。
        * そして、Shapeを引数に取り、その説明をコンソールに返すapplyメソッドを持つDrawというシングルトンオブジェクトを書きます。
        * 例えば、以下のようになります。
        */
      //      Draw(Circle(10))
      //      // res1: String = A circle of radius 10.0cm
      //
      //      Draw(Rectangle(3, 4))
      //      // res2: String = A rectangle of width 3.0cm and height 4.0cm

      /**
        * 最後に、case句をコメントアウトするとコンパイラが文句を言うことを確認します。
        */
      //      sealedにすると
      sealed trait Shape {
        def sides: Int

        def perimeter: Double

        def area: Double
      }

      case class Circle(radius: Double) extends Shape {
        val sides = 1
        val perimeter = 2 * math.Pi * radius
        val area = math.Pi * radius * radius
      }

      sealed trait Rectangular extends Shape {
        def width: Double

        def height: Double

        val sides = 4
        override val perimeter = 2 * width + 2 * height
        override val area = width * height
      }

      case class Square(size: Double) extends Rectangular {
        val width = size
        val height = size
      }

      case class Rectangle(val width: Double, val height: Double) extends Rectangular

      //      object Draw {
      //        def apply(shape: Shape): String = shape match {
      //          case Circle(x)       => s"A circle of radius ${x}cm"
      //          case Square(x)       => s"A Square of width and height ${x}cm"
      //          case Rectangle(x, y) => s"A rectangle of width ${x}cm and height ${y}cm"
      //        }
      //      }

      /**
        * 模範
        */
      object Draw {
        def apply(shape: Shape): String = shape match {
          case Rectangle(width, height) =>
            s"A rectangle of width ${width}cm and height ${height}cm"

          case Square(size) =>
            s"A square of size ${size}cm"

          case Circle(radius) =>
            s"A circle of radius ${radius}cm"
        }

        /**
        * - `まず Shape を sealed trait にします。`　の模範は無かった
        * - 文句を言われる　のところが分からなかった
        */
      }
    }

    object chapter4222 {

      /**
        * #### 4.2.2.2 The Color and the Shape
        * shapeをより面白くするために、sealed traitのColorを書きます。
        * - ColorのRGB値に3つのプロパティを与えます。
        * - 3つの定義済みカラーを作成します。Red、Yellow、Pinkの3つの定義済みカラーを作成します。
        * - 独自のRGB値を持つ独自のカラーを作成する手段を提供する。
        * - カラーが「明るい」か「暗い」かを判断する手段を提供する。
        *
        * この演習では、多くの部分が意図的に自由に解釈できるようになっています。
        * 重要なのは、trait、クラス、オブジェクトを使って作業をすることを練習することです。
        *
        * 色をどのようにモデル化するか、何が明るい色か暗い色かなどの決定は、あなた自身に任せるか、他のクラスのメンバーと話し合って決めてください。
        *
        * Shapeとそのサブタイプのコードを編集して、各シェイプに色を追加します。
        *
        * 最後に、Draw.apply のコードを更新して、引数の色とその形や寸法をprintします。
        *
        * - 引数が定義済みの色の場合は、その色の名前を表示します。
        */
      //      Draw(Circle(10, Yellow))
      //      // res8: String = A yellow circle of radius 10.0cm

      /**
        * - 引数が定義済みの色ではなくカスタムカラーの場合は、代わりに「light」または「dark」と表示します。
        *
        * ヘルパーメソッドで色を処理するのもいいかもしれません。
        */
      // TODO: - 引数が定義済みの色の場合は、その色の名前を表示します。 -> 名前どうやって判別するか？

      /**
        * 途中まで
        */
      //      // - ColorのRGB値に3つのプロパティを与えます。
      //      // - 3つの定義済みカラーを作成します。Red、Yellow、Pinkの3つの定義済みカラーを作成します。
      //      sealed trait Color {
      //        def R: Int
      //        def G: Int
      //        def B: Int
      //        def bright: String
      //      }
      //
      //      case class Red() extends Color {
      //        val R = 255
      //        val G = 0
      //        val B = 0
      //        val bright = "light"
      //      }
      //
      //      case class Yellow() extends Color {
      //        val R = 255
      //        val G = 241
      //        val B = 0
      //        val bright = "light"
      //      }
      //
      //      case class Pink() extends Color {
      //        val R = 234
      //        val G = 145
      //        val B = 152
      //        val bright = "light"
      //      }
      //
      //      //- 独自のRGB値を持つ独自のカラーを作成する手段を提供する。
      //      // 独自ってどうやって判断するんだろう
      //      case class Custom() extends Color {
      //        val R = 0
      //        val G = 0
      //        val B = 0
      //        val bright = "light"
      //      }
      //
      //      // Shapeとそのサブタイプのコードを編集して、各シェイプに色を追加します。
      //      sealed trait Shape {
      //        def sides: Int
      //        def perimeter: Double
      //        def area: Double
      //        def color: Color
      //      }
      //
      //      case class Circle(radius: Double, val color: Color) extends Shape {
      //        val sides = 1
      //        val perimeter = 2 * math.Pi * radius
      //        val area = math.Pi * radius * radius
      //      }
      //
      //      sealed trait Rectangular extends Shape {
      //        def width: Double
      //
      //        def height: Double
      //
      //        val sides = 4
      //        override val perimeter = 2 * width + 2 * height
      //        override val area = width * height
      //      }
      //
      //      case class Square(size: Double, val color: Color) extends Rectangular {
      //        val width = size
      //        val height = size
      //      }
      //
      //      case class Rectangle(val width: Double, val height: Double, val color: Color)
      //          extends Rectangular
      //
      //      // - 引数が定義済みの色の場合は、その色の名前を表示します。
      //      // 色の名前の判定どうするんだろう
      ////      object Draw {
      ////        def apply(shape: Shape): String = shape match {
      ////          case Rectangle(width, height, color) =>
      ////            s"A ${color} rectangle of width ${width}cm and height ${height}cm"
      ////          case Square(size, color)   => s"A ${color} square of size ${size}cm"
      ////          case Circle(radius, color) => s"A ${color} circle of radius ${radius}cm"
      ////        }
      ////      }

      /**
        * 模範
        */
      /**
        * この問題に対する1つの解決策を以下に示します。実装の詳細は重要ではありませんが、正しい解答のためには重要な点があることを覚えておいてください。
        *
        * - sealed trait Color が必要です。
        *   - この trait には、RGB 値に対する 3 つの def メソッドが含まれていなければなりません。
        *   - このtraitには、RGB値で定義されたisLightメソッドが含まれている必要があります。
        *     - 定義済みの色を表す3つのオブジェクトが必要です。
        *   - 各オブジェクトはColorを継承している必要があります。
        *   - 各オブジェクトは RGB 値を vals としてオーバーライドしなければなりません。
        *   - オブジェクトをfinalとすることは任意です。
        *   - オブジェクトをケースオブジェクトにすることも任意です。
        *     - カスタムカラーを表すクラスを用意する必要があります。
        *   - このクラスはColorを継承していなければなりません。
        *   - クラスをfinalとすることは任意です。
        *   - クラスをケースクラスにすることは任意です（ただし、強く推奨します）。
        *     - Drawには理想的には2つのメソッドがあるべきです。
        *   - 1つのメソッドはパラメータとしてColorを受け取り、もう1つはShapeを受け取ります。
        *   - メソッドの名前は重要ではありません。
        *   - 各メソッドは、与えられた値に対してマッチを実行し、考えられるすべてのサブタイプをカバーするために十分なケースを提供する必要があります。
        *     - コードベース全体がコンパイルされ、テストされたときに適切な値を生成する必要があります。
        */
      // シェイプはカラーを使うので、まずカラーを定義します。
      sealed trait Color {
        // RGB値を0.0～1.0の倍数で保存することにしました。
        //
        // 抽象的なメンバーを `defs` として定義することは常に良い習慣です。
        // そうすれば、`defs`, `vals` または `vars` で実装することができます。
        // TODO: defが良いのかも
        def red: Double

        def green: Double

        def blue: Double

        // 私たちは、以下の条件を満たす色を「明るい色」と定義しました。
        // 平均RGB値が0.5以上の色とした。
        def isLight = (red + green + blue) / 3.0 > 0.5

        def isDark = !isLight
      }

      case object Red extends Color {
        // ここでは，RGB値を `vals` として実装しています．
        // 値が変更できないためです。
        val red = 1.0
        val green = 0.0
        val blue = 0.0
      }

      case object Yellow extends Color {
        // ここでは，RGB値を `vals` として実装しています．
        // 値が変更できないためです。
        val red = 1.0
        val green = 1.0
        val blue = 0.0
      }

      case object Pink extends Color {
        // ここでは，RGB値を `vals` として実装しています．
        // 値が変更できないためです。
        val red = 1.0
        val green = 0.0
        val blue = 1.0
      }

      // ここでの case クラスへの引数は，`val` 宣言を生成します．
      // `Color` の RGB メソッドを実装した `val` 宣言を生成します。
      final case class CustomColor(red: Double, green: Double, blue: Double) extends Color

      // 前の演習のコードがほぼそのまま反映されています。
      // ただし、`Shape` とそのサブタイプに `color` フィールドを追加しています。
      sealed trait Shape {
        def sides: Int

        def perimeter: Double

        def area: Double

        def color: Color
      }

      final case class Circle(radius: Double, color: Color) extends Shape {
        val sides = 1
        val perimeter = 2 * math.Pi * radius
        val area = math.Pi * radius * radius
      }

      sealed trait Rectangular extends Shape {
        def width: Double

        def height: Double

        val sides = 4
        val perimeter = 2 * width + 2 * height
        val area = width * height
      }

      final case class Square(size: Double, color: Color) extends Rectangular {
        val width = size
        val height = size
      }

      final case class Rectangle(
          width: Double,
          height: Double,
          color: Color
      ) extends Rectangular

      // `Shape` と `Color` に対しては、`Draw.apply` メソッドをオーバーロードすることにしました。
      // `Color` のコードを別の場所で直接利用したいという理由から、`Shape` と `Color` の `Draw.apply` メソッドをオーバーロードすることにしました。
      // 他の場所で直接利用したいと考えたからです。
      object Draw {
        def apply(shape: Shape): String = shape match {
          case Circle(radius, color) =>
            s"A ${Draw(color)} circle of radius ${radius}cm"

          case Square(size, color) =>
            s"A ${Draw(color)} square of size ${size}cm"

          case Rectangle(width, height, color) =>
            s"A ${Draw(color)} rectangle of width ${width}cm and height ${height}cm"
        }

        def apply(color: Color): String = color match {
          // 定義済みのColorsのそれぞれを特別なケースで扱います。
          case Red    => "red"
          case Yellow => "yellow"
          case Pink   => "pink"
          case color  => if (color.isLight) "light" else "dark"
        }
      }

      // Test code:

      Draw(Circle(10, Pink))
      // res29: String = A pink circle of radius 10.0cm

      Draw(Rectangle(3, 4, CustomColor(0.4, 0.4, 0.6)))
      // res30: String = A dark rectangle of width 3.0cm and height 4.0cm

    }

    object chapter4223 {

      /**
        * #### 4.2.2.3 A Short Division Exercise
        * 優れたScala開発者は、データのモデル化に型を使うだけではありません。
        * 型は、プログラムでミスをしないように、人工的な制限を設けるための素晴らしい方法です。
        * このエクササイズでは、ゼロ除算のエラーを防ぐために型を使用するという、簡単な例を見てみましょう（作為的ではありますが）。
        *
        * ゼロ除算は、例外が発生しやすい厄介な問題です。JVMは浮動小数点の除算に関してはカバーしていますが、整数の除算はまだ問題があります。
        */
//      1.0 / 0.0
//      // res31: Double = Infinity

//      1 / 0
//      // java.lang.ArithmeticException: / by zero
//      //   ... 1024 elided

      /**
        * 型を使ってこの問題を解決してみましょう。
        *
        * 2 つの Ints を受け取り、DivisionResult を返す apply メソッドを持つ divide というオブジェクトを作成します。
        * DivisionResult は、有効な除算の結果をカプセル化した Finite 型と、0 で除算した結果を表す Infinite 型の 2 つのサブタイプを持つ sealed trait でなければなりません。
        *
        * 以下に使用例を示します。
        */
//      val x = divide(1, 2)
//      // x: DivisionResult = Finite(0)
//
//      val y = divide(1, 0)
//      // y: DivisionResult = Infinite

      /**
        * 最後に、divideを呼び出し、その結果を照合し、適切な説明を返すサンプルコードを書いてください。
        */
////      sealed trait DivisionResult {
////        def finite: Finite
////        def infinite: Infinite
////      }
//      sealed trait DivisionResult
//      case class Finite(a: Int) extends DivisionResult
//      case object Infinite extends DivisionResult
//
//      object divide {
//        def apply(a: Int, b: Int): DivisionResult = (a, b) match {
//          case (_, 0) => Infinite
//          case (a, b) => Finite(a / b)
//        }
//      }

      /**
        * 模範
        */
      /**
        * これがそのコードです。
        */
      sealed trait DivisionResult
      final case class Finite(value: Int) extends DivisionResult
      case object Infinite extends DivisionResult

      object divide {
        def apply(num: Int, den: Int): DivisionResult =
          if (den == 0) Infinite else Finite(num / den)
      }

      divide(1, 0) match {
        case Finite(value) => s"It's finite: ${value}"
        case Infinite      => s"It's infinite"
      }
      // res34: String = It's infinite

      /**
      * divide.applyの結果はDivisionResultで、これは2つのサブタイプを持つsealed traitです。
      * サブタイプの Finite は結果をカプセル化したケースクラスですが、サブタイプの Infinite は単にオブジェクトにすることができます。
      * ここでは、Finite と同等に case オブジェクトを使用しています。
      *
      * divide.applyの実装はシンプルで、テストを実行して結果を返します。
      * ScalaはInfiniteとFiniteの最小上限としてDivisionResult型を推論することができます．
      *
      * 最後に，このマッチは，括弧を使った case class パターンと，括弧を使わない case object パターンを示しています．
      */
    }
  }

  def main(args: Array[String]): Unit = {
    println("chapter42")

    //    import Chapter4.chapter42.chapter422.chapter4221.{Circle, Draw, Rectangle}
    //    println(Draw(Circle(10)))
    //    println(Draw(Rectangle(3, 4)))

//    import Chapter4.chapter42.chapter422.chapter4222._
//    println(Draw(Circle(10, Pink)))
//    // res29: String = A pink circle of radius 10.0cm
//
//    println(Draw(Rectangle(3, 4, CustomColor(0.4, 0.4, 0.6))))
//    // res30: String = A dark rectangle of width 3.0cm and height 4.0cm

    import Chapter4.chapter42.chapter422.chapter4223._
    println(divide(1, 2))
    println(divide(1, 0))
  }
}

object chapter43 {

  /**
    * ## 4.3 Modelling Data with Traits
    * このセクションでは、言語機能からプログラミングパターンへと焦点を移すことにします。
    * ここでは、データのモデル化に注目し、論理的なorとandで定義されるデータモデルをScalaで表現するプロセスを学びます。
    * オブジェクト指向プログラミングの用語を使って、is-aとhas-aの関係を表現します。
    * 関数型プログラミングの用語では、sum型とproduct型を学びます。これらはまとめて代数的データ型と呼ばれます。
    *
    * このセクションの目的は、データモデルをScalaのコードに変換する方法を確認することです。
    * 次のセクションでは，代数的なデータ型を使用するコードのパターンを見ていきます．
    */
  object chapter431 {

    /**
      * ### 4.3.1 The Product Type Pattern
      * 最初のパターンは、他のデータを含むデータをモデル化することです。
      * これを「AはBとCを持っている」と表現することがあります。
      * 例えば、猫には色と好きな食べ物があり、訪問者にはIDと作成日がある、といった具合です。
      *
      * これを記述するには、ケースクラスを使用します。
      * これは演習で何度も行ってきたことですが、今回はこのパターンを正式なものにします。
      */

    /**
      * ---
      *
      * **Product Type Pattern**
      * Aが b（タイプB）と c（タイプC）を持つ場合、次のように書きます。
      */
//    case class A(b: B, c: C)
    /**
      * or
      */
//    trait A {
//      def b: B
//      def c: C
//    }
    /**
    *
    *  ---
    */
  }
}

object chapter44 {

  /**
    * ## 4.4 The Sum Type Pattern
    * 次のパターンは、2つ以上の異なるケースがあるデータをモデル化することです。
    * これを「AはBかCである」と表現することがあります。
    * 例えば、FelineはCat、Lion、Tigerのいずれか、VisitorはAnonymous、Userのいずれかといった具合です。
    *
    * これは、sealed trait / final case class パターンを使って記述します。
    */
  /**
    * ---
    *
    * **Sum Type Pattern**
    * AがBまたはCの場合は、次のように書きます。
    */
//  sealed trait A
//  final case class B() extends A
//  final case class C() extends A
  /**
    *
    * ---
    */
  object chapter441 {

    /**
    * ### 4.4.1 Algebraic Data Types
    * 代数的なデータ型とは、上記の2つのパターンを使用するデータのことです。
    * 関数型プログラミングの文献では、「has-a and」パターンを使ったデータをproduct型、
    * 「is-a or」パターンを使ったデータをsum型と呼んでいます。
    */
  }

  object chapter442 {

    /**
      * ### 4.4.2 The Missing Patterns
      * これまで、「is-a/has-a」と「and/or」という2つの次元で関係性を見てきました。
      * ちょっとした表を作ってみると、4つの表のセルのうち2つのセルにしかパターンがないことがわかります。
      *
      * missing twoのパターンはどうでしょうか？
      * 「is-a and」パターンは、AがBとCであることを意味します。
      * このパターンは、ある意味でsum型パターンの逆バージョンであり、次のように実装できます。
      */
//    trait B
//    trait C
//    trait A extends B with C

    /**
      * Scalaでは、A extends B with C with Dなどのように、withキーワードを使って、1つのtraitを好きなだけ拡張することができます。
      * このコースでは，このパターンを使うことはありません。
      * あるデータが複数の異なるインターフェースに準拠していることを表現したい場合、多くの場合、型クラスを使用した方が良いでしょう。
      * これについては後ほど説明します。しかし、このパターンにはいくつかの正当な使い方があります。
      *
      * - モジュール化のために、Cakeパターンと呼ばれるものを使用する。
      * - 複数のクラスで実装を共有する場合、メインの trait でデフォルトの実装を行うのは意味がありません。
      *
      * 「has-a or」パターンは、AがBまたはCを持っていることを意味しています。
      * AはD型のdを持っていて、DはBかCである」と言えば、2つのパターンを機械的に適用してこれを実装することができます。
      */
//    trait A {
//      def d: D
//    }
//    sealed trait D
//    final case class B() extends D
//    final case class C() extends D

    /**
      * 別の方法として、AはDまたはEであり、DにはBがあり、EにはCがあります。これもコードに直接変換されます
      */
//    sealed trait A
//    final case class D(b: B) extends A
//    final case class E(c: C) extends A
  }

  object chapter443 {

    /**
    * ### 4.4.3 Take Home Points
    * "has-a and" と "is-a or "のパターン（より簡潔に言えば、product型とsum型）を使ったデータを機械的にScalaのコードに変換できることを見てきました。
    * このようなデータは、代数的データ型として知られています。
    * これらのパターンを理解することは、慣用的なScalaのコードを書くために非常に重要です。
    */
  }

  object chapter444 {

    /**
      * ### 4.4.4 Exercises
      */
    object chapter4441 {

      /**
        * #### 4.4.4.1 Stop on a Dime
        */
//      sealed trait shingouki
//      final case object red extends shingouki
//      final case object green extends shingouki
//      final case object yellow extends shingouki

      /**
        * 模範
        */
      /**
        * これは、sum型パターンの直接的な応用です。
        */
      sealed trait TrafficLight
      case object Red extends TrafficLight
      case object Green extends TrafficLight
      case object Yellow extends TrafficLight
      /**
      * 3つのケースにはフィールドやメソッドがあり、複数のインスタンスを作成する必要がないため、ケースクラスではなくケースオブジェクトを使用しました。
      */
    }

    object chapter4442 {

      /**
        * #### 4.4.4.2 Calculator
        * 計算は成功（Intの結果）または失敗（Stringのメッセージ）します。これを実装します。
        */
      sealed trait Calc
      final case class succeed(value: Int) extends Calc
      final case class fail(ms: String) extends Calc

      /**
        * 模範
        */
      sealed trait Calculation
      final case class Success(result: Int) extends Calculation
      final case class Failure(reason: String) extends Calculation
    }

    object chapter4443 {

      /**
        * #### 4.4.4.3 Water, Water, Everywhere
        * Bottled waterは、size（サイズ）（Int）、source（ソース）（井戸、泉、水道）、ブール値のcarbonated（炭酸）を持っています。
        * これをScalaで実装してみましょう。
        */
//      sealed trait Source
//      final case object well extends Source
//      final case object spring extends Source
//      final case object tap extends Source
//
//      trait BottledWater {
//        def size: Int
//        def source: Source
//        def carbonated: Boolean
//      }

      /**
        * 模範
        */
      /**
        * プロダクトタイプとサムタイプのパターンのハンドルを回します。
        */
      sealed trait Source
      case object Well extends Source
      case object Spring extends Source
      case object Tap extends Source
      final case class BottledWater(size: Int, source: Source, carbonated: Boolean)
    }
  }

  def main(args: Array[String]): Unit = {
    println("chapter44")
  }
}
object chapter45 {

  /**
    * ## 4.5 Working With Data
    * 前節では、sum(or)パターンとproduct type(and)パターンの組み合わせで代数的データ型を定義する方法を見ました。
    * 本節では、構造的再帰と呼ばれる代数的データ型の使用方法を見ていきます。
    * このパターンには、ポリモーフィズムを使ったものと、パターンマッチを使ったものの2種類があります。
    *
    * 構造的再帰は、代数的データ型を構築するプロセスの正反対のものです。
    * AがBとCを持つ場合（積型パターン）、Aを作るにはBとCを持たなければなりません。
    * sum型パターンとproduct型パターンは、データを組み合わせてより大きなデータを作る方法を教えてくれます。
    * 構造的再帰とは、先ほど定義したようにAがあれば、それを構成するBとCに分けて、何らかの方法で組み合わせて目的の答えに近づけようというものです。
    * 構造的再帰は、基本的にはデータをより小さなピースに分解するプロセスです。
    *
    * 代数的なデータ型を作るのに2つのパターンがあるように、構造的再帰を使ってデータを分解するのにも2つのパターンがあります。
    * それぞれのパターンには、典型的なオブジェクト指向のスタイルであるポリモーフィズムを使ったものと、典型的な関数型のスタイルであるパターンマッチングを使ったものがあります。
    * 最後に、どちらのパターンを使うかを決めるためのルールをご紹介します。
    */
  // ポリモーフィズムとは、プログラミング言語の持つ性質の一つで、ある関数やメソッドなどが、引数や返り値の数やデータ型などの異なる複数の実装を持ち、呼び出し時に使い分けるようにできること。

  object chapter451 {

    /**
      * ### 4.5.1 Structural Recursion using Polymorphism
      * ポリモーフィックディスパッチ（Polymorphic Dispatch）、略してポリモーフィズム（Polymorphism）は、オブジェクト指向の基本的な技術です。
      * あるメソッドを trait で定義し、その trait を拡張したクラスに異なる実装がある場合、そのメソッドを呼び出すと、実際の具象インスタンスの実装が使用されます。
      * 非常に簡単な例を挙げてみましょう。まず、おなじみのsum type (or)パターンを使って簡単な定義をしてみます。
      */
//    sealed trait A {
//      def foo: String
//    }
//    final case class B() extends A {
//      def foo: String =
//        "It's B!"
//    }
//    final case class C() extends A {
//      def foo: String =
//        "It's C!"
//    }

    /**
      * A型の値を宣言しても、B型やC型の具体的な実装が使われていることがわかります。
      */
//    val anA: A = B()
//    // anA: A = B()
//
//    anA.foo
//    // res0: String = It's B!
//
//    val anA: A = C()
//    // anA: A = C()
//
//    anA.foo
//    // res1: String = It's C!

    /**
      * traitでは実装を定義し、拡張したクラスではoverrideキーワードを使って実装を変更することができます。
      */
    sealed trait A {
      def foo: String =
        "It's A!"
    }
    final case class B() extends A {
      override def foo: String =
        "It's B!"
    }
    final case class C() extends A {
      override def foo: String =
        "It's C!"
    }

    /**
      * 挙動は従来通りで、具象クラスでの実装が選択されています。
      */
    val anA: A = B()
    // anA: A = B()

    anA.foo
    // res2: String = It's B!

    /**
      * traitでデフォルトの実装を提供する場合は、その実装がすべてのサブタイプで有効であることを確認する必要があることを覚えておいてください。
      *
      * さて、ポリモーフィズムの仕組みがわかったところで、代数的なデータ型を使ってどのようにポリモーフィズムを使えばいいのでしょうか？
      * 必要なことはすべてわかりましたが、ここではそれを明示してパターンを見てみましょう。
      */

    /**
      * ---
      *
      * **The Product Type Polymorphism Pattern**
      * Aが b（タイプB）と c（タイプC）を持ち、Fを返すメソッドfを書きたい場合、通常の方法でメソッドを書くだけです。
      */
//    case class A(b: B, c: C) {
//      def f: F = ???
//    }
    /**
      * メソッドの本体では、b,c, および任意のメソッドパラメータを使用して、F型の結果を構築する必要があります。
      *
      * ---
      */

    /**
      * ---
      *
      * **The Sum Type Polymorphism Pattern**
      * AがBまたはCであり、Fを返すメソッドfを書きたい場合、A上でfを抽象メソッドとして定義し、BとCで具体的な実装を提供する。
      */
//    sealed trait A {
//      def f: F
//    }
//    final case class B() extends A {
//      def f: F =
//        ???
//    }
//    final case class C() extends A {
//      def f: F =
//        ???
//    }
    /**
    *
    *  ---
    */
  }

  object chapter452 {

    /**
      * ### 4.5.2 Structural Recursion using Pattern Matching
      * パターンマッチングによる構造的再帰は、ポリモーフィズムと同じように進められます。
      * 単純にサブタイプごとにケースを用意し、パターンマッチのケースでは興味のあるフィールドを抽出する必要があります。
      */

    /**
      * ---
      *
      * **The Product Type Pattern Matching Pattern**
      * Aにb(B型)とc(C型)があり、Aを受け取ってFを返すメソッドfを書きたい場合、次のように書きます。
      */
//    def f(a: A): F =
//      a match {
//        case A(b, c) => ???
//      }
    /**
      * メソッドの本体では、bとcを使ってF型の結果を作ります。
      *
      * ---
      */

    /**
      * ---
      *
      * **The Sum Type Pattern Matching Pattern**
      * AがBまたはCであり、Aを受け入れてFを返すメソッドfを書きたい場合、BとCのパターンマッチのケースを定義します。
      */
//    def f(a: A): F =
//      a match {
//        case B() => ???
//        case C() => ???
//      }
    /**
    *
    * ---
    */
  }

  object chapter453 {

    /**
      * ### 4.5.3 A Complete Example
      * おなじみのFelineデータ型を使って、代数的データ型と構造的な再帰パターンの完全な例を見てみましょう。
      *
      * まず、データの説明から始めます。Felineとは、ライオン、タイガー、パンサー、キャットのことです。
      * ここでは、データの説明を簡単にして、CatはStringのfavoriteFoodを持っているとします。
      * この記述から、すぐにパターンを適用してデータを定義することができます。
      */
//    sealed trait Feline
//    final case class Lion() extends Feline
//    final case class Tiger() extends Feline
//    final case class Panther() extends Feline
//    final case class Cat(favouriteFood: String) extends Feline

    /**
      * それでは、ポリモーフィズムとパターン・マッチングの両方を使ってメソッドを実装してみましょう。
      * 私たちのメソッドdinnerは、対象となるネコに適した食べ物を返します。
      * 猫の場合、夕食は彼らの好きな食べ物です。
      * ライオンの場合はカモシカ、タイガーの場合は虎の餌、パンサーの場合はリコリスとなります。
      *
      * foodをStringで表現することもできますが、もっとうまく、typeで表現することもできます。
      * そうすることで、例えば、コードのスペルミスを防ぐことができます。
      * それでは、おなじみのパターンを使って、Food型を定義してみましょう。
      */
    sealed trait Food
    case object Antelope extends Food
    case object TigerFood extends Food
    case object Licorice extends Food
    final case class CatFood(food: String) extends Food

    /**
      * では、dinnerをFoodを返すメソッドとして実装してみましょう。まずポリモーフィズムを使って
      */
    // ポリモーフィズムパターン
//    sealed trait Feline {
//      def dinner: Food
//    }
//    final case class Lion() extends Feline {
//      def dinner: Food =
//        Antelope
//    }
//    final case class Tiger() extends Feline {
//      def dinner: Food =
//        TigerFood
//    }
//    final case class Panther() extends Feline {
//      def dinner: Food =
//        Licorice
//    }
//    final case class Cat(favouriteFood: String) extends Feline {
//      def dinner: Food =
//        CatFood(favouriteFood)
//    }
// ↑OO？

    /**
      * それでは、パターン・マッチングを使ってみましょう。
      * パターン・マッチングを使う場合、実際には2つの選択肢があります。
      * Felineの単一のメソッドにコードを実装するか、別のオブジェクトのメソッドにコードを実装するかです。両方見てみましょう。
      */
    // パターンマッチング
    final case class Lion() extends Feline
    final case class Tiger() extends Feline
    final case class Panther() extends Feline
    final case class Cat(favouriteFood: String) extends Feline

    // Felineの単一のメソッドにコードを実装するか、
    // FP
    sealed trait Feline {
      def dinner: Food =
        this match {
          case Lion()             => Antelope
          case Tiger()            => TigerFood
          case Panther()          => Licorice
          case Cat(favouriteFood) => CatFood(favouriteFood)
        }
    }

    // 別のオブジェクトのメソッドにコードを実装するかです。
    object Diner {
      def dinner(feline: Feline): Food =
        feline match {
          case Lion()    => Antelope
          case Tiger()   => TigerFood
          case Panther() => Licorice
          case Cat(food) => CatFood(food)
        }
    }

    /**
    * パターンを直接適用すると、コードが出てくることに注目してください。
    * これが構造的再帰の最大のポイントです。コードはデータの形に沿って、ほとんど機械的な方法で生成されます。
    */
  }

  object chapter454 {

    /**
    * ### 4.5.4 Choosing Which Pattern to Use
    * 構造的再帰を実装する方法は3つあります。
    *
    * 1. ポリモーフィズム。
    * 2. 基本traitでのパターンマッチング
    * 3. 外部オブジェクトでのパターンマッチ（上のDinerの例のように）。
    *
    * どの方法を使えばいいのでしょうか？
    * 最初の2つの方法は、対象となるクラスで定義されたメソッドという同じ結果になります。
    * どちらか便利な方を使えばいいのです。コードの重複が少ないため、通常は基本traitのパターンマッチングを使用します。
    *
    * 興味のあるクラスにメソッドを実装する場合、そのメソッドの実装は1つだけで、そのメソッドが動作するために必要なものはすべて、クラスとメソッドに渡すパラメータに含まれていなければなりません。
    * 外部のオブジェクトにパターン・マッチングを使用してメソッドを実装する場合、
    * オブジェクトごとに複数の実装を提供することができます（上記の例では複数のDiners）。
    *
    * 一般的なルールとしては、メソッドがクラス内の他のフィールドやメソッドにのみ依存する場合は、そのクラス内で実装するのが良いでしょう。
    * メソッドが他のデータに依存している場合（例えば、dinnerを作るためにCookが必要な場合）は、問題のクラスの外でパターンマッチングを使って実装することを検討してください。
    * もし、複数の実装をしたいのであれば、パターンマッチングを使って、クラスの外で実装するべきです。
    */
  }

  object chapter455 {

    /**
      * ### 4.5.5 Object-Oriented vs Functional Extensibility
      * 古典的な関数型プログラミングのスタイルでは、オブジェクトはなく、メソッドや関数を持たないデータのみが存在します。
      * このプログラミングスタイルでは、パターンマッチングが多用されます。
      * Scalaでは、代数的なデータ型パターンと、外部オブジェクトに定義されたメソッドのパターンマッチングを使って、このスタイルを模倣することができます。
      *
      * 古典的なオブジェクト指向スタイルは、ポリモーフィズムを使用し、クラスのオープンな拡張を可能にします。
      * Scalaの用語では、これはsealed traitがないことを意味します。
      *
      * 2つの異なるスタイルでは、どのようなトレードオフがあるのでしょうか？
      *
      * 関数型スタイルの利点の1つは、コンパイラがより多くの手助けをしてくれることです。
      * traitを封印することで、コンパイラはそのtraitのすべての可能なサブタイプを知っていることになります。
      * そうすれば、パターンマッチングでケースを見落としても、コンパイラが教えてくれます。
      * これは特に、開発の後半でサブタイプを追加したり削除したりする場合に役立ちます。
      * オブジェクト指向のスタイルでも同じような利点があると言うことができます。
      * これは事実ですが、実際には多数のメソッドを持つクラスは保守が非常に困難であり、コードの一部を別のクラスにファクタリングすることになり、実質的に機能的なスタイルと重複することになります。
      *
      * だからといって、すべてのケースで関数型スタイルが好まれるわけではありません。
      * オブジェクト指向のスタイルと機能的なスタイルでは、拡張性に根本的な違いがあります。
      * OOスタイルでは、traitを拡張することで新しいデータを簡単に追加できますが、新しいメソッドを追加するには既存のコードを変更する必要があります。
      * 関数型スタイルでは、新しいメソッドを簡単に追加できますが、新しいデータを追加するには既存のコードを変更する必要があります。表形式の場合
      */

    // テーブル
    /**
      * |      | Add new method | Add new data |
      * | --- | --- | --- |
      * | OO | Change existing code | Existing code unchanged |
      * | FP | Existing code unchanged | Change existing code |
      */

    /**
    * Scalaでは，ポリモーフィズムとパターンマッチングの両方を柔軟に使用することができます。
    * しかし，一般的には，コードのセマンティクスをより確実にするために，sealed traits を使用することをお勧めします。
    * また，後述する型クラスを使用することで，OO スタイルの拡張性を得ることができます。
    */
  }

  object chapter456 {

    /**
      * ### 4.5.6 Exercises
      */
    object chapter4561 {

      /**
        * #### 4.5.6.1 Traffic Lights
        */
      /**
        * 前節では、TrafficLightのデータタイプを次のように実装しました。
        */
//      sealed trait TrafficLight
//      case object Red extends TrafficLight
//      case object Green extends TrafficLight
//      case object Yellow extends TrafficLight

      /**
        * ポリモーフィズムとパターン・マッチングを使って、
        * 標準的な赤→緑→黄→赤のサイクルで次のTrafficLightを返すnextというメソッドを実装します。
        * このメソッドは、クラスの内部と外部のどちらに実装するのが良いと思いますか？
        * 内部に実装する場合、パターン・マッチングとポリモーフィズムのどちらを使いますか？その理由は？
        */
//       ポリモーフィズム
//      sealed trait TrafficLight {
//        def next: TrafficLight
//      }
//      final case class Red() extends TrafficLight {
//        def next: TrafficLight = Green
//      }
//      final case class Green() extends TrafficLight {
//        def next: TrafficLight = Yellow
//      }
//      final case class Yellow() extends TrafficLight {
//        def next: TrafficLight = Red
//      }
//
//      // パターンマッチング
//      sealed trait TrafficLight {
//        def next: TrafficLight =
//          this match {
//            case Red    => Green
//            case Green  => Yellow
//            case Yellow => Red
//          }
//      }
//      case object Red extends TrafficLight
//      case object Green extends TrafficLight
//      case object Yellow extends TrafficLight

      /**
        * 模範
        */
      /**
        * 最初はポリモーフィズムで。
        */
//      sealed trait TrafficLight {
//        def next: TrafficLight
//      }
//      case object Red extends TrafficLight {
//        def next: TrafficLight =
//          Green
//      }
//      case object Green extends TrafficLight {
//        def next: TrafficLight =
//          Yellow
//      }
//      case object Yellow extends TrafficLight {
//        def next: TrafficLight =
//          Red
//      }

      /**
        * 今度はパターンマッチングで。
        */
      sealed trait TrafficLight {
        def next: TrafficLight =
          this match {
            case Red    => Green
            case Green  => Yellow
            case Yellow => Red
          }
      }
      case object Red extends TrafficLight
      case object Green extends TrafficLight
      case object Yellow extends TrafficLight

      /**
      * この場合、パターンマッチを使ってクラス内に実装するのがベストだと思います。
      * Nextは外部のデータに依存しないので、おそらく1つの実装しか必要ないでしょう。
      * パターン・マッチングは、ポリモーフィズムよりもステート・マシンの構造を明確にします。
      *
      * 最終的には、厳格なルールは存在せず、自分が書いている大きなプログラムの文脈の中で設計上の決定を検討しなければなりません。
      */
    }

    object chapter4562 {

      /**
        * #### 4.5.6.2 Calculation
        * 前節では、Calculationデータタイプを以下のように作成しました。
        */
//      sealed trait Calculation
//      final case class Success(result: Int) extends Calculation
//      final case class Failure(reason: String) extends Calculation

      /**
        * これから、Calculationを使ってより大きな計算を行うメソッドをいくつか書いてみましょう。
        * これらのメソッドは少々変わった形をしていますが、これはこれから学ぶことの前兆であり、パターンに従えば問題ないでしょう。
        *
        * Calculatorオブジェクトを作成します。
        * Calculatorには、CalculationとIntを受け取り、新しいCalculationを返すメソッド+と-を定義します。以下に例を示します。
        */
//      assert(Calculator.+(Success(1), 1) == Success(2))
//      assert(Calculator.-(Success(1), 1) == Success(0))
//      assert(Calculator.+(Failure("Badness"), 1) == Failure("Badness"))

      sealed trait Calculation
      final case class Success(result: Int) extends Calculation
      final case class Failure(reason: String) extends Calculation

//      object Calculator {
//        def +(calculation: Calculation, num: Int): Calculation = calculation match {
//          // 1個だけなら：Pattern type is incompatible with expected type, found: chapter4562.Success.type, required: Calculation
//          case Success(i) => Success(i + num)
//          case Failure(s) => Failure(s)
//        }
//
//        def -(calculation: Calculation, num: Int): Calculation = calculation match {
//          // 1個だけなら：Pattern type is incompatible with expected type, found: chapter4562.Success.type, required: Calculation
//          case Success(i) => Success(i - num)
//          case Failure(s) => Failure(s)
//        }
//      }

      /**
        * 模範
        */
      /**
        * 演習で求められているフレームワークを導入することから始めましょう。
        */
//      object Calculator {
//        def +(calc: Calculation, operand: Int): Calculation = ???
//        def -(calc: Calculation, operand: Int): Calculation = ???
//      }
      /**
        * 次に、構造的な再帰パターンを適用します。
        */
//      object Calculator {
//        def +(calc: Calculation, operand: Int): Calculation =
//          calc match {
//            case Success(result) => ???
//            case Failure(reason) => ???
//          }
//        def -(calc: Calculation, operand: Int): Calculation =
//          calc match {
//            case Success(result) => ???
//            case Failure(reason) => ???
//          }
//      }
      /**
        * メソッドの残りのボディを書くためには、もはやパターンに頼ることはできません。
        * しかし，少し考えればすぐに正解にたどり着くことができます．
        * 「+」と「-」は二項演算であることがわかっていますが、これを使うには2つの整数が必要です。
        * また、Calculationを返さなければならないこともわかっています。
        * 失敗例を見てみると、2つのIntsはありません。返すべき意味のある唯一の結果はFailureです。
        * Success の場合は、2 つの Ints があるので、Success を返す必要があります。これにより、次のようになります。
        */
      object Calculator {
        def +(calc: Calculation, operand: Int): Calculation =
          calc match {
            case Success(result) => Success(result + operand)
            case Failure(reason) => Failure(reason)
          }
        def -(calc: Calculation, operand: Int): Calculation =
          calc match {
            case Success(result) => Success(result - operand)
            case Failure(reason) => Failure(reason)
          }
      }

      object two {

        /**
          * 除数が0のときに失敗する除算メソッドを書いてみましょう。
          * 以下のテストはパスするはずです。最後のテストの動作に注目してください。
          * これは "fail fast" の動作を示しています。計算がすでに失敗している場合、その失敗を保持し、それ以上のデータを処理しません。
          * たとえテストのように、そうすることでまた失敗してしまう場合でもです。
//          */
//        assert(Calculator./(Success(4), 2) == Success(2))
//        assert(Calculator./(Success(4), 0) == Failure("Division by zero"))
//        assert(Calculator./(Failure("Badness"), 0) == Failure("Badness"))

        object Calculator {
//          def /(calc: Calculation, operand: Int): Calculation =
//            calc match {
//              case Success(result) =>
//                if (operand == 0) Failure("Division by zero") else Success(result / operand)
//              case Failure(reason) => Failure(reason)
//            }

          /**
            * 模範
            */
          /**
            * 1. 先ほどと同じ一般的なパターンで、フェイルファスト動作を実装するために、まずCalculationにマッチングを行います。
            * 2. Calculationにマッチした後、ゼロ除算をチェックします。
            */
          def /(calc: Calculation, operand: Int): Calculation =
            calc match {
              case Success(result) =>
                operand match {
                  case 0 => Failure("Division by zero")
                  case _ => Success(result / operand)
                }
              case Failure(reason) => Failure(reason)
            }
        }
      }
    }

    object chapter4563 {

      /**
        * #### 4.5.6.3 Email
        * 前に見た訪問者の特徴を思い出してください。
        * ウェブサイトの訪問者は、匿名またはサインインしたユーザーのどちらかです。
        * ここで、訪問者にメールを送信する機能を追加したいとします。
        * しかし、メールを送れるのはサインインしたユーザだけですし、メールを送るにはSMTPの設定やMIMEヘッダなどについて多くの知識が必要になります。
        * メール送信のメソッドは、Visitor traitのポリモーフィズムを使って実装するのと、EmailServiceオブジェクトのパターンマッチを使って実装するのと、どちらが良いでしょうか？その理由は？
        */

//      外部のサービスを使うものなので、EmailServiceオブジェクトのパターンマッチを使って実装する

      /**
        * 模範
        */
      /**
      * 私はこのメソッドをEmailServiceオブジェクトに実装します。
      * 電子メールの送信には、Visitorクラスとは関係のない詳細がたくさんあります。
      * 私はこれらの詳細を別の抽象化したものにしたいのです。
      */
    }
  }
  def main(args: Array[String]): Unit = {
    println("chapter45")

    // chapter4562
    import Chapter4.chapter45.chapter456.chapter4562.two.Calculator
    import Chapter4.chapter45.chapter456.chapter4562.{Success, Failure}
    println(assert(Calculator./(Success(4), 2) == Success(2)))
    println(assert(Calculator./(Success(4), 0) == Failure("Division by zero")))
    println(assert(Calculator./(Failure("Badness"), 0) == Failure("Badness")))
  }
}

object chapter46 {

  /**
    * ## 4.6 Recursive Data
    * 代数的なデータ型の使い方として、再帰的なデータの定義がよく挙げられます。
    * 再帰データとは、自分自身を基準にして定義されるデータで、無限の大きさのデータを作ることができます
    * （ただし、具体的なインスタンスはすべて有限です）。
    *
    * このような再帰データを定義することはできません。※7
    */
  final case class Broken(broken: Broken)

  /**
    * ---
    *
    * - ※7
    * 実際には、再帰的なケースの構築を遅らせれば、このような方法でデータを定義することができます。
    * `final case class LazyList(head: Int, tail: () => LazyList)` のようになります。
    * これは、まだ見たことのないScalaの機能である関数を使っています。
    * この構文を使えば，`val ones: LazyList = LazyList(1, () => ones)` という宣言で，無限に続くonesを定義するなど，かなりぶっ飛んだことができます．
    * このリストは限られた量しか実現しないので，他の方法では実現が難しい特定のタイプのデータを実装するのに使用できます。
    * この分野に興味のある方は、私たちが実装したものを遅延リストと呼び、特に「奇数遅延リスト」と呼ばれています。
    * 「How to add laziness to a strict language wihtout even being odd」で紹介した「even list」の方が、より優れた実装です。
    * さらに詳しく知りたい方は、lazy datastructuresに関する豊富な文献や、「coinductive data」という名の心を揺さぶる理論があります。
    *
    * ---
    */
  /**
    * このような型のインスタンスを実際に作成することはできないので、再帰は終わりません。
    * 有効な再帰データを定義するには、基本ケースを定義しなければなりません。
    *
    * ここでは、より便利な再帰的定義を示します。
    * IntListは、空のリストEndか、IntとIntListを含むペアです。※8
    * おなじみのパターンを使って、これを直接コードに変換することができます。
    */
//  sealed trait IntList
//  case object End extends IntList
//  final case class Pair(head: Int, tail: IntList) extends IntList

  /**
    * ---
    *
    * - ※8
    * この要素の伝統的な名称は、コンサ・セルです。
    * この名前は、背景にあるストーリーを知らないとちょっと混乱してしまうので、私たちは使いません。
    *
    * ---
    */
  /**
    * ここでは「End」を基本ケースとします。1,2,3を含むリストを以下のように構成します。
    */
//  Pair(1, Pair(2, Pair(3, End)))

  /**
    * このデータ構造は、シングリーリンクリスト（singly-linked list）と呼ばれています。
    * この例では、4つのリンクが連鎖しています。
    * リストの構造をより理解するために、これをもっと長い形で書き出すことができます。
    * 以下、dは空のリストを表し、a、b、cはその上に構築されたペアです。
    */
//  val d = End //()ついてたけど不要っぽい
//  val c = Pair(3, d)
//  val b = Pair(2, c)
//  val a = Pair(1, b)

  /**
    * これらのデータ構造は、連鎖のリンクであるだけでなく、すべて整数の完全なシーケンスを表します。
    * * aは，1，2，3の順序を表す
    * * bは，配列2，3を表す
    * * cはシーケンス3（1つの要素のみ）を表す
    * * dは空の配列を表す
    *
    * この実装を用いると、既存のリストに新しい要素を前置することを繰り返すことで、任意の長さのリストを構築することができます。※9
    */
  /**
    *
    * * ---
    *
    * - ※9
    * これがScalaに組み込まれたListデータ構造の仕組みです。Listについては、コレクションの章で紹介します。
    *
    * ---
    */
  /**
    * 同じ構造的な再帰パターンを適用して、再帰的な代数的データ型を処理することができます。
    * 唯一の難点は、データ定義が再帰である場合、再帰呼び出しを行わなければならないことです。
    *
    * IntListのすべての要素を加算してみましょう。
    * ここではパターンマッチを使用しますが、ご存知のようにポリモーフィズムを使用する場合も同様のプロセスとなります。
    *
    * まずはテストとメソッドの宣言から。
    */
//  val example = Pair(1, Pair(2, Pair(3, End)))
//  assert(sum(example) == 6)
//  assert(sum(example.tail) == 5)
//  assert(sum(End) == 0)
//  def sum(list: IntList): Int =
//    list match {
//      case End          => ???
//      case Pair(hd, tl) => ???
//    }

  /**
    * テストでは、0 を End リストの要素の合計と定義していることに注意してください。
    * 最終的な結果はこのベースケースに基づいて構築されるため、我々の手法に適切なベースケースを定義することが重要です。
    */
  /**
    * 次に、構造的な再帰パターンを適用して、メソッドの本体を完成させます。
    */
//  def sum(list: IntList): Int =
//    list match {
//      case End => ???
//      case Pair(hd, tl) => ???
//    }

  /**
    * 最後に、ケースの本体を決めなければなりません。
    * Endについては、0が答えであることはすでに決定しています。
    * Pairについては、2つの情報があります。Intを返さなければならないことと、tlを再帰的に呼び出す必要があることがわかっています。
    * それでは、今ある情報を入力してみましょう。
    */
//  def sum(list: IntList): Int =
//    list match {
//      case End          => 0
//      case Pair(hd, tl) => ??? sum (tl)
//    }

  /**
    * 再帰呼び出しは、定義上、リストの末尾の合計を返します。
    * したがって、この結果にhdを追加するのが正しいやり方です。これで、最終的な結果が得られます。
    */
//  def sum(list: IntList): Int =
//    list match {
//      case End          => 0
//      case Pair(hd, tl) => hd + sum(tl)
//    }

  object chapter461 {

    /**
      * ### 4.6.1 Understanding the Base Case and Recursive Case
      * しかし、基本的なケースや再帰的なケースでは、メソッド本体を用意する必要があります。
      * 一般的に使用できる指針がいくつかあります。
      *
      * 基本的なケースでは、一般的に、計算しようとしている関数の恒等式を返す必要があります。
      * アイデンティティとは、結果を変更しない要素のことです。
      * 例えば、足し算の場合、a + 0 == a for any aなので、0が恒等式になります。
      * 要素の積を計算する場合、a * 1 == a for all aなので、恒等式は1になります。
      *
      * 再帰的なケースでは、再帰が正しい結果を返すと仮定して、正しい答えを得るために何を追加する必要があるかを考えます。
      * sumでは、再帰呼び出しによってリストの末尾に正しい結果が得られると仮定して、先頭に追加するだけです。
      */
    /**
      * ---
      *
      * **Recursive Algebraic Data Types Pattern**
      * 再帰的な代数データ型を定義する場合、再帰的なケースとそうでないケースの少なくとも2つのケースが必要です。
      * 再帰的ではないケースはベースケースと呼ばれます。コードでは、一般的な骨格は
      */
    sealed trait RecursiveExample
    final case class RecursiveCase(recursion: RecursiveExample) extends RecursiveExample
    case object BaseCase extends RecursiveExample

    /**
      *
      * ---
      */

    /**
    * ---
    *
    * **Recursive Structural Recursion Pattern**
    * 再帰的な代数データ型に対して構造的に再帰的なコードを書く場合、
    * データの中で再帰的な要素に出会うたびに、自分のメソッドを再帰的に呼び出します。
    * - データ内で再帰的要素に遭遇した場合は、常にメソッドを再帰的に呼び出します。
    * - データ内でベースケースに遭遇するたびに、実行中の操作のためのアイデンティティを返す。
    *
    * ---
    */
  }

  object chapter462 {

    /**
      * ### 4.6.2 Tail Recursion
      * 再帰呼び出しがスタックスペースを過剰に消費することを懸念するかもしれません。
      * Scalaは、テールリカーションと呼ばれる最適化を多くの再帰関数に適用して、スタックスペースの消費を止めることができます。
      *
      * テールコールとは、呼び出し側が即座に値を返すメソッド呼び出しのことです。つまり、これはテールコールです。
      */
    def method1: Int =
      1

    def tailCall: Int =
      method1

    /**
      * なぜなら、tailCallはすぐにmethod1を呼び出した結果を返す一方で
      */
    def notATailCall: Int =
      method1 + 2

    /**
      * notATailCallはすぐにはリターンせず、コールの結果に数値を追加するからです。
      *
      * テールコールは、スタックスペースを使用しないように最適化することができます。
      * JVMの制限により、Scalaは、呼び出し側が自分自身を呼び出すテールコールのみを最適化します。
      * テールリカーションは維持すべき重要な特性であるため、@tailrecアノテーションを使用して、
      * テールリカーションであると思われるメソッドが本当にテールリカーションであるかどうかをコンパイラにチェックしてもらうことができます。
      * ここでは、2つのバージョンのsumがアノテーションされています。
      * ひとつは末尾再帰的で、もうひとつは末尾再帰的ではありません。
      * 末尾再帰的ではないメソッドについて、コンパイラが文句を言っているのがわかります。
      */
    import scala.annotation.tailrec
//    @tailrec
//    def sum(list: IntList): Int =
//      list match {
//        case End          => 0
//        case Pair(hd, tl) => hd + sum(tl)
//      }
//    // <console>:20: error: could not optimize @tailrec annotated method sum: it contains a recursive call not in tail position
//    //          list match {
//    //          ^

//    @tailrec
//    def sum(list: IntList, total: Int = 0): Int =
//      list match {
//        case End          => total
//        case Pair(hd, tl) => sum(tl, total + hd)
//      }
//    // sum: (list: IntList, total: Int)Int

    /**
    * 非末尾再帰関数は、上記のsumのようにアキュムレータを追加することで、末尾再帰バージョンに変換することができます。
    * これにより，スタックの割り当てがヒープの割り当てに変わりますが，これは有利な場合もあれば，不利な場合もあります．
    *
    * Scalaでは、末尾再帰関数を直接扱うことはありません。
    * なぜなら、末尾再帰が使用される最も一般的なケースをカバーする豊富なコレクションライブラリがあるからです。
    * しかし、独自のデータ型を実装したり、コードを最適化したりするために、これを超える必要がある場合は、末尾再帰について知っておくと便利です。
    */
  }

  object chapter463 {

    /**
      * ### 4.6.3 Exercises
      */
    object chapter4631 {

      /**
        * #### 4.6.3.1 A List of Methods
        * IntListの定義を使ってリストの長さを返すメソッドlengthを定義します。
        */
//      sealed trait IntList
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList
      /**
        * 下にテストデータがあるので、それを使って自分の解答を確認してください。
        * この演習では、基本traitのパターン・マッチングを使用するのがよいでしょう。
        *
        * 1問目
        */
//      val example = Pair(1, Pair(2, Pair(3, End)))
//
//      assert(example.length == 3)
//      assert(example.tail.length == 2)
//      assert(End.length == 0)

//        sealed trait IntList {
//          def length: Int
//        }
//
//        case object End extends IntList {
//          val length = 0
//        }
//        final case class Pair(head: Int, tail: IntList) extends IntList {
//          @tailrec
//          def length2(list: IntList, total: Int = 0): Int = {
//            list match {
//              case End         => total
//              case Pair(_, tl) => length2(tl, total + 1)
//            }
//          }
//
//          override def length: Int = length2(this)
//        }

      /**
        * 模範
        */
      /**
        * * 最初sealed traitにlength定義しようとしたけど、tailrecのまま移したので以下のエラー
        * `Method annotated with @tailrec is neither private nor final (so can be overridden)`
        * `Tailrecでアノテーションされたメソッドは、privateでもfinalでもない（つまりオーバーライド可能）。`
        *
        * それで↓の方法で実装したけど、結果的にはtailrecが不要だった、引数も不要
        */
//      sealed trait IntList {
//        def length: Int =
//          this match {
//            case End          => 0
//            case Pair(hd, tl) => 1 + tl.length
//          }
//      }
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList

      /**
        * IntListの要素の積を計算するメソッドを定義します。
        * テストケースは以下の通りです。
        *
        * 2問目
        */
//      assert(example.product == 6)
//      assert(example.tail.product == 6)
//      assert(End.product == 1)

//      sealed trait IntList {
//        def product: Int =
//          this match {
//            case End          => 1
//            case Pair(hd, tl) => hd * tl.product
//          }
//      }
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList

      /**
        * 模範
        */
//      sealed trait IntList {
//        def product: Int =
//          this match {
//            case End          => 1
//            case Pair(hd, tl) => hd * tl.product
//          }
//      }
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList

      /**
        * IntListの各要素の値を2倍にして、新しいIntListを返すメソッドを定義します。
        * 以下のテストケースが成立するはずです。
        *
        * 3問目
        */
//      assert(example.double == Pair(2, Pair(4, Pair(6, End))))
//      assert(example.tail.double == Pair(4, Pair(6, End)))
//      assert(End.double == End)

//      sealed trait IntList {
//        def double: IntList =
//          this match {
//            case End          => End
//            case Pair(hd, tl) => Pair(hd * 2, tl.double)
//          }
//      }
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList

      /**
        * 模範
        */
//      sealed trait IntList {
//        def double: IntList =
//          this match {
//            case End          => End
//            case Pair(hd, tl) => Pair(hd * 2, tl.double)
//          }
//      }
//      case object End extends IntList
//      final case class Pair(head: Int, tail: IntList) extends IntList
    }

    object chapter4632 {

      /**
        * #### 4.6.3.2 The Forest of Trees
        * 整数の二分木は次のように定義できる。
        * Treeとは、Int型の要素を持つ左右のTreeまたはLeafを持つNodeのことです。
        * この代数的なデータ型を実装する。
        *
        * 1問目
        */
//      sealed trait Tree
//      case class Leaf(i: Int) extends Tree
//      final case class Node(head: Tree, tail: Tree) extends Tree

      /**
        * 模範
        */
      /**
        * * Leafは引数が必要だから、case class。（コピペ元がcase objectだった）
        * * 拡張しないのでfinalつけないといけない
        */
//      sealed trait Tree
//      final case class Node(l: Tree, r: Tree) extends Tree
//      final case class Leaf(elt: Int) extends Tree
      /**
        * ポリモーフィズムとパターン・マッチングを使ってTreeのsumとdoubleを実装する。
        *
        * 2問目
        */
//      sealed trait Tree {
//        def sum: Int
//        def double: Tree = {
//          this match {
//            case Node(l, r) => Node(l.double, r.double)
//            case Leaf(x)    => Leaf(x).double
//          }
//        }
//      }
//
//      final case class Node(l: Tree, r: Tree) extends Tree {
//        def sum: Int = sum(l) + sum(r)
//
//        def sum(t: Tree, total: Int = 0): Int = {
//          t match {
//            case Node(l, r) => sum(l, total) + sum(r, total)
//            case Leaf(x)    => Leaf(x).sum
//          }
//        }
//      }
//      final case class Leaf(elt: Int) extends Tree {
//        def sum: Int = elt
//        override def double: Tree = Leaf(elt * 2)
//      }

      /**
        * 模範
        */
      /**
        * どうなってるんだろう
        * case classの方のsumとか、直接呼ばれる用？
        * TreeOosは何用？
        */
      object TreeOps {
        def sum(tree: Tree): Int =
          tree match {
            case Leaf(elt)  => elt
            case Node(l, r) => sum(l) + sum(r)
          }

        def double(tree: Tree): Tree =
          tree match {
            case Leaf(elt)  => Leaf(elt * 2)
            case Node(l, r) => Node(double(l), double(r))
          }
      }

      sealed trait Tree {
        def sum: Int
        def double: Tree
      }
      final case class Node(l: Tree, r: Tree) extends Tree {
        def sum: Int = l.sum + r.sum

        def double: Tree = Node(l.double, r.double)
      }
      final case class Leaf(elt: Int) extends Tree {
        def sum: Int = elt

        def double: Tree = Leaf(elt * 2)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println("chapter46")

    // 4.6.3.1 A List of Methods
//    import Chapter4.chapter46.chapter463.chapter4631._
//    val example = Pair(1, Pair(2, Pair(3, End)))
//
//    println(assert(example.length == 3))
//    println(assert(example.tail.length == 2))
//    println(assert(End.length == 0))

//    println(example.product)
//    assert(example.product == 6)
//    assert(example.tail.product == 6)
//    assert(End.product == 1)

//    assert(example.double == Pair(2, Pair(4, Pair(6, End))))
//    assert(example.tail.double == Pair(4, Pair(6, End)))
//    assert(End.double == End)

    import Chapter4.chapter46.chapter463.chapter4632._
    val example1 = Node(Leaf(1), Leaf(2))
    val example2 = Node(Leaf(3), Leaf(4))
    val example = Node(example1, example2)
    println(example.sum)
    println(example.double)

    println(example1.double)
  }
}

object chapter47 {

  /**
    * ## 4.7 Extended Examples
    * 代数的なデータ型や構造的な再帰のスキルを試すために、より大きなプロジェクトに挑戦してみましょう。
    */
  object chapter4701 {

    /**
      * #### 4.7.0.1 A Calculator
      * この演習では、数値演算のみを行うプログラムのための簡単なインタプリタを実装します。
      *
      * まず、操作する式を表す型をいくつか定義します。コンパイラの世界では、これを抽象構文木と呼びます。
      *
      * 私たちの表現は以下の通りです。
      * * 式とは、足し算、引き算、または数です。
      * * 足し算は、leftとrightの式を持ちます。
      * * 減算は、leftとrightの式を持ち、または
      * * NumberはDouble型のvalueを持ちます。
      *
      * これをScalaで実装してみましょう。
      * ①
      */
    // 答え見た

    /**
      * 模範
      */
    /**
      * これは、わかりやすい代数的なデータ型です。
      */
//    sealed trait Expression
//    final case class Addition(left: Expression, right: Expression) extends Expression
//    final case class Subtraction(left: Expression, right: Expression) extends Expression
//    final case class Number(value: Double) extends Expression

    /**
      * ExpressionをDoubleに変換するメソッドevalを実装します。
      * 必要に応じてポリモーフィズムやパターン・マッチングを使用してください。実装方法の選択について説明しなさい。
      *
      * ②
      */
    /**
      * 模範
      */
    /**
      * パターンマッチングを使用したのは、よりコンパクトで、コードが読みやすくなると考えたからです。
      */
//    sealed trait Expression {
//      def eval: Double =
//        this match {
//          case Addition(l, r)    => l.eval + r.eval
//          case Subtraction(l, r) => l.eval - r.eval
//          case Number(v)         => v
//        }
//    }
//    final case class Addition(left: Expression, right: Expression) extends Expression
//    final case class Subtraction(left: Expression, right: Expression) extends Expression
//    final case class Number(value: Int) extends Expression

    /**
      * ここでは、除算と平方根という失敗を呼ぶ式を追加してみます。
      * まず、抽象的な構文ツリーを拡張して、DivisionとSquareRootの表現を追加します。
      *
      * ③
      */
    // 答え見た

    /**
      * 模範
      */
//    sealed trait Expression
//    final case class Addition(left: Expression, right: Expression) extends Expression
//    final case class Subtraction(left: Expression, right: Expression) extends Expression
//    final case class Division(left: Expression, right: Expression) extends Expression
//    final case class SquareRoot(value: Expression) extends Expression
//    final case class Number(value: Double) extends Expression

    /**
      * 今度はevalを変更して，計算が失敗することがあることを表すようにしましょう．
      * (Doubleでは計算が失敗したことを示すためにNaNを使用していますが，ユーザの役に立つように，なぜ計算が失敗したのかを伝えたいと思います)．
      * 適切な代数的データ型を実装します。
      *
      * ④
      */
//    sealed trait Expression {
//      def eval: Double =
//        this match {
//          case Addition(l, r)    => l.eval + r.eval
//          case Subtraction(l, r) => l.eval - r.eval
//          case Number(v)         => v
//        }
//    }
//    final case class Addition(left: Expression, right: Expression) extends Expression
//    final case class Subtraction(left: Expression, right: Expression) extends Expression
//    final case class Division(left: Expression, right: Expression) extends Expression
//    final case class SquareRoot(value: Expression) extends Expression
//    final case class Number(value: Int) extends Expression

    /**
      * 模範
      */
    /**
      * これは、前のセクションで行ったことです。
      */
    sealed trait Calculation
    final case class Success(result: Double) extends Calculation
    final case class Failure(reason: String) extends Calculation

    /**
      * ここでevalを変更して、結果のタイプを返すようにします
      * 私の実装ではCalculationと呼んでいます。以下に例を示します。
      *
      * ⑤
      */
//    assert(Addition(SquareRoot(Number(-1.0)), Number(2.0)).eval ==  Failure("Square root of negative number"))
//    assert(Addition(SquareRoot(Number(4.0)), Number(2.0)).eval == Success(4.0))
//    assert(Division(Number(4), Number(0)).eval == Failure("Division by zero"))

    // 断念

//    sealed trait Expression {
//      def eval: Calculation =
//        this match {
//          case Addition(l, r)    => l.eval + r.eval
//          case Subtraction(l, r) => l.eval - r.eval
//          case Division(l, r)    => Failure("Square root of negative number")
//          case Number(v)         => Success(v.toDouble)
//        }
//    }
//    final case class Addition(left: Expression, right: Expression) extends Expression
//    final case class Subtraction(left: Expression, right: Expression) extends Expression
//    final case class Division(left: Expression, right: Expression) extends Expression
//    final case class SquareRoot(value: Expression) extends Expression
//    final case class Number(value: Int) extends Expression

    /**
      * 模範
      */
    /**
      * このようなパターンマッチングの繰り返しは、非常に面倒なものですよね。
      * 次のセクションでは、これを抽象化する方法を見ていきましょう。
      */
    sealed trait Expression {
      def eval: Calculation =
        this match {
          case Addition(l, r) =>
            l.eval match {
              case Failure(reason) => Failure(reason)
              case Success(r1) =>
                r.eval match {
                  case Failure(reason) => Failure(reason)
                  case Success(r2)     => Success(r1 + r2)
                }
            }
          case Subtraction(l, r) =>
            l.eval match {
              case Failure(reason) => Failure(reason)
              case Success(r1) =>
                r.eval match {
                  case Failure(reason) => Failure(reason)
                  case Success(r2)     => Success(r1 - r2)
                }
            }
          case Division(l, r) =>
            l.eval match {
              case Failure(reason) => Failure(reason)
              case Success(r1) =>
                r.eval match {
                  case Failure(reason) => Failure(reason)
                  case Success(r2) =>
                    if (r2 == 0)
                      Failure("Division by zero")
                    else
                      Success(r1 / r2)
                }
            }
          case SquareRoot(v) =>
            v.eval match {
              case Success(r) =>
                if (r < 0)
                  Failure("Square root of negative number")
                else
                  Success(Math.sqrt(r))
              case Failure(reason) => Failure(reason)
            }
          case Number(v) => Success(v)
        }
    }
    final case class Addition(left: Expression, right: Expression) extends Expression
    final case class Subtraction(left: Expression, right: Expression) extends Expression
    final case class Division(left: Expression, right: Expression) extends Expression
    final case class SquareRoot(value: Expression) extends Expression
    final case class Number(value: Int) extends Expression
  }

  object chapter4702 {

    /**
      * #### 4.7.0.2 JSON
      * calculatorの練習では、代数データ型の表現方法をお伝えしました。
      * この演習では、代数的データ型を自分で設計してもらいます。
      * ここでは、できれば馴染みのある領域で作業をしてもらいます。JSONです。
      *
      * JSONを表現する代数的データ型を設計してください。
      * 直接コードを書くのはやめましょう。
      * まず、代数的データ型の構成要素である論理的な「and」と「or」の観点から設計をスケッチしてみましょう。
      * BNFに似た記法を使うと便利かもしれません。例えば、先ほどのExpressionデータタイプを次のように表現します。
      */
    // これなに
//    Expression ::= Addition left:Expression right:Expression
//    | Subtraction left:Expression right:Expression
//    | Division left:Expression right:Expression
//    | SquareRoot value:Expression
//    | Number value:Int
    /**
      * この簡略化された記法により，Scalaの複雑な構文を気にすることなく，代数的データ型の構造に集中することができます．
      *
      * JSONをモデル化するにはシーケンス型が必要になりますが、Scalaのコレクションライブラリはまだ見ていません。
      * しかし、代数的なデータ型としてリストを実装する方法は見てきました。
      *
      * 以下は、表現できる必要のあるJSONの例です。
      */
//    ["a string", 1.0, true]
//    {
//      "a": [1,2,3],
//      "b": ["a","b","c"]
//      "c": { "doh":true, "ray":false, "me":1 }
//    }

    // わからない

    /**
      * 模範
      */
    /**
      * JSONのモデル化には様々な方法が考えられます。
      * ここでは、JSON仕様書のレールウェイ図をかなり直訳したものを紹介します。
      */
    // これなに
//  Json ::= JsNumber value:Double
//    | JsString value:String
//    | JsBoolean value:Boolean
//    | JsNull
//      | JsSequence
//      | JsObject
//      JsSequence ::= SeqCell head:Json tail:JsSequence
//    | SeqEnd
//      JsObject ::= ObjectCell key:String value:Json tail:JsObject
//    | ObjectEnd

    /**
      * 表現をScalaのコードに変換します。
      *
      * ②
      */
    // JsNull の部分が分からず放置して忘れてた。
    // `sealed trait JsSequence extends Json` この辺間違ってる　extendsしてない

//    sealed trait Json
//    final case class JsNumber(value: Double) extends Json
//    final case class JsString(value: String) extends Json
//    final case class JsBoolean(value: Boolean) extends Json
//
//    sealed trait JsSequence
//    final case class SeqCall(head: Json, tail: JsSequence) extends JsSequence
//    final case object SeqEnd extends JsSequence
//
//    sealed trait JsObject
//    final case class ObjectCell(key: String, value: Json) extends JsObject
//    final case object ObjectEnd extends JsObject

    /**
      * 模範
      */
    /**
      * これは機械的なプロセスであるべきです。これが代数的なデータ型のポイントです。
      * データをモデル化する作業を行い、コードはそのモデルから直接導かれます。
      */
//    sealed trait Json
//    final case class JsNumber(value: Double) extends Json
//    final case class JsString(value: String) extends Json
//    final case class JsBoolean(value: Boolean) extends Json
//
//    case object JsNull extends Json
//    sealed trait JsSequence extends Json
//    final case class SeqCell(head: Json, tail: JsSequence) extends JsSequence
//    case object SeqEnd extends JsSequence
//    sealed trait JsObject extends Json
//    final case class ObjectCell(key: String, value: Json, tail: JsObject) extends JsObject
//    case object ObjectEnd extends JsObject

    /**
      * 次に、JSON表現を文字列に変換するメソッドを追加します。
      * 文字列を引用符で囲み、配列やオブジェクトを適切に処理していることを確認してください。
      *
      * ③
      */
//    sealed trait Json {
//      def toString: Json = {
//        this match {
//          case JsNumber(v)  => ???
//          case JsString(v)  => ???
//          case JsBoolean(v) => ???
//          case JsNull       => ???  // 引数が無い場合の書き方わからない
//        }
//      }
//    }
//    final case class JsNumber(value: Double) extends Json
//    final case class JsString(value: String) extends Json
//    final case class JsBoolean(value: Boolean) extends Json
//
//    case object JsNull extends Json
//    sealed trait JsSequence extends Json
//    final case class SeqCell(head: Json, tail: JsSequence) extends JsSequence
//    case object SeqEnd extends JsSequence
//    sealed trait JsObject extends Json
//    final case class ObjectCell(key: String, value: Json, tail: JsObject) extends JsObject
//    case object ObjectEnd extends JsObject

    /**
      * 模範
      */
    /**
      * これは、構造的再帰の応用であり、代数的データ型の変換はすべてそうであるが、シーケンス型を特別に扱わなければならないという難点がある。これが私の解決策です。
      */
    object json {
      sealed trait Json {
        def print: String = {
          def quote(s: String): String =
            '"'.toString ++ s ++ '"'.toString
          def seqToJson(seq: SeqCell): String =
            seq match {
              case SeqCell(h, t @ SeqCell(_, _)) =>
                s"${h.print}, ${seqToJson(t)}"
              case SeqCell(h, SeqEnd) => h.print
            }

          def objectToJson(obj: ObjectCell): String =
            obj match {
              case ObjectCell(k, v, t @ ObjectCell(_, _, _)) =>
                s"${quote(k)}: ${v.print}, ${objectToJson(t)}"
              case ObjectCell(k, v, ObjectEnd) =>
                s"${quote(k)}: ${v.print}"
            }

          this match {
            case JsNumber(v)             => v.toString
            case JsString(v)             => quote(v)
            case JsBoolean(v)            => v.toString
            case JsNull                  => "null"
            case s @ SeqCell(_, _)       => "[" ++ seqToJson(s) ++ "]"
            case SeqEnd                  => "[]"
            case o @ ObjectCell(_, _, _) => "{" ++ objectToJson(o) ++ "}"
            case ObjectEnd               => "{}"
          }
        }
      }
      final case class JsNumber(value: Double) extends Json
      final case class JsString(value: String) extends Json
      final case class JsBoolean(value: Boolean) extends Json
      case object JsNull extends Json
      sealed trait JsSequence extends Json
      final case class SeqCell(head: Json, tail: JsSequence) extends JsSequence
      case object SeqEnd extends JsSequence
      sealed trait JsObject extends Json
      final case class ObjectCell(key: String, value: Json, tail: JsObject) extends JsObject
      case object ObjectEnd extends JsObject
    }

    /**
      * あなたの方法が機能することをテストします。ここでは、私が選んだ表現方法を使った例を紹介します。
      */
//    SeqCell(JsString("a string"), SeqCell(JsNumber(1.0), SeqCell(JsBoolean(true), SeqEnd))).print
//    // res0: String = ["a string", 1.0, true]
//
//    ObjectCell(
//      "a",
//      SeqCell(JsNumber(1.0), SeqCell(JsNumber(2.0), SeqCell(JsNumber(3.0), SeqEnd))),
//      ObjectCell(
//        "b",
//        SeqCell(JsString("a"), SeqCell(JsString("b"), SeqCell(JsString("c"), SeqEnd))),
//        ObjectCell(
//          "c",
//          ObjectCell("doh",
//                     JsBoolean(true),
//                     ObjectCell("ray",
//                                JsBoolean(false),
//                                ObjectCell("me", JsNumber(1.0), ObjectEnd))),
//          ObjectEnd
//        )
//      )
//    ).print
//    // res1: String = {"a": [1.0, 2.0, 3.0], "b": ["a", "b", "c"], "c": {"doh": true, "ray": false, "me": 1.0}}
  }

  object chapter4703 {

    /**
      * #### 4.7.0.3 Music
      */
    /**
      * JSONの演習では、モデル化するための明確に定義された仕様がありました。
      * このエクササイズでは、かなりあいまいな仕様をもとに、モデル化のスキルを身につけたいと思います。
      * 目標は、音楽をモデル化することです。
      * これをどのように解釈するかは自由で、モデルを単純にも複雑にもすることができます。
      * 重要なのは、自分が下した決定を正当化し、モデルの限界を理解することです。
      *
      * モデルの記述には、JSONの演習で紹介したBNF記法を使うのが一番簡単かもしれません。
      */

    /**
      * 模範
      */
    /**
      * 私のソリューションは、西洋音楽の非常に単純化されたバージョンをモデルとしています。私の基本的な「原子」は音符で、音程と持続時間で構成されています。
      */
    // Note ::= pitch:Pitch duration:Duration

    /**
      * C0（約16Hz）からC8までの標準的な音階の音を表すピッチのデータを想定しています。以下のようなものです。
      */
    // Pitch ::= C0 | CSharp0 | D0 | DSharp0 | F0 | FSharp0 | ... | C8 | Rest

    /**
      * 静寂をモデル化するために、Restをピッチとして入れたことに注意してください。
      *
      * すでにいくつかの制限があるようです。
      * 音階の外にある音（微分音）や、他の音階を使用する音楽システムをモデル化していません。
      * さらに、ほとんどのチューニングシステムでは、フラットとそのエンハーモニック・シャープ（例：CシャープとDフラット）は同じ音ではありませんが、ここではその区別を無視しています。
      *
      * この表現をさらに音程に分解すると
      */
    // Tone ::= C | CSharp | D | DSharp | F | FSharp | ... | B

    /**
      * と1オクターブ
      */
    // Octave ::= 0 | 1 | 2 | ... | 8

    /**
      * そして
      */
    // Pitch ::= tone:Tone octave:Octave

    /**
      * 標準的な楽譜では、デュレーションは混乱しています。
      * たくさんの名前のついた持続時間（半音、四分音符など）があり、その他の持続時間を表すために点や結ばれた音符があります。
      * 私たちの音楽には、ビートと呼ばれる時間の原子単位があり、各デュレーションは0またはそれ以上のビートであると言えば、うまくいくでしょう。
      */
    // Duration ::= 0 | 1 | 2 | ...

    /**
      * 言い換えれば、Durationは自然数です。Scalaでは、これをIntでモデル化したり、Intにかける追加の制約を表す型を作成したりします。
      *
      * しかし，この表現には制限があります．つまり、ある時間の区分にきれいに収まらない音楽、いわゆるフリータイムの音楽は表現できません。
      *
      * 最後に、音符の構成方法について説明します。大きく分けて2つの方法があります。音符を順番に演奏する方法と、同時に演奏する方法です。
      */
//    Phrase ::= Sequence | Parallek
//    Sequence ::= SeqCell phrase:Phrase tail:Sequence
//    | SeqEnd
//
//    Parallel ::= ParCell phrase:Phrase tail:Parallel
//    | ParEnd

    /**
      * この表現では、音符の並列および連続した単位を任意に入れ子にすることができます。しかし、次のような正規化された表現の方がいいかもしれません。
      */
//    Sequence ::= SeqCell note:Note tail:Sequence
//    | SeqEnd
//
//    Parallel ::= ParCell sequence:Sequence tail:Parallel
//    | ParEnd

    /**
    * このモデルには多くのものが欠けています。そのいくつかを紹介します。
    * * 音楽のダイナミクスを一切モデル化していません。音符は大きくなったり小さくなったりしますし、音量も演奏中に変化します。
    * また、音符のピッチは常に一定ではありません。ピッチベンドやスラーは、1つの音の中で音程を変化させる例です。
    *
    * * さまざまな楽器のモデル化はまったくしていません。
    * * 現代音楽の重要な部分を占めるエコーやディストーションなどのエフェクトもモデル化されていません。
    */

  }

  def main(args: Array[String]): Unit = {
    println("chapter47")

    import Chapter4.chapter47.chapter4702.json._
    SeqCell(JsString("a string"), SeqCell(JsNumber(1.0), SeqCell(JsBoolean(true), SeqEnd))).print
    // res0: String = ["a string", 1.0, true]

    ObjectCell(
      "a",
      SeqCell(JsNumber(1.0), SeqCell(JsNumber(2.0), SeqCell(JsNumber(3.0), SeqEnd))),
      ObjectCell(
        "b",
        SeqCell(JsString("a"), SeqCell(JsString("b"), SeqCell(JsString("c"), SeqEnd))),
        ObjectCell(
          "c",
          ObjectCell("doh",
                     JsBoolean(true),
                     ObjectCell("ray",
                                JsBoolean(false),
                                ObjectCell("me", JsNumber(1.0), ObjectEnd))),
          ObjectEnd
        )
      )
    ).print
    // res1: String = {"a": [1.0, 2.0, 3.0], "b": ["a", "b", "c"], "c": {"doh": true, "ray": false, "me": 1.0}}
  }
}

object chapter48 {

  /**
  * ## 4.8 Conclusions
  * この章では、言語の特徴から、その言語がサポートするプログラミングパターンへと、焦点を非常に重要なものに変えています。
  * これは本書の残りの部分でも同様です。
  *
  * 本章では、代数的データ型と構造的再帰という、非常に重要な2つのパターンを取り上げました。
  * これらのパターンにより、データのメンタルモデルから、Scalaでのデータの表現と処理までを、ほぼ完全に機械的に行うことができます。
  * コードの構造が定型化され、理解しやすくなるだけでなく、コンパイラが一般的なエラーを検出してくれるので、開発やメンテナンスが容易になります。
  * この2つのツールは，慣用的な関数型コードで最もよく使われるものであり，その重要性を強調しすぎることはありません．
  *
  * 演習では、いくつかの一般的なデータ構造を開発しましたが、保存するデータの種類が限られており、コードには多くの繰り返しが含まれていました。
  * 次のセクションでは，型やメソッドをどのように抽象化するかを見ていき，操作の順序付けに関する重要な概念を紹介していきます．
  */
}
