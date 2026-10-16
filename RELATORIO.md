# Relatório do Projeto

## Funcionalidades implementadas

- **Cadastro**: formulário dividido em "Dados pessoais" e "Endereço", com exemplos de valores válidos em cada campo. Nas datas, a barra (/) é inserida automaticamente ao digitar.
- **Exclusão**: remove o funcionário selecionado na tabela, com confirmação.
- **Consulta**: busca por matrícula e exibe todos os dados do funcionário.
- **Listagem**: tabela com todos os funcionários cadastrados.
- **Persistência**: arquivo `funcionarios.csv` separado por ponto-e-vírgula, carregado ao iniciar e salvo após cada cadastro ou exclusão.
- **Relatórios com Stream API**:
  - Funcionários por cargo (`filter`), escolhendo o cargo em uma lista que é filtrada conforme se digita
  - Funcionários por faixa salarial (`filter`)
  - Média salarial por cargo (`groupingBy`, `map`, `reduce`)
  - Funcionários agrupados por cidade e por estado (`groupingBy`)
  - Soma dos salários nos relatórios filtrados (`map`, `reduce`)

## Validações

- Matrícula com 6 dígitos numéricos e única
- Nome com no mínimo 3 caracteres
- CPF com formato válido, dígitos verificadores corretos e único
- Idade mínima de 18 anos
- Salário positivo
- Data de contratação não pode ser futura
- CEP no formato 00000-000
- Estado com sigla de 2 letras e campos de endereço obrigatórios
- Campos não podem conter `;` para não corromper o CSV

## Decisões de design

- **Camadas separadas**: `model` (Funcionario e Endereco), `view` (tela), `controller` (eventos), `service` (regras e relatórios), `repository` (CSV), `util` (validação) e `exception`.
- **Composição**: `Funcionario` possui um `Endereco`.
- **Tipos adequados**: `LocalDate` para datas e `BigDecimal` para salário, evitando erros de arredondamento.
- **Exceções próprias**: `ValidacaoException` para dados inválidos e `PersistenciaException` para erros de arquivo, ambas exibidas ao usuário em alertas.
- **Interface sem FXML**: a tela é montada em código Java na classe `FuncionarioView` e os eventos são tratados no `FuncionarioController`.
- **Validação centralizada**: a classe `Validador` reúne todas as regras e retorna todos os erros de uma vez.
