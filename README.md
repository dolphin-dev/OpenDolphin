2015-01-15　皆川和史、王勝偉　オープンドルフィン・ラボ  

１．ライセンス  
・OpenDolphinのライセンスは GNU GPL3 です。  
・OpenDolphinは下記先生方の開発されたソースコードを含んでいます。  
　　札幌市元町皮ふ科の松村先生  
　　和歌山市増田内科の増田先生  
　　新宿ヒロクリニック   
これらの部分の著作権はそれぞれの先生に帰属します。  
また Takayoshi KimuraさんのJBoss as7 へのポーティングを使用させていただきました。  

２．開発環境  
・jdk 1.8.0_25  
・NetBeans 8.0.2  
・maven 3.2.1  
・JavaEE 6  
・JBoss-7.1.2.Final  
＊ソース/バイナリ形式は jdk1.7

３．依存性  
OpenDolphinは、maven でプロジェクト管理を行っています。  
ビルドするにはext_lib内の  
・iTextAsian.jar  
・AppleJavaExtensions.jar  
をローカルリポジトリーに手動でインストールする必要があります。  

mvn install:install-file -Dfile=/path/to/iTextAsian.jar -DgroupId=opendolphin -DartifactId=itext-font -Dversion=1.0 -Dpackaging=jar  

mvn install:install-file -Dfile=/path/to/AppleJavaExtensions.jar -DgroupId=com.apple -DartifactId=AppleJavaExtensions -Dversion=1.6 -Dpackaging=jar

４ ．コンパイル  
OpenDolphin（トップ）ディレクトリで  
mvn clean  
mvn package  
