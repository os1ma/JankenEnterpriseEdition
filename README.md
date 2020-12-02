# JankenEnterpriseEdition

![badge.svg](https://github.com/os1ma/JankenEnterpriseEdition/workflows/workflow/badge.svg)

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
