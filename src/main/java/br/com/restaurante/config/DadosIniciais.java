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
        for (String nome : Arrays.asList("Spaghetti", "Fettuccine", "Penne", "Rigatoni", "Linguine", "Tagliatelle",
                "Massa de lasanha", "Ravioli", "Nhoque de batata", "Fusilli", "Molho de tomate", "Molho branco",
                "Ragù de carne", "Bacon", "Guanciale", "Ovo", "Parmesão", "Pecorino", "Muçarela", "Ricota",
                "Burrata", "Manjericão", "Alho", "Azeite", "Pimenta calabresa", "Espinafre", "Cogumelos",
                "Camarão", "Vôngole", "Frango", "Abobrinha", "Tomate cereja", "Pão italiano", "Carne bovina",
                "Rúcula", "Arroz arbóreo", "Mascarpone", "Café", "Cacau", "Creme de leite", "Frutas vermelhas",
                "Limão siciliano", "Açúcar")) {
            ing.put(nome, ingredienteRepository.save(new Ingrediente(nome)));
        }

        // Massas
        Prato carbonara = prato("Spaghetti alla carbonara", "Spaghetti com guanciale, ovo, pecorino e pimenta-do-reino", "Massas", "58.00",
                ing.get("Spaghetti"), ing.get("Guanciale"), ing.get("Ovo"), ing.get("Pecorino"));
        Prato bolonhesa = prato("Spaghetti à bolonhesa", "Spaghetti com ragù de carne cozido lentamente", "Massas", "52.00",
                ing.get("Spaghetti"), ing.get("Ragù de carne"), ing.get("Parmesão"));
        Prato alfredo = prato("Fettuccine Alfredo", "Fettuccine na manteiga com parmesão e creme", "Massas", "54.00",
                ing.get("Fettuccine"), ing.get("Molho branco"), ing.get("Parmesão"));
        Prato arrabbiata = prato("Penne all'arrabbiata", "Penne com molho de tomate, alho e pimenta calabresa", "Massas", "46.00",
                ing.get("Penne"), ing.get("Molho de tomate"), ing.get("Alho"), ing.get("Pimenta calabresa"));
        Prato lasanha = prato("Lasanha à bolonhesa", "Camadas de massa, ragù, molho branco e muçarela gratinada", "Massas", "62.00",
                ing.get("Massa de lasanha"), ing.get("Ragù de carne"), ing.get("Molho branco"), ing.get("Muçarela"));
        prato("Ravioli de ricota e espinafre", "Ravioli recheado na manteiga de sálvia", "Massas", "59.00",
                ing.get("Ravioli"), ing.get("Ricota"), ing.get("Espinafre"));
        Prato nhoque = prato("Nhoque ao sugo", "Nhoque de batata com molho de tomate e manjericão", "Massas", "48.00",
                ing.get("Nhoque de batata"), ing.get("Molho de tomate"), ing.get("Manjericão"));
        prato("Tagliatelle ai funghi", "Tagliatelle com mix de cogumelos e creme", "Massas", "64.00",
                ing.get("Tagliatelle"), ing.get("Cogumelos"), ing.get("Creme de leite"), ing.get("Parmesão"));
        Prato pesto = prato("Linguine al pesto", "Linguine com pesto de manjericão, alho e parmesão", "Massas", "52.00",
                ing.get("Linguine"), ing.get("Manjericão"), ing.get("Alho"), ing.get("Parmesão"), ing.get("Azeite"));
        prato("Spaghetti alle vongole", "Spaghetti com vôngole, alho, azeite e vinho branco", "Massas", "72.00",
                ing.get("Spaghetti"), ing.get("Vôngole"), ing.get("Alho"), ing.get("Azeite"));
        prato("Rigatoni all'amatriciana", "Rigatoni com guanciale, tomate e pecorino", "Massas", "56.00",
                ing.get("Rigatoni"), ing.get("Guanciale"), ing.get("Molho de tomate"), ing.get("Pecorino"));
        prato("Spaghetti aglio, olio e peperoncino", "Spaghetti com alho, azeite e pimenta", "Massas", "42.00",
                ing.get("Spaghetti"), ing.get("Alho"), ing.get("Azeite"), ing.get("Pimenta calabresa"));
        prato("Linguine ao camarão", "Linguine com camarão, tomate cereja e manjericão", "Massas", "78.00",
                ing.get("Linguine"), ing.get("Camarão"), ing.get("Tomate cereja"), ing.get("Manjericão"));
        prato("Fusilli primavera", "Fusilli com abobrinha, tomate cereja e azeite", "Massas", "47.00",
                ing.get("Fusilli"), ing.get("Abobrinha"), ing.get("Tomate cereja"), ing.get("Azeite"));
        prato("Penne ao frango e molho branco", "Penne com frango desfiado e molho branco", "Massas", "53.00",
                ing.get("Penne"), ing.get("Frango"), ing.get("Molho branco"));

        // Antipasti
        prato("Bruschetta al pomodoro", "Pão italiano tostado com tomate, alho e manjericão", "Antipasti", "28.00",
                ing.get("Pão italiano"), ing.get("Tomate cereja"), ing.get("Alho"), ing.get("Manjericão"));
        prato("Burrata", "Burrata com tomate cereja, rúcula e azeite", "Antipasti", "48.00",
                ing.get("Burrata"), ing.get("Tomate cereja"), ing.get("Rúcula"), ing.get("Azeite"));
        prato("Carpaccio", "Lâminas de carne crua com rúcula e parmesão", "Antipasti", "44.00",
                ing.get("Carne bovina"), ing.get("Rúcula"), ing.get("Parmesão"));

        // Risotti
        prato("Risotto ai funghi", "Arroz arbóreo com cogumelos e parmesão", "Risotti", "66.00",
                ing.get("Arroz arbóreo"), ing.get("Cogumelos"), ing.get("Parmesão"));
        prato("Risotto al limone", "Arroz arbóreo com limão siciliano e parmesão", "Risotti", "58.00",
                ing.get("Arroz arbóreo"), ing.get("Limão siciliano"), ing.get("Parmesão"));

        // Dolci
        Prato tiramisu = prato("Tiramisù", "Biscoito embebido em café com creme de mascarpone e cacau", "Dolci", "26.00",
                ing.get("Mascarpone"), ing.get("Café"), ing.get("Cacau"), ing.get("Ovo"), ing.get("Açúcar"));
        prato("Panna cotta", "Creme cozido com calda de frutas vermelhas", "Dolci", "22.00",
                ing.get("Creme de leite"), ing.get("Frutas vermelhas"), ing.get("Açúcar"));
        prato("Cannoli siciliano", "Massa crocante recheada com ricota doce", "Dolci", "24.00",
                ing.get("Ricota"), ing.get("Açúcar"));

        // Bevande
        Prato limonada = prato("Limonada siciliana", "Limonada de limão siciliano, 400 ml", "Bevande", "14.00",
                ing.get("Limão siciliano"), ing.get("Açúcar"));
        prato("Água mineral", "Com ou sem gás, 500 ml", "Bevande", "7.00");
        prato("Refrigerante", "Lata 350 ml", "Bevande", "8.00");
        Prato vinho = prato("Vinho tinto (taça)", "Chianti, taça de 150 ml", "Bevande", "32.00");
        prato("Espresso", "Café espresso italiano", "Bevande", "8.00", ing.get("Café"));

        Promocao semana = new Promocao();
        semana.setNome("Semana da massa fresca");
        semana.setDataInicio(LocalDate.now().minusDays(2));
        semana.setDataFim(LocalDate.now().plusDays(5));
        semana.setTipoDesconto(TipoDesconto.PERCENTUAL);
        semana.setDesconto(new BigDecimal("15"));
        semana.getPratos().addAll(List.of(lasanha, alfredo, nhoque));
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
        paraAvaliar.adicionarItem(new ItemPedido(pesto, 1, pesto.getPreco()));
        paraAvaliar.adicionarItem(new ItemPedido(arrabbiata, 1, arrabbiata.getPreco()));
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
        emAndamento.adicionarItem(new ItemPedido(bolonhesa, 1, bolonhesa.getPreco()));
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
