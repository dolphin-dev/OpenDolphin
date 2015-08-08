# OpenDolphin
皆川和史、王勝偉　[オープンドルフィン・ラボ](http://www.opendolphin.com)  

#### １．OpenDolphin 2.6.0  
 * 2015-08-08
 * WildFly 9 に対応しました。
 * 2.5.x 系のバグを修正しました。
 * クライアント側の機能追加はありません。

#### ２．ライセンス  
 * OpenDolphinのライセンスは GNU GPL3 です。  
 * OpenDolphinは下記先生方の開発されたソースコードを含んでいます。  
  - 札幌市元町皮ふ科の松村先生
  - 和歌山市増田内科の増田先生
  - 新宿ヒロクリニック
  - 日本RedHat Takayoshi KimuraさんのJBoss as7 へのポーティング

これらの部分の著作権はそれぞれの先生に帰属します。  

#### ３．開発環境  
 * jdk 1.8.0_51  
 * NetBeans 8.0.2  
 * maven 3.3.1
 * JavaEE 7
 * WildFly-9.0.1.Final
 * ソース/バイナリ形式は jdk1.8

#### ４．依存性  
OpenDolphinは、maven でプロジェクト管理を行っています。ビルドするにはext_lib内の  
 * iTextAsian.jar  
 * AppleJavaExtensions.jar

をローカルリポジトリーに手動でインストールする必要があります。  

mvn install:install-file -Dfile=/path/to/iTextAsian.jar -DgroupId=opendolphin -DartifactId=itext-font -Dversion=1.0 -Dpackaging=jar  

mvn install:install-file -Dfile=/path/to/AppleJavaExtensions.jar -DgroupId=com.apple -DartifactId=AppleJavaExtensions -Dversion=1.6 -Dpackaging=jar

#### ５ ．コンパイル  
 * git clone https&#58;//github.com/dolphin-dev/OpenDolphin.git ~/Desktop/OpenDolphin  
 * mvn clean  
 * mvn package  

#### ６．Docker Image  
dolphin-dev リポジトリに Docker コンテナイメージ（docke-images）を置いています。[この手順](https://github.com/dolphin-dev/docker-images)
に従うと最も簡単にOpenDolphinサーバーを構築することができます。
