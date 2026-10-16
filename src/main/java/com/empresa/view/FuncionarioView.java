package com.empresa.view;

import com.empresa.model.Funcionario;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FuncionarioView {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public final TextField txtMatricula = new TextField();
    public final TextField txtNome = new TextField();
    public final TextField txtCpf = new TextField();
    public final DatePicker dpDataNascimento = criarDatePicker("Ex.: 10/05/1990");
    public final TextField txtCargo = new TextField();
    public final TextField txtSalario = new TextField();
    public final DatePicker dpDataContratacao = criarDatePicker("Ex.: 01/03/2024");
    public final TextField txtLogradouro = new TextField();
    public final TextField txtNumero = new TextField();
    public final TextField txtComplemento = new TextField();
    public final TextField txtBairro = new TextField();
    public final TextField txtCidade = new TextField();
    public final TextField txtEstado = new TextField();
    public final TextField txtCep = new TextField();

    public final Button btnCadastrar = new Button("Cadastrar");
    public final Button btnLimpar = new Button("Limpar");

    public final TextField txtConsultaMatricula = new TextField();
    public final Button btnConsultar = new Button("Consultar");
    public final Button btnListarTodos = new Button("Listar todos");
    public final Button btnExcluir = new Button("Excluir selecionado");

    public final TableView<Funcionario> tabela = new TableView<>();
    public final TableColumn<Funcionario, String> colMatricula = new TableColumn<>("Matrícula");
    public final TableColumn<Funcionario, String> colNome = new TableColumn<>("Nome");
    public final TableColumn<Funcionario, String> colCpf = new TableColumn<>("CPF");
    public final TableColumn<Funcionario, String> colNascimento = new TableColumn<>("Nascimento");
    public final TableColumn<Funcionario, String> colCargo = new TableColumn<>("Cargo");
    public final TableColumn<Funcionario, String> colSalario = new TableColumn<>("Salário");
    public final TableColumn<Funcionario, String> colContratacao = new TableColumn<>("Contratação");
    public final TableColumn<Funcionario, String> colCidade = new TableColumn<>("Cidade");
    public final TableColumn<Funcionario, String> colEstado = new TableColumn<>("UF");

    public final ComboBox<String> cbTipoRelatorio = new ComboBox<>();
    public final ComboBox<String> cbFiltroCargo = new ComboBox<>();
    public final TextField txtSalarioMinimo = new TextField();
    public final TextField txtSalarioMaximo = new TextField();
    public final Button btnGerarRelatorio = new Button("Gerar relatório");
    public final TextArea txtRelatorio = new TextArea();

    private final TabPane root = new TabPane();

    public FuncionarioView() {
        Tab abaCadastro = new Tab("Cadastro", criarAbaCadastro());
        Tab abaRelatorios = new Tab("Relatórios", criarAbaRelatorios());
        abaCadastro.setClosable(false);
        abaRelatorios.setClosable(false);
        root.getTabs().addAll(abaCadastro, abaRelatorios);
    }

    public Parent getRoot() {
        return root;
    }

    private VBox criarAbaCadastro() {
        txtMatricula.setPromptText("Ex.: 123456");
        txtNome.setPromptText("Ex.: Maria da Silva");
        txtCpf.setPromptText("Ex.: 529.982.247-25");
        txtCargo.setPromptText("Ex.: Analista");
        txtSalario.setPromptText("Ex.: 3500,00");
        txtLogradouro.setPromptText("Ex.: Rua 10");
        txtNumero.setPromptText("Ex.: 120");
        txtComplemento.setPromptText("Ex.: Apto 301");
        txtBairro.setPromptText("Ex.: Setor Oeste");
        txtCidade.setPromptText("Ex.: Goiânia");
        txtEstado.setPromptText("Ex.: GO");
        txtCep.setPromptText("Ex.: 74120-020");
        txtConsultaMatricula.setPromptText("Ex.: 100001");

        GridPane dadosPessoais = criarGrid();
        adicionarCampo(dadosPessoais, 0, 0, "Matrícula", txtMatricula);
        adicionarCampo(dadosPessoais, 0, 2, "Nome", txtNome);
        adicionarCampo(dadosPessoais, 1, 0, "CPF", txtCpf);
        adicionarCampo(dadosPessoais, 1, 2, "Data de nascimento", dpDataNascimento);
        adicionarCampo(dadosPessoais, 2, 0, "Cargo", txtCargo);
        adicionarCampo(dadosPessoais, 2, 2, "Salário", txtSalario);
        adicionarCampo(dadosPessoais, 3, 0, "Data de contratação", dpDataContratacao);

        GridPane endereco = criarGrid();
        adicionarCampo(endereco, 0, 0, "Logradouro", txtLogradouro);
        adicionarCampo(endereco, 0, 2, "Número", txtNumero);
        adicionarCampo(endereco, 1, 0, "Complemento", txtComplemento);
        adicionarCampo(endereco, 1, 2, "Bairro", txtBairro);
        adicionarCampo(endereco, 2, 0, "Cidade", txtCidade);
        adicionarCampo(endereco, 2, 2, "Estado (UF)", txtEstado);
        adicionarCampo(endereco, 3, 0, "CEP", txtCep);

        TitledPane painelDadosPessoais = criarPainel("Dados pessoais", dadosPessoais);
        TitledPane painelEndereco = criarPainel("Endereço", endereco);
        HBox.setHgrow(painelDadosPessoais, Priority.ALWAYS);
        HBox.setHgrow(painelEndereco, Priority.ALWAYS);

        HBox grupos = new HBox(10, painelDadosPessoais, painelEndereco);
        HBox botoesFormulario = new HBox(10, btnCadastrar, btnLimpar);
        TitledPane painelFormulario = criarPainel("Dados do funcionário", new VBox(10, grupos, botoesFormulario));

        HBox barraConsulta = new HBox(10, new Label("Consultar por matrícula:"), txtConsultaMatricula,
                btnConsultar, btnListarTodos, btnExcluir);
        barraConsulta.setStyle("-fx-alignment: center-left;");

        tabela.getColumns().addAll(List.of(colMatricula, colNome, colCpf, colNascimento, colCargo,
                colSalario, colContratacao, colCidade, colEstado));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum funcionário cadastrado."));
        VBox.setVgrow(tabela, Priority.ALWAYS);

        VBox aba = new VBox(10, painelFormulario, barraConsulta, tabela);
        aba.setPadding(new Insets(10));
        return aba;
    }

    private VBox criarAbaRelatorios() {
        cbFiltroCargo.setEditable(true);
        cbFiltroCargo.setPromptText("Ex.: Analista");
        cbFiltroCargo.setPrefWidth(200);
        txtSalarioMinimo.setPromptText("Mínimo. Ex.: 2000,00");
        txtSalarioMaximo.setPromptText("Máximo. Ex.: 8000,00");

        HBox filtros = new HBox(10, new Label("Relatório:"), cbTipoRelatorio, cbFiltroCargo,
                txtSalarioMinimo, txtSalarioMaximo, btnGerarRelatorio);
        filtros.setStyle("-fx-alignment: center-left;");

        txtRelatorio.setEditable(false);
        txtRelatorio.setStyle("-fx-font-family: 'monospace';");
        VBox.setVgrow(txtRelatorio, Priority.ALWAYS);

        VBox aba = new VBox(10, filtros, txtRelatorio);
        aba.setPadding(new Insets(10));
        return aba;
    }

    private GridPane criarGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        return grid;
    }

    private TitledPane criarPainel(String titulo, Node conteudo) {
        TitledPane painel = new TitledPane(titulo, conteudo);
        painel.setCollapsible(false);
        painel.setMaxHeight(Double.MAX_VALUE);
        return painel;
    }

    private void adicionarCampo(GridPane grid, int linha, int coluna, String rotulo, Control campo) {
        Label label = new Label(rotulo);
        label.setMinWidth(Region.USE_PREF_SIZE);
        campo.setPrefWidth(130);
        campo.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(campo, Priority.ALWAYS);
        grid.add(label, coluna, linha);
        grid.add(campo, coluna + 1, linha);
    }

    private static DatePicker criarDatePicker(String exemplo) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText(exemplo);
        datePicker.getEditor().setTextFormatter(new TextFormatter<String>(FuncionarioView::aplicarMascaraData));
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate data) {
                return data == null ? "" : FORMATO_DATA.format(data);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), FORMATO_DATA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
        return datePicker;
    }

    private static TextFormatter.Change aplicarMascaraData(TextFormatter.Change alteracao) {
        if (!alteracao.isContentChange()) {
            return alteracao;
        }
        String novoTexto = alteracao.getControlNewText();
        String digitos = novoTexto.replaceAll("\\D", "");
        if (digitos.length() > 8) {
            return null;
        }
        int digitosAntesDoCursor = novoTexto.substring(0, alteracao.getCaretPosition()).replaceAll("\\D", "").length();
        String formatado = formatarData(digitos, alteracao.isAdded());

        alteracao.setRange(0, alteracao.getControlText().length());
        alteracao.setText(formatado);
        int cursor = posicaoCursor(formatado, digitosAntesDoCursor);
        alteracao.selectRange(cursor, cursor);
        return alteracao;
    }

    private static String formatarData(String digitos, boolean digitando) {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < digitos.length(); i++) {
            if (i == 2 || i == 4) {
                data.append('/');
            }
            data.append(digitos.charAt(i));
        }
        if (digitando && (digitos.length() == 2 || digitos.length() == 4)) {
            data.append('/');
        }
        return data.toString();
    }

    private static int posicaoCursor(String formatado, int digitosAntesDoCursor) {
        int contador = 0;
        for (int i = 0; i < formatado.length(); i++) {
            if (Character.isDigit(formatado.charAt(i))) {
                if (contador == digitosAntesDoCursor) {
                    return i;
                }
                contador++;
            }
        }
        return formatado.length();
    }
}
