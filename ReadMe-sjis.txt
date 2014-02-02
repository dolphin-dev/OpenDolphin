2012-01-07 （株）デジタルグローブ　皆川和史

１．iTextAsian
OpenDolphinは、バージョン2.2 より maven ベースに移行しています。
iTextAsian.jar はmavenのセントラルリポジトリーにないため、ローカルリポジトリーに手動でインストールする必要があります。
例）
mvn install:install-file -Dfile=iTextAsian.jar -DgroupId=opendolphin -DartifactId=itext-font -Dversion=1.0 -Dpackaging=jar


２．開発環境
・jdk 1.6.0_29
・NetBeans 7.1
・maven 3.0.3
・JavaEE 5
・JBoss-5.1.0.GA


３．ライセンス
・OpenDolphinのライセンスは GNU GPL2 です。
・OpenDolphinには札幌市元町皮ふ科の松村先生、和歌山市増田内科の増田先生のコードが含まれています。これらの部分の著作権はそれぞれの先生に帰属します。
・OpenDolphinは（株）デジタルグローブの登録商標です。