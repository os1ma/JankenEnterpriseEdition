# JankenEnterpriseEdition

![badge.svg](https://github.com/os1ma/JankenEnterpriseEdition/workflows/workflow/badge.svg)

じゃんけんプログラムを全力で実装していく一人アドベントカレンダーのソースコードリポジトリです。

詳しくは [じゃんけん Advent Calendar 2020](https://qiita.com/advent-calendar/2020/janken) を参照ください。

## 開発環境のセットアップ

以下のコマンドにより、`git commit` 実行時にビルドが通るかローカルで検証されるようになります。

```shell
printf '#!/bin/bash\n'"$(pwd)"'/bin/build.sh\n' > .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

## 実行手順

```shell
./gradlew build
DATA_DIR="$(pwd)/data" java -jar app/build/libs/app.jar
```
