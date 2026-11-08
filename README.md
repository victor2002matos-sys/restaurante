# Sistema de Gerenciamento de Restaurante — demonstração "OHMELETE"

Protótipo funcional (entrega parcial) do sistema descrito no documento de requisitos.
Aplicação web em **Java 21 + Spring Boot 4.1**, gerada pelo [Spring Initializr](https://start.spring.io), no padrão **MVC**:

| Camada MVC | Onde está | Tecnologia |
| --- | --- | --- |
| Model (domínio) | `src/main/java/br/com/restaurante/domain` | Entidades JPA |
| Persistência | `src/main/java/br/com/restaurante/repository` | Spring Data JPA |
| Regras de negócio | `src/main/java/br/com/restaurante/service` | Spring `@Service` |
| Controller | `src/main/java/br/com/restaurante/controller` | Spring MVC `@Controller` |
| View | `src/main/resources/templates` | Thymeleaf + Bootstrap, visual neutro |

## Como rodar

Pré-requisito: **JDK 21** instalado. O Maven não precisa ser instalado, o projeto traz o Maven Wrapper.

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Abra http://localhost:8080.

No **VS Code**: instale o "Extension Pack for Java" (o VS Code sugere sozinho ao abrir a pasta),
abra `RestauranteApplication.java` e clique em **Run**, ou use a aba Run and Debug → "Restaurante (H2)".

Por padrão o sistema usa o banco **H2 em memória**, que não precisa de instalação.
Os dados de exemplo são de um restaurante italiano fictício, a OHMELETE: 30 itens no cardápio (15 massas, antipasti, risotti, sobremesas e bebidas), 2 promoções, 12 mesas e alguns pedidos. Eles são recriados a cada vez que o sistema inicia.

### Usando PostgreSQL (opcional)

```bash
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Testes

```bash
./mvnw test
```

## Usuários de demonstração

Senha de todos: `123456`

| Perfil | CPF (login) | Acesso |
| --- | --- | --- |
| Gerente | 11111111111 | Tudo, incluindo Administração |
| Garçom | 22222222222 | Pedidos e clientes |
| Cozinheiro | 33333333333 | Painel da cozinha |

Clientes de exemplo: 44444444444 (João), 55555555555 (Maria), 66666666666 (Pedro).
O pedido **#2** (CPF 66666666666) está finalizado e ainda sem avaliação, bom para testar o UC-3.

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
| RNF002 Controle de acesso por perfil | Pronto | Login com CPF e senha |
| RNF004 Integridade | Pronto | FKs, `NOT NULL`, `UNIQUE` e `CHECK` no banco + validação no backend |

Regras de negócio:

- **RN001** pedido só para cliente cadastrado (`PedidoService.registrar`)
- **RN002** pedido sempre ligado a cliente, mesa e funcionário (colunas `NOT NULL`)
- **RN003** prato exige nome e preço (`@NotBlank`, `@NotNull`)
- **RN004** nota entre 1 e 10 (`CHECK` na tabela `avaliacao` + `AvaliacaoService`)

Casos de uso:

- **UC-1 Registrar Pedido**: mesa ocupada fica bloqueada; o resumo e o total são calculados na tela; o pedido nasce "Aguardando preparo" e ligado ao funcionário logado.
- **UC-2 Cadastrar Prato**: preço inválido e nome repetido mostram erro e voltam ao formulário.
- **UC-3 Avaliar Atendimento**: o cliente informa o número do pedido e o CPF; só pedidos finalizados podem ser avaliados, uma vez cada.

## Decisões tomadas onde o documento não detalha

- Status do pedido: Aguardando preparo → Em preparo → Pronto → Entregue → Finalizado, ou Cancelado.
- O preço cobrado fica gravado no item do pedido, já com a promoção do dia. Se houver mais de uma promoção, vale o menor preço.
- Cozinheiro e gerente são funcionários; a função define o perfil de acesso.
- A avaliação não exige login: o CPF confirma que quem avalia é o cliente do pedido.
- Prato que já foi pedido não é apagado, só fica indisponível, para manter o histórico.

## Modelo de dados

`pessoa` (base) → `cliente`, `funcionario` (herança JOINED pelo CPF) ·
`prato` ↔ `ingrediente` (`prato_ingrediente`) · `promocao` ↔ `prato` (`prato_promocao`) ·
`mesa` · `pedido` (cliente, mesa, funcionário) → `item_pedido` (prato, quantidade, preço) · `avaliacao` (1 por pedido).

## Equipe

Tiago Longo, Heloiza Ruiz, Henrique Menegatti, Victor Shiguemoto.
