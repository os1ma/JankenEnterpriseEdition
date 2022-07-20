# JankenEnterpriseEdition

![badge.svg](https://github.com/os1ma/JankenEnterpriseEdition/workflows/main/badge.svg)

![badge.svg](https://github.com/os1ma/JankenEnterpriseEdition/workflows/schedule/badge.svg)

[![dependency-check-analyze](https://github.com/os1ma/JankenEnterpriseEdition/actions/workflows/dependency-check-analyze.yaml/badge.svg)](https://github.com/os1ma/JankenEnterpriseEdition/actions/workflows/dependency-check-analyze.yaml)

じゃんけんプログラムを全力で実装していく一人アドベントカレンダーのソースコードリポジトリです。

詳しくは [じゃんけん Advent Calendar 2020](https://qiita.com/advent-calendar/2020/janken) を参照ください。

## 開発環境のセットアップ

プロジェクトホームで以下のコマンドを実行することで、Git の hook が設定され、`git commit` 実行時にビルドが通るかローカルで検証されるようになります。

```shell
ln -s ../../bin/pre-commit .git/hooks/pre-commit
```

## 開発環境での実行手順

```shell
./gradlew bootRun
```

その後、以下の URL などにアクセス

- http://localhost:8080/api/health
