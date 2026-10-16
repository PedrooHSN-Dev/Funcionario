# Cadastro de Funcionários

Sistema em Java com JavaFX para cadastrar, excluir, consultar e listar funcionários, com dados salvos em CSV e relatórios usando Stream API.

## Requisitos

- JDK 17 ou superior
- Maven (o IntelliJ IDEA já possui Maven embutido)
- IntelliJ IDEA

## Como executar

1. Abra a pasta do projeto no IntelliJ IDEA (File > Open).
2. Aguarde o Maven baixar as dependências.
3. Na aba Maven, execute Plugins > javafx > javafx:run.

Ou pelo terminal, na pasta do projeto:

```
mvn javafx:run
```

## Funcionalidades

- Cadastrar, excluir, consultar por matrícula e listar funcionários
- Dados salvos no arquivo `funcionarios.csv` (separado por `;`)
- Relatórios: por cargo, por faixa salarial, média salarial por cargo, agrupados por cidade e por estado
