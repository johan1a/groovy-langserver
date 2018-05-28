
# groovy-langserver

An LSP server implementation for groovy

# Build

```
./gradlew shadowJar
```

# Run

```
java -jar build/libs/groovy-langserver-all.jar
```

# Set log level to debug

```
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar build/libs/groovy-langserver-all.jar
```

# Use with Neovim

Install an LSP client:

https://github.com/autozimu/LanguageClient-neovim


Add to init.vim:

```
let g:LanguageClient_serverCommands = {
    \ 'groovy': ['java', '-jar', '~/path/to/jar/groovy-langserver/build/libs/groovy-langserver-all.jar']
}
```
