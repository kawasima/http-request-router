Http Request Router
========================

Ruby on Railsのようなルーティングを実現するためのライブラリです。

## ルーティングの設定

以下のようなルーティング用のXMLを用意します。

	<routes>
		<root to="index#index"/>
		<match path="/user/list/:pageNo" controller="admin.User" action="index"/>
		<match path="/user/:id" controller="admin.User" action="show"/>
		<controller name="blog">
			<match path="post" to="post"/>
		</controller>
		<match path="/:controller/:action/:id"/>
	</routes>

コントローラは、アクションパッケージ以降の名前で書きます。サフィックスのActionは不要です。
すなわち、com.exampleがルートパッケージの場合は、admin.Userと書くとcom.example.action.admin.UserActionのアクションクラスを
指すことになります。

match は基本のルーティング設定で、path属性にマッチすると、そのコントローラ、アクションにforwardされます。
controllerタグでコントローラごとの設定をまとめることができます。


### Namespaceとルーティング

ルーティングをグループ化するために、namespaceを使うことができます。

	<namespace name="admin">
		<match path="posts" to="Posts#index"/>
	</namespace>

と書くと、/admin/posts のパスが admin.PostsAction にマッチするようになります。

また、パスには/adminを付けたくないけれども、admin.PostsActionにマッチさせたい場合は、

	<scope module="admin">
		<match path="posts" to="Posts#index"/>
	</scope>

とscopeディレクティブとmodule属性を使ってActionに付加するパッケージ名を指定することができます。

逆に、パスに/adminを付けるが、Actionにパッケージ名を付加したくない場合、すなわちコンテキストルートや
リバースプロキシのパスプリフィックスに対応させるようなケースでは、

	<scope name="admin">
		<match path="posts" to="Posts#index"/>
	</scope>

と書きます。


### 特別なパラメータ

:controller と :action は特別なパラメータで、:controllerはアプリケーション内のActionクラスにマッピングされ、
:actionはActionクラスのメソッドにマッピングされます。
したがって、「action名/メソッド名」形式のルーティングは以下の1行で表現されます。

	<match path=":controller/:action" />

### Dynamic Segments

任意のパラメータをURLに付け加えることができます。

	<match path=":controller/:action/:id/:userId"/>

というルーティングでは、例えば /photos/show/1/2 というリクエストが来ると、PhotosActionのshowメソッドに転送され、
Actionのプロパティ、またはActionFormのid属性に"1"が、userId属性に"2"が設定されます。

namespaceの中で:controllerセグメントを使うことはできません。そうする必要があれば、:controllerを

### Static Segments

固定のパスを途中に挟み込むことができます。

	<match path=":controller/:action/:id/with_user/:userId"/>

というルーティングは、 /photos/show/1/with_user/2 のようなURLに対応し、

	{ :controller => "Photos", :action => "show", :id => "1", userId => "2" }

というパラメータ構成になります。

### Query String

当然ながら任意のパラメータをクエリ文字列から取得することもできます。

	<match path=":controller/:action/:id"/>

というルーティングは、 /photos/show/1?userId=2 のようなURLに対応し、

	{ :controller => "Photos", :action => "show", :id => "1", userId => "2" }

というパラメータ構成になります。

### マッチング

パラメータが特定の形式の場合だけ、ルーティングさせたいという場合は、以下のように正規表現によるマッチングが可能です。

	<match path="posts/:year/:month" to="Posts#index">
		<requirements>
			<requirement name="year" value="\d{4}" />
		</requirements>
	</match>

この場合、yearパラメータが数字4桁の場合だけ、Posts#indexにルーティングされるようになります。

また、マッチングパラメータは1つのパスセグメントの中に複数持つことができます。その場合はパラメータを括弧で囲ってください。

	<match path="images/(:width)x(:height)" to="Images#show">
		<requirements>
			<requirement name="width" value="\d+"/>
			<requirement name="height" value="\d+"/>
		</requirements>
	</match>

### デフォルト

パラメータがパス要素として渡されない場合に、パラメータにデフォルト値を設定できます。

	<match path="prefecture/:name" to="Prefecture#show"/>
		<defaults>
			<default name="name" value="tokyo"/>
		</defaults>
	</match>

のように定義されていると、 /prefecture のようなURLに対応し、

	{ :controller => "Prefecture", :action => "show", :name => "tokyo" }

というパラメータ構成になります。

### HTTPメソッド

HTTPメソッドによってルーティングを分けたい場合には

	<get  path="photos" to="Photos#list"/>
	<post path="photos" to="Photos#update"/>

のように書いておくと、/photos へGETでアクセスすると Photos#list へ、POSTでアクセスすると Photos#update へ転送されます。

### グロブ

	<match path="photos/*other" to="Photos#unknown"/>

というルーティングは、photos/12 や /photos/long/path/to/12 というパスにマッチし、otherパラメータに
"12" または "long/path/to/12" がセットされます。

ワイルドカードはルートのどこにあっても構いません。

	<match path="books/*section/:title" to="Books#show"/>

は、books/some/section/last-words-a-memoir のパスにマッチし、sectionパラメータに"some/section"が、
titleパラメータに"last-words-a-memoir" がセットされます。

## License

Http Request Router はApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt

