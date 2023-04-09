# Sales Importer

Aplicação responsável pela importar e processar os arquivos de transações feitas na venda de produtos por nossos
clientes.

## Issues

_A intenção aqui é demonstrar como as issues do projeto foram organizadas._

| ID | Descrição                                                                                       | Status     |
|----|-------------------------------------------------------------------------------------------------|------------|
| T1 | Convenções para as mensagens dos commits, nomeclatura de branches e pipeline de desenvolvimento | Finalizado |
| T2 | Definir a linguagem, frameworks e arquitetura da aplicação                                      | Finalizado |
| T3 | Definir a pirâmide de testes                                                                    | Finalizado |
| T4 | Definir o design de pacotes e camadas                                                           |            |
| T5 | Criar a estrutura da aplicação de backend                                                       |            |
| T6 | Configurar Checkstyle e CodeFormatter para o backend                                            |            |
| T7 | Criar a estrutura da aplicação de frontend                                                      |            |
| T8 | Empacotar a aplicação utilizando Docker                                                         |            |
| T9 | Modernizar o frontend utilizando um framework mais moderno como Angular ou React                |            |

## ADR

_As `Architecture Decision Records` aqui estão escritas de forma simplificada, em um projeto real todas as alternativas
seriam detalhadas e pontos fortes e fracos de cada uma delas seriam discutidas._

### Convenção para as mensagens nos commits

Consideramos aqui a possibilidade de geração automatizada dos release notes a partir das mensagens dos commits,
além da experiência da comunidade nessa questão.

* Decisão: [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
* Alternativas consideradas: Definir um modelo interno e personalizado.

### Nomeclatura das branches

Consideramos aqui a facilidade em identificar o escopo da branches apenas pelo seu nome.

* Decisão: <scope>/<issue-id> Vamos seguir os escopos definidos
  em [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
  Ex: feat/T2, fix/T3, docs/T1
* Alternativas consideradas: Gitflow (feat and fix)

### Pipeline de desenvolvimento

Deploys frequentes, entregas pequenas e sem afetar as métricas de aplicação como tempo de resposta e taxa de erros.
Estamos considerando também que o próprio desenvolvedor é responsável por testar e entregar a feature em produção.

* Decisão: Trunk Based + Feature flags + Testes automatizados
* Alternativas consideradas: Gitflow

### Pirâmide de testes

Optamos por uma pirâmide que entende e minimiza a necessidade de um testes E2E, já que eles tendem a quebrar com muita
frequência e levar muito tempo para executar. Optamos também por uma pirâmide que encoraja o uso de TDD, e que
garanta o teste de todas as camadas da aplicação de forma independente e também conjunta.

* Decisão:
  Artigo [Testing Strategies in a Microservice Architecture](https://martinfowler.com/articles/microservice-testing/)
* Alternativas consideradas: Testes unitários + Testes integrados + Testes e2e

### Linguagem de programação do backend

Devido a urgência do projeto decidimos seguir pela stack de maior experiência, a que possibilita entregar mais
rapidamente o componente.

* Decisão: `Kotlin`
* Alternativas consideradas: `Java`, `Go`

### Frameworks

Aplicações executadas por uma JVM tem um vasto leque de opções de frameworks, seguindo o direcionamento de prazo, vamos
utilizar um framework mais estabelecido e que tenha muitas opções de integração e bibliotecas.

* Decisão: Spring Boot
* Alternativas consideradas: Quarkus, Micronaut

### Versão da JVM

Como é um projeto novo, e o framework possui suporte as versões mais recentes da `JVM`, vamos seguir com a última LTS.
Com relação as diferentes distribuições optamos por uma versão livre e que seja mantida por um vendor de confiança.

* Decisão: 17.0.6 - [Corretto](https://aws.amazon.com/pt/corretto/) (Amazon)
* Alternativas consideradas: 17.x - OpenJDK, 17.x - GraalVM 11, 20.x - OpenJDK

### Build system para o Backend

Mais uma vez aqui o direcionamento é pelo suporte da comunidade e do framework escolhido, o suporte da IDE também
foi considerado. Nesse ponto: tanto `Gradle`, quanto `Maven` são equivalentes. O desempate aqui ficou por conta da
capacidade de cache e maior velocidade.

* Decisão: `Gradle`
* Alternativas consideradas: `Maven`, `Bazel`

### Framework de Frontend

Devido a pouca experiência com frameworks para recentes para frontend, o prazo do projeto e a simplicidade do frontend,
vamos seguir com Vanilla JS, a não necessidade de uma outra build tool, como npm ou yarn também foi considerado um ponto
de simplificação do projeto. Mas está planejado a sustituição dessa desse frontend para o `Angular` ou `React` em um
futuro próximo.

* Decisão: `Vanilla JS`
* Alternativas consideradas: `Angular`, `React`

### Build system para o Frontend

Mais uma vez aqui o direcionamento é pelo suporte da comunidade. Nesse ponto: tanto `yarn`, quando `npm` são
equivalentes.
O desempate aqui ficou por conta do desempenho do `yarn` ser superior.

* Decisão: `yarn`
* Alternativas consideradas: `npm`, `Bazel`

### Arquitetura de aplicação

É um requisito não funcional que a aplicação seja empacotada em containers `Docker`. Optamos aqui for dois containers,
um para o frontend e outro para o backend, seria possível empacotar ambos em um único container, porém optamos aqui
por ter as camadas separadas e utilizar build systems diferente para cada um deles.

* Decisão: Dois containers (Um para frontend e outro para o backend)
* Alternativas consideradas: Um unico container contendo tanto o Frontend, quanto o Backend.

### Banco de dados

É um requisito não funcional a escolha de um banco de dados relacional. Optamos por um banco OpenSource, com grande
suporte da comunidade e de provedores de Cloud. A experiência com o banco de dados também foi considerada.

* Decisão: `PostgreSQL`
* Alternativas consideradas: `MySQL`, `SQLServer`, `Oracle`