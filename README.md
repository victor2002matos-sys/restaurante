# Sistema de Gerenciamento de Restaurante — demonstração "OHMELETE"

Protótipo funcional (entrega parcial) do sistema descrito no documento de requisitos.
Aplicação web em **Java 21 + Spring Boot 4.1**, gerada pelo [Spring Initializr](https://start.spring.io), no padrão **MVC**:

| Camada MVC | Onde está | Tecnologia |

| Model (domínio) | `src/main/java/br/com/restaurante/domain` | Entidades JPA |
| Persistência | `src/main/java/br/com/restaurante/repository` | Spring Data JPA + **PostgreSQL** |
| Regras de negócio | `src/main/java/br/com/restaurante/service` | Spring `@Service` |
| Controller | `src/main/java/br/com/restaurante/controller` | Spring MVC `@Controller` |
| View | `src/main/resources/templates` | Thymeleaf + Bootstrap, visual neutro |

## Passo a passo para rodar (iniciantes)

Você precisa instalar duas coisas: o **Java** e o banco de dados **PostgreSQL**. O Maven não precisa: o projeto já traz o `mvnw`, que baixa tudo sozinho.

### 1. Instale o Java 21 ou mais novo (só na primeira vez)

1. Acesse https://adoptium.net/temurin/releases/?version=21
2. Baixe o instalador do seu sistema (no Windows, o arquivo `.msi` x64).
3. Durante a instalação, marque a opção **Set JAVA_HOME variable**.
4. Para conferir, abra o terminal (no Windows, o PowerShell) e rode `java -version`. Deve aparecer a versão 21 ou maior. Se `java -version` não for reconhecido, reinicie o computador.

### 2. Instale o PostgreSQL (só na primeira vez)

Os dados do sistema ficam gravados no PostgreSQL, então continuam lá depois que você desliga o sistema.

1. Acesse https://www.postgresql.org/download/windows/ e clique em **Download the installer** (versão 16 ou mais nova, Windows x86-64).
2. Rode o instalador e vá clicando em **Next**, mantendo as opções padrão:
   - componentes: deixe marcados **PostgreSQL Server**, **pgAdmin 4** e **Command Line Tools**;
   - **senha do superusuário `postgres`**: escolha uma senha e **anote**, ela é pedida no passo 4;
   - porta: deixe **5432**.
3. No fim, desmarque o **Stack Builder** (não é necessário) e clique em **Finish**.

O PostgreSQL fica instalado como um serviço do Windows e liga sozinho junto com o computador.

**Linux:** `sudo apt install postgresql` · **macOS:** `brew install postgresql@16 && brew services start postgresql@16`.
**Com Docker (qualquer sistema):** `docker compose up -d` já sobe o banco pronto, com usuário e senha `restaurante`, e você pode pular o passo 4.

### 3. Baixe o projeto

**Sem Git:** nesta página, clique no botão verde **Code → Download ZIP** e descompacte o arquivo numa pasta.

**Com Git:**

```bash
git clone https://github.com/victor2002matos-sys/restaurante.git
cd restaurante
```

### 4. Crie o banco do sistema (só na primeira vez)

**Windows:** dentro da pasta do projeto, dê dois cliques em **`criar-banco.bat`** e digite a senha do `postgres` que você anotou (ela não aparece enquanto você digita; depois aperte Enter).
Ele cria o usuário `restaurante` (senha `restaurante`) e o banco `restaurante`. Pode rodar de novo sem problema.

**Linux / macOS:** `sudo -u postgres psql < banco/criar-banco.sql` (no macOS com Homebrew, `psql postgres -f banco/criar-banco.sql`).

**Sem o script (pelo pgAdmin):** abra o pgAdmin, entre no servidor com a senha do `postgres`, abra **Databases**, clique com o botão direito no banco **postgres** → **Query Tool**, cole o código abaixo e aperte **F5**:

```sql
CREATE ROLE restaurante LOGIN PASSWORD 'restaurante';
CREATE DATABASE restaurante OWNER restaurante;
```

As **tabelas** não precisam ser criadas à mão: o sistema cria todas (com chaves, `NOT NULL`, `UNIQUE` e `CHECK`) na primeira vez que liga, e insere os dados de exemplo.

### 5. Ligue o sistema

**Windows:** dentro da pasta do projeto, dê dois cliques em **`iniciar.bat`**.
Uma janela preta mostra o andamento e o navegador abre sozinho quando estiver pronto.
Não feche a janela enquanto estiver usando o sistema; fechar a janela desliga o sistema.

Dica: para ter um atalho, clique com o botão direito em `iniciar.bat` → **Mostrar mais opções → Enviar para → Área de trabalho (criar atalho)**.

**Linux / macOS:** no terminal, dentro da pasta do projeto:

```bash
./mvnw spring-boot:run
```

Na **primeira vez** demora alguns minutos, porque ele baixa as bibliotecas. Nas próximas, leva menos de 1 minuto.

### 6. Use o sistema

Abra http://localhost:8080 (se o navegador não abrir sozinho).

- **Cardápio** e **Avaliar** são públicos.
- Para a área interna, clique em **Área do funcionário** e entre com o CPF `11111111111` e a senha `123456` (perfil Gerente). Os outros usuários estão na tabela abaixo.

Tudo o que você cadastrar fica gravado no PostgreSQL e continua lá quando o sistema é desligado e ligado de novo.
Os dados de exemplo só são inseridos quando o banco está vazio. Para voltar ao estado inicial, apague e recrie o banco
(no pgAdmin: botão direito no banco `restaurante` → **Delete**, e rode o `criar-banco.bat` de novo).

Para ver as tabelas e os dados, use o pgAdmin: **Servers → PostgreSQL → Databases → restaurante → Schemas → public → Tables**.

### Problemas comuns

| O que aparece | Como resolver |
| --- | --- |
| `O PostgreSQL nao esta respondendo` ao abrir o `iniciar.bat`, ou `Connection refused` | O PostgreSQL não está instalado ou está desligado. Instale (passo 2) ou ligue o serviço: **Win+R → `services.msc`**, procure **postgresql-x64-16** (ou a sua versão) → **Iniciar**. |
| `password authentication failed for user "restaurante"` ou `database "restaurante" does not exist` | O banco ainda não foi criado: rode o `criar-banco.bat` (passo 4). |
| `criar-banco.bat` diz `password authentication failed for user "postgres"` | A senha digitada não é a escolhida na instalação do PostgreSQL. Rode de novo com a senha certa. |
| "O Windows protegeu o computador" ao abrir o `iniciar.bat` | Clique em **Mais informações → Executar assim mesmo**. |
| `java` não é reconhecido / `JAVA_HOME not found` | O Java não está instalado ou o `JAVA_HOME` não foi configurado. Reinstale o Java marcando **Set JAVA_HOME variable** e abra um terminal novo. |
| Erro `PKIX path building failed` ao baixar | Um antivírus (por exemplo o Avast) está inspecionando HTTPS. O `iniciar.bat` já contorna isso. No terminal do Windows, rode antes `$env:MAVEN_OPTS='-Djavax.net.ssl.trustStoreType=Windows-ROOT'`. |
| `Invalid argument: connect` ao iniciar | Problema de socket local em alguns Windows. O `iniciar.bat` já contorna isso. No terminal, rode `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=target/uds"` depois de criar a pasta `target\uds`. |
| `Port 8080 was already in use` | O sistema já está ligado em outra janela. Use essa, ou feche-a e ligue de novo. |
| `localhost:8080` não abre em outro computador | Normal: `localhost` é sempre o próprio computador. O sistema precisa estar ligado na máquina onde você abre o navegador. |

### Abrindo no VS Code (opcional)

1. **File → Open Folder** e escolha a pasta do projeto.
2. Instale o **Extension Pack for Java**, que o VS Code sugere sozinho.
3. Abra `src/main/java/br/com/restaurante/RestauranteApplication.java` e clique em **Run** acima do método `main`, ou rode o `iniciar.bat` pelo terminal integrado (**Terminal → New Terminal**).

### Para desenvolvedores

```bash
./mvnw test              # testes automáticos (usam um banco H2 temporário, não mexem no PostgreSQL)
docker compose up -d     # alternativa: PostgreSQL em Docker, já com usuário e banco "restaurante"
./mvnw spring-boot:run   # liga o sistema usando o PostgreSQL
```

A conexão fica em `src/main/resources/application.properties`. Para usar outro servidor, usuário ou senha sem mudar o código,
defina as variáveis de ambiente `DB_URL` (ex.: `jdbc:postgresql://localhost:5432/restaurante`), `DB_USUARIO` e `DB_SENHA`.

## Usuários de demonstração

Senha de todos: `123456`

| Perfil | CPF (login) | Acesso |
| --- | --- | --- |
| Gerente | 11111111111 | Tudo, incluindo Administração |
| Garçom | 22222222222 | Pedidos e clientes |
| Cozinheiro | 33333333333 | Painel da cozinha |

Clientes de exemplo: 44444444444 (João), 55555555555 (Maria), 66666666666 (Pedro).
O pedido **#2** está finalizado e ainda sem avaliação, bom para testar o UC-3.

## O que está implementado

| Requisito | Situação | Onde testar |
| --- | --- | --- |
| RF001–RF003 Cadastro de pessoas, clientes e funcionários | Pronto | Clientes; Administração → Funcionários |
| RF004, RF006 Pratos e ingredientes | Pronto | Administração → Pratos / Ingredientes |
| RF005 Consulta do cardápio | Pronto | Cardápio (público) |
| RF007, RF008 Promoções associadas a pratos | Pronto | Administração → Promoções |
| RF009 Mesas | Pronto | Administração → Mesas |
| RF010, RF011 Registro de pedidos com itens | Pronto | Pedidos → Novo pedido |
| RF012 Atualização de status | Pronto | Detalhe do pedido; Cozinha |
| RF013 Avaliação | Pronto | Avaliar (público) |
| RNF002 Controle de acesso por perfil | Pronto | Login com CPF e senha (Spring Security) |
| RNF004 Integridade | Pronto | FKs, `NOT NULL`, `UNIQUE` e `CHECK` no banco + validação no backend |

Regras de negócio:

- **RN001** (alterada) o pedido não pede cliente nem CPF; basta mesa e pratos
- **RN002** pedido sempre ligado a mesa e funcionário (colunas `NOT NULL`)
- **RN003** prato exige nome e preço (`@NotBlank`, `@NotNull`)
- **RN004** nota entre 1 e 10 (`CHECK` na tabela `avaliacao` + `AvaliacaoService`)

Casos de uso:

- **UC-1 Registrar Pedido**: sem cliente nem CPF; mesa ocupada fica bloqueada; o resumo e o total são calculados na tela; o pedido nasce "Aguardando preparo" e ligado ao funcionário logado.
- **UC-2 Cadastrar Prato**: preço inválido e nome repetido mostram erro e voltam ao formulário.
- **UC-3 Avaliar Atendimento**: o cliente informa só o número do pedido, sem CPF; só pedidos finalizados podem ser avaliados, uma vez cada.

## Banco de dados e segurança

- **Persistência:** PostgreSQL, acessado por Spring Data JPA/Hibernate. As tabelas são criadas e atualizadas pelo Hibernate (`ddl-auto=update`) sem apagar dados.
- **Autenticação:** Spring Security com login por formulário (CPF e senha) e perfis Gerente, Garçom e Cozinheiro.
- **Senhas criptografadas:** gravadas só como hash **BCrypt** (`BCryptPasswordEncoder`), nunca em texto puro. Dá para conferir no pgAdmin: a coluna `senha` da tabela `funcionario` guarda valores como `$2a$10$...`.
- **Telas:** Thymeleaf com `thymeleaf-extras-springsecurity6` mostra ou esconde menus conforme o perfil; formulários levam token CSRF.

## Decisões tomadas onde o documento não detalha

- Status do pedido: Aguardando preparo → Em preparo → Pronto → Entregue → Finalizado, ou Cancelado.
- O preço cobrado fica gravado no item do pedido, já com a promoção do dia. Se houver mais de uma promoção, vale o menor preço.
- Cozinheiro e gerente são funcionários; a função define o perfil de acesso.
- A avaliação não exige login nem CPF: basta o número do pedido.
- Prato que já foi pedido não é apagado, só fica indisponível, para manter o histórico.

## Modelo de dados

`pessoa` (base) → `cliente`, `funcionario` (herança JOINED pelo CPF) ·
`prato` ↔ `ingrediente` (`prato_ingrediente`) · `promocao` ↔ `prato` (`prato_promocao`) ·
`mesa` · `pedido` (cliente, mesa, funcionário) → `item_pedido` (prato, quantidade, preço) · `avaliacao` (1 por pedido).

## Equipe

Tiago Longo, Heloiza Ruiz, Henrique Menegatti, Victor Shiguemoto.
