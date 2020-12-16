# JankenEnterpriseEdition

![badge.svg](https://github.com/os1ma/JankenEnterpriseEdition/workflows/workflow/badge.svg)

じゃんけんプログラムを全力で実装していく一人アドベントカレンダーのソースコードリポジトリです。

詳しくは [じゃんけん Advent Calendar 2020](https://qiita.com/advent-calendar/2020/janken) を参照ください。

## 開発環境のセットアップ

プロジェクトホームで以下のコマンドを実行することで、Git の hook が設定され、`git commit` 実行時にビルドが通るかローカルで検証されるようになります。

```shell
ln -s ../../bin/pre-commit .git/hooks/pre-commit
```

## 実行手順

### CLI アプリケーション

```shell
./gradlew clean jar
DATA_DIR="$(pwd)/data" java -jar app/build/libs/app.jar
```

### Web アプリケーション

```shell
./gradlew clean war
java -jar app/build/server/webapp-runner-*.jar app/build/libs/*.war --port 8080
```

その後、以下の URL などにアクセス

- http://localhost:8080/api/health
