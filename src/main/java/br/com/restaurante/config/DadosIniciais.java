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

        Cliente joao = cliente("44444444444", "João Silva", "16988880001", "Prefere mesa perto da janela", null, 3);
        cliente("55555555555", "Maria Souza", "16988880002", "Vegetariana", "Amendoim", null);
        cliente("66666666666", "Pedro Lima", "16988880003", null, "Lactose", 5);

        Map<String, Ingrediente> ing = new HashMap<>();
        for (String nome : Arrays.asList("Arroz", "Feijão", "Carne bovina", "Frango", "Batata", "Alface", "Tomate",
                "Queijo", "Massa", "Molho de tomate", "Leite condensado", "Chocolate", "Limão", "Açúcar")) {
            ing.put(nome, ingredienteRepository.save(new Ingrediente(nome)));
        }

        Prato pf = prato("Prato feito", "Arroz, feijão, bife acebolado e salada", "Pratos principais", "32.90",
                ing.get("Arroz"), ing.get("Feijão"), ing.get("Carne bovina"), ing.get("Alface"), ing.get("Tomate"));
        Prato frango = prato("Frango grelhado", "Filé de frango grelhado com batatas", "Pratos principais", "36.50",
                ing.get("Frango"), ing.get("Batata"));
        Prato lasanha = prato("Lasanha à bolonhesa", "Massa fresca, molho de tomate e queijo gratinado", "Massas", "42.00",
                ing.get("Massa"), ing.get("Molho de tomate"), ing.get("Carne bovina"), ing.get("Queijo"));
        prato("Salada da casa", "Folhas, tomate e queijo branco", "Entradas", "24.00",
                ing.get("Alface"), ing.get("Tomate"), ing.get("Queijo"));
        prato("Batata frita", "Porção de batata frita crocante", "Entradas", "22.00", ing.get("Batata"));
        Prato pudim = prato("Pudim", "Pudim de leite condensado", "Sobremesas", "14.00", ing.get("Leite condensado"), ing.get("Açúcar"));
        prato("Brownie", "Brownie de chocolate", "Sobremesas", "16.00", ing.get("Chocolate"), ing.get("Açúcar"));
        Prato limonada = prato("Limonada", "Limonada natural 500 ml", "Bebidas", "9.00", ing.get("Limão"), ing.get("Açúcar"));

        Promocao semana = new Promocao();
        semana.setNome("Semana da massa");
        semana.setDataInicio(LocalDate.now().minusDays(2));
        semana.setDataFim(LocalDate.now().plusDays(5));
        semana.setTipoDesconto(TipoDesconto.PERCENTUAL);
        semana.setDesconto(new BigDecimal("15"));
        semana.getPratos().add(lasanha);
        promocaoRepository.save(semana);

        Promocao bebida = new Promocao();
        bebida.setNome("Limonada com desconto");
        bebida.setDataInicio(LocalDate.now());
        bebida.setDataFim(LocalDate.now().plusDays(30));
        bebida.setTipoDesconto(TipoDesconto.VALOR);
        bebida.setDesconto(new BigDecimal("2.00"));
        bebida.getPratos().add(limonada);
        promocaoRepository.save(bebida);

        for (int numero = 1; numero <= 8; numero++) {
            mesaRepository.save(new Mesa(numero, numero <= 4 ? 4 : 6));
        }

        // Um pedido já finalizado e avaliado, para o histórico e o relatório de avaliações
        Pedido antigo = new Pedido();
        antigo.setCliente(joao);
        antigo.setMesa(mesaRepository.findById(3).orElseThrow());
        antigo.setFuncionario(garcom);
        antigo.setDataHora(LocalDateTime.now().minusHours(3));
        antigo.setStatus(StatusPedido.FINALIZADO);
        antigo.adicionarItem(new ItemPedido(pf, 1, pf.getPreco()));
        antigo.adicionarItem(new ItemPedido(pudim, 1, pudim.getPreco()));
        pedidoRepository.save(antigo);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPedido(antigo);
        avaliacao.setNota(9);
        avaliacao.setComentario("Comida ótima e atendimento rápido.");
        avaliacao.setDataHora(LocalDateTime.now().minusHours(2));
        avaliacaoRepository.save(avaliacao);

        // Um pedido finalizado ainda sem avaliação, para testar o UC-3
        Pedido paraAvaliar = new Pedido();
        paraAvaliar.setCliente(clienteRepository.findById("66666666666").orElseThrow());
        paraAvaliar.setMesa(mesaRepository.findById(5).orElseThrow());
        paraAvaliar.setFuncionario(garcom);
        paraAvaliar.setDataHora(LocalDateTime.now().minusHours(1));
        paraAvaliar.setStatus(StatusPedido.FINALIZADO);
        paraAvaliar.adicionarItem(new ItemPedido(frango, 2, frango.getPreco()));
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
        emAndamento.adicionarItem(new ItemPedido(limonada, 1, bebida.aplicar(limonada.getPreco())));
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
