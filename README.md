# summer_eletrohits_2000

##Integrantes

-Enzo de Paula Souza
-Erica Goncalves de Oliveira
-

Projeto Java — README inicial com instruções de execução e lista de tarefas previstas para persistência e regras de negócio.

## Requisitos

- Java 11+ (recomendado 17+)
- Git
- Maven ou Gradle (se presentes no repositório)
- Banco de dados (desenvolvimento: Docker / PostgreSQL recomendado)

## Como executar

1. Clone o repositório:

   git clone https://github.com/isoldess/summer_eletrohits_2000.git
   cd summer_eletrohits_2000

2. Usando Maven (se houver `pom.xml`):

   mvn clean package
   java -jar target/*.jar

3. Usando Gradle (se houver `build.gradle`):

   ./gradlew build
   java -jar build/libs/*.jar

4. Sem build tool explícito:

   - Abra o projeto em sua IDE (IntelliJ IDEA, Eclipse, VS Code) e execute a classe `main` apropriada.
   - Ou compile manualmente (ex.: `javac`) e execute via `java -cp` apontando para a classe principal.

5. Execução com banco de dados (desenvolvimento local com Docker):

   - Exemplo docker-compose (crie um `docker-compose.yml` localmente):

     version: '3.8'
     services:
       db:
         image: postgres:15
         environment:
           POSTGRES_USER: app
           POSTGRES_PASSWORD: secret
           POSTGRES_DB: summer_eletrohits
         ports:
           - "5432:5432"

   - Após subir o DB, atualize as configurações de conexão (ex.: `application.properties` ou variáveis de ambiente) e rode a aplicação.

## Configuração (exemplos)

- Variáveis de ambiente sugeridas:
  - DB_URL (ex.: jdbc:postgresql://localhost:5432/summer_eletrohits)
  - DB_USER
  - DB_PASSWORD
  - SERVER_PORT (opcional)

- Arquivos de configuração: use `application.properties`/`application.yml` ou outra abordagem conforme o framework adotado.

## Tarefas previstas — Persistência

Estas são tarefas sugeridas para implementar a camada de persistência de forma robusta:

1. Escolher e documentar a tecnologia de persistência
   - Ex.: Spring Data JPA com Hibernate + PostgreSQL (ou outra combinação desejada).

2. Dependências e configuração
   - Adicionar dependências (JDBC driver, JPA, HikariCP, Flyway/Liquibase) ao build.
   - Configurar pool de conexões (HikariCP) e parâmetros de timeout/conexões.

3. Modelo de dados e entidades
   - Mapear entidades JPA (ou DTO/Models) com colunas e relacionamentos.
   - Definir chaves primárias, índices e constraints necessários.

4. Migrações de esquema
   - Integrar Flyway ou Liquibase e criar migrations iniciais.
   - Garantir que migrations rodem automaticamente em ambientes de CI/CD quando apropriado.

5. Repositórios/DAOs
   - Implementar repositórios (ex.: Spring Data repositories) com consultas necessárias.
   - Escrever testes de integração para repositorios usando um DB em memória ou Testcontainers.

6. Backup e recovery (planejamento)
   - Documentar estratégia de backup para o ambiente de produção.

7. Observabilidade e monitoramento
   - Logs de SQL (opcional em dev), métricas de pool/latência de queries e alertas.

## Tarefas previstas — Regras de negócio

Planejamento das regras e validações que o domínio deve aplicar:

1. Definição do modelo de domínio
   - Elaborar um diagrama simples com as entidades principais e seus relacionamentos.

2. Regras de validação básica
   - Validações de campos obrigatórios, formatos (ex.: emails, datas) e limites (tamanhos, ranges).

3. Regras transacionais
   - Identificar operações que precisam ser atômicas e garantir uso correto de transações.

4. Regras de integridade e unicidade
   - Restrições (únicas) no banco e validações na camada de aplicação para evitar duplicidade.

5. Regras de negócio específicas (exemplos genéricos — ajustar ao domínio)
   - Controle de permissões/roles para operações sensíveis.
   - Regra de negócio para cálculo de pontuação/preço/ordenamento (definir fórmula e testes).
   - Políticas de expiração/retenção de dados, se aplicável.

6. Logs de auditoria
   - Registrar ações importantes (criação/atualização/exclusão) com usuário, timestamp e mudanças.

7. Testes de aceitação e de integração
   - Criar cenários que validem as regras de negócio críticas.

## Critérios de aceitação (sugestão)

- A aplicação inicia e se conecta ao banco configurado.
- Migrations aplicadas com sucesso no ambiente de desenvolvimento.
- Repositórios passam nos testes de integração.
- Regras de validação críticas cobertas por testes automatizados.

## Próximos passos recomendados

1. Definir o domínio e a lista de entidades prioritárias.
2. Escolher o banco (Postgres recomendado) e a ferramenta de migrations (Flyway/Liquibase).
3. Adicionar configuração de CI para rodar migrations e testes de integração (usar Testcontainers).
4. Implementar as primeiras migrations e os repositórios básicos.

## Como contribuir

- Abra issues descrevendo o que será implementado.
- Crie PRs pequenos e com testes quando possível.

---

Se quiser, eu já posso abrir um PR com este README atualizado ou ajustar o texto para um framework específico (Spring Boot, Micronaut, Quarkus, etc.).
