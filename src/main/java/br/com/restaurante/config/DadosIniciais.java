package br.com.restaurante.config;

import br.com.restaurante.domain.*;
import br.com.restaurante.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Popula o banco com dados de exemplo quando ele está vazio, para a demonstração. */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final IngredienteRepository ingredienteRepository;
    private final PratoRepository pratoRepository;
    private final PromocaoRepository promocaoRepository;
    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(FuncionarioRepository funcionarioRepository, ClienteRepository clienteRepository,
                         IngredienteRepository ingredienteRepository, PratoRepository pratoRepository,
                         PromocaoRepository promocaoRepository, MesaRepository mesaRepository,
                         PedidoRepository pedidoRepository, AvaliacaoRepository avaliacaoRepository,
                         PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.clienteRepository = clienteRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.pratoRepository = pratoRepository;
        this.promocaoRepository = promocaoRepository;
        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (funcionarioRepository.count() > 0) {
            return;
        }

        Funcionario gerente = funcionario("11111111111", "Ana Gerente", "16999990001", Funcao.GERENTE, Turno.NOITE);
        Funcionario garcom = funcionario("22222222222", "Bruno Garçom", "16999990002", Funcao.GARCOM, Turno.NOITE);
        funcionario("33333333333", "Carla Cozinheira", "16999990003", Funcao.COZINHEIRO, Turno.NOITE);

        Cliente joao = cliente("44444444444", "João Silva", "16988880001", "Prefere mesa perto da janela; gosta de massas com bastante queijo", null, 3);
        cliente("55555555555", "Maria Souza", "16988880002", "Vegetariana", "Frutos do mar", null);
        cliente("66666666666", "Pedro Lima", "16988880003", "Prefere massa al dente", "Lactose", 5);

        Map<String, Ingrediente> ing = new HashMap<>();
        for (String nome : Arrays.asList("Spaghetti", "Fettuccine", "Massa de lasanha", "Molho branco",
                "Ragù de carne", "Guanciale", "Ovo", "Parmesão", "Pecorino", "Muçarela", "Ricota", "Burrata",
                "Manjericão", "Alho", "Azeite", "Tomate cereja", "Pão italiano", "Carne bovina", "Rúcula",
                "Mascarpone", "Café", "Cacau", "Creme de leite", "Frutas vermelhas", "Limão siciliano", "Açúcar")) {
            ing.put(nome, ingredienteRepository.save(new Ingrediente(nome)));
        }

        // Entrega parcial: 3 exemplos por categoria (Entradas, Pratos principais, Sobremesas e Bebidas)

        // Entradas
        prato("Bruschetta al pomodoro", "Pão italiano tostado com tomate, alho e manjericão", Categorias.ENTRADAS, "28.00",
                ing.get("Pão italiano"), ing.get("Tomate cereja"), ing.get("Alho"), ing.get("Manjericão"));
        prato("Burrata", "Burrata com tomate cereja, rúcula e azeite", Categorias.ENTRADAS, "48.00",
                ing.get("Burrata"), ing.get("Tomate cereja"), ing.get("Rúcula"), ing.get("Azeite"));
        prato("Carpaccio", "Lâminas de carne crua com rúcula e parmesão", Categorias.ENTRADAS, "44.00",
                ing.get("Carne bovina"), ing.get("Rúcula"), ing.get("Parmesão"));

        // Pratos principais
        Prato carbonara = prato("Spaghetti alla carbonara", "Spaghetti com guanciale, ovo, pecorino e pimenta-do-reino", Categorias.PRATOS_PRINCIPAIS, "58.00",
                ing.get("Spaghetti"), ing.get("Guanciale"), ing.get("Ovo"), ing.get("Pecorino"));
        Prato alfredo = prato("Fettuccine Alfredo", "Fettuccine na manteiga com parmesão e creme", Categorias.PRATOS_PRINCIPAIS, "54.00",
                ing.get("Fettuccine"), ing.get("Molho branco"), ing.get("Parmesão"));
        Prato lasanha = prato("Lasanha à bolonhesa", "Camadas de massa, ragù, molho branco e muçarela gratinada", Categorias.PRATOS_PRINCIPAIS, "62.00",
                ing.get("Massa de lasanha"), ing.get("Ragù de carne"), ing.get("Molho branco"), ing.get("Muçarela"));

        // Sobremesas
        Prato tiramisu = prato("Tiramisù", "Biscoito embebido em café com creme de mascarpone e cacau", Categorias.SOBREMESAS, "26.00",
                ing.get("Mascarpone"), ing.get("Café"), ing.get("Cacau"), ing.get("Ovo"), ing.get("Açúcar"));
        Prato pannaCotta = prato("Panna cotta", "Creme cozido com calda de frutas vermelhas", Categorias.SOBREMESAS, "22.00",
                ing.get("Creme de leite"), ing.get("Frutas vermelhas"), ing.get("Açúcar"));
        prato("Cannoli siciliano", "Massa crocante recheada com ricota doce", Categorias.SOBREMESAS, "24.00",
                ing.get("Ricota"), ing.get("Açúcar"));

        // Bebidas
        Prato limonada = prato("Limonada siciliana", "Limonada de limão siciliano, 400 ml", Categorias.BEBIDAS, "14.00",
                ing.get("Limão siciliano"), ing.get("Açúcar"));
        prato("Água mineral", "Com ou sem gás, 500 ml", Categorias.BEBIDAS, "7.00");
        Prato vinho = prato("Vinho tinto (taça)", "Chianti, taça de 150 ml", Categorias.BEBIDAS, "32.00");

        Promocao semana = new Promocao();
        semana.setNome("Semana da massa fresca");
        semana.setDataInicio(LocalDate.now().minusDays(2));
        semana.setDataFim(LocalDate.now().plusDays(5));
        semana.setTipoDesconto(TipoDesconto.PERCENTUAL);
        semana.setDesconto(new BigDecimal("15"));
        semana.getPratos().addAll(List.of(lasanha, alfredo));
        promocaoRepository.save(semana);

        Promocao bebida = new Promocao();
        bebida.setNome("Limonada com desconto");
        bebida.setDataInicio(LocalDate.now());
        bebida.setDataFim(LocalDate.now().plusDays(30));
        bebida.setTipoDesconto(TipoDesconto.VALOR);
        bebida.setDesconto(new BigDecimal("4.00"));
        bebida.getPratos().add(limonada);
        promocaoRepository.save(bebida);

        for (int numero = 1; numero <= 12; numero++) {
            mesaRepository.save(new Mesa(numero, numero <= 4 ? 4 : 6));
        }

        // Um pedido já finalizado e avaliado, para o histórico e o relatório de avaliações
        Pedido antigo = new Pedido();
        antigo.setCliente(joao);
        antigo.setMesa(mesaRepository.findById(3).orElseThrow());
        antigo.setFuncionario(garcom);
        antigo.setDataHora(LocalDateTime.now().minusHours(3));
        antigo.setStatus(StatusPedido.FINALIZADO);
        antigo.adicionarItem(new ItemPedido(carbonara, 1, carbonara.getPreco()));
        antigo.adicionarItem(new ItemPedido(tiramisu, 1, tiramisu.getPreco()));
        antigo.adicionarItem(new ItemPedido(vinho, 1, vinho.getPreco()));
        pedidoRepository.save(antigo);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPedido(antigo);
        avaliacao.setNota(9);
        avaliacao.setComentario("A carbonara estava perfeita e o atendimento foi rápido.");
        avaliacao.setDataHora(LocalDateTime.now().minusHours(2));
        avaliacaoRepository.save(avaliacao);

        // Um pedido finalizado ainda sem avaliação, para testar o UC-3
        Pedido paraAvaliar = new Pedido();
        paraAvaliar.setCliente(clienteRepository.findById("66666666666").orElseThrow());
        paraAvaliar.setMesa(mesaRepository.findById(5).orElseThrow());
        paraAvaliar.setFuncionario(garcom);
        paraAvaliar.setDataHora(LocalDateTime.now().minusHours(1));
        paraAvaliar.setStatus(StatusPedido.FINALIZADO);
        paraAvaliar.adicionarItem(new ItemPedido(carbonara, 2, carbonara.getPreco()));
        paraAvaliar.adicionarItem(new ItemPedido(pannaCotta, 1, pannaCotta.getPreco()));
        paraAvaliar.adicionarItem(new ItemPedido(limonada, 2, limonada.getPreco()));
        pedidoRepository.save(paraAvaliar);

        // Um pedido em andamento, ocupando a mesa 2, para a tela da cozinha
        Pedido emAndamento = new Pedido();
        emAndamento.setCliente(clienteRepository.findById("55555555555").orElseThrow());
        emAndamento.setMesa(mesaRepository.findById(2).orElseThrow());
        emAndamento.setFuncionario(gerente);
        emAndamento.setDataHora(LocalDateTime.now().minusMinutes(10));
        emAndamento.setStatus(StatusPedido.AGUARDANDO_PREPARO);
        emAndamento.adicionarItem(new ItemPedido(lasanha, 1, semana.aplicar(lasanha.getPreco())));
        emAndamento.adicionarItem(new ItemPedido(alfredo, 1, semana.aplicar(alfredo.getPreco())));
        emAndamento.adicionarItem(new ItemPedido(limonada, 2, bebida.aplicar(limonada.getPreco())));
        pedidoRepository.save(emAndamento);
    }

    private Funcionario funcionario(String cpf, String nome, String telefone, Funcao funcao, Turno turno) {
        Funcionario f = new Funcionario();
        f.setCpf(cpf);
        f.setNome(nome);
        f.setTelefone(telefone);
        f.setFuncao(funcao);
        f.setTurno(turno);
        f.setSenha(passwordEncoder.encode("123456"));
        return funcionarioRepository.save(f);
    }

    private Cliente cliente(String cpf, String nome, String telefone, String preferencias, String alergias, Integer mesa) {
        Cliente c = new Cliente();
        c.setCpf(cpf);
        c.setNome(nome);
        c.setTelefone(telefone);
        c.setPreferencias(preferencias);
        c.setAlergias(alergias);
        c.setMesaPreferida(mesa);
        return clienteRepository.save(c);
    }

    private Prato prato(String nome, String descricao, String categoria, String preco, Ingrediente... ingredientes) {
        Prato p = new Prato();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setCategoria(categoria);
        p.setPreco(new BigDecimal(preco));
        p.getIngredientes().addAll(Arrays.asList(ingredientes));
        return pratoRepository.save(p);
    }
}
