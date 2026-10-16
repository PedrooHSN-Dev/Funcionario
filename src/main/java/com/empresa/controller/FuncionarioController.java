package com.empresa.controller;

import com.empresa.exception.PersistenciaException;
import com.empresa.exception.ValidacaoException;
import com.empresa.model.Endereco;
import com.empresa.model.Funcionario;
import com.empresa.service.FuncionarioService;
import com.empresa.service.RelatorioService;
import com.empresa.view.FuncionarioView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FuncionarioController {

    private static final String RELATORIO_CARGO = "Funcionários por cargo";
    private static final String RELATORIO_FAIXA = "Funcionários por faixa salarial";
    private static final String RELATORIO_MEDIA = "Média salarial por cargo";
    private static final String RELATORIO_CIDADE = "Funcionários agrupados por cidade";
    private static final String RELATORIO_ESTADO = "Funcionários agrupados por estado";
    private static final String FORMATO_LINHA = "%-10s %-30s %-20s %15s%n";

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final FuncionarioView view;
    private final FuncionarioService funcionarioService;
    private final RelatorioService relatorioService;
    private final ObservableList<Funcionario> itensTabela = FXCollections.observableArrayList();
    private final ObservableList<String> cargos = FXCollections.observableArrayList();
    private final FilteredList<String> cargosFiltrados = new FilteredList<>(cargos);

    public FuncionarioController(FuncionarioView view, FuncionarioService funcionarioService,
                                 RelatorioService relatorioService) {
        this.view = view;
        this.funcionarioService = funcionarioService;
        this.relatorioService = relatorioService;
    }

    public void inicializar() {
        configurarTabela();
        configurarRelatorios();

        view.btnCadastrar.setOnAction(evento -> cadastrar());
        view.btnLimpar.setOnAction(evento -> limparFormulario());
        view.btnConsultar.setOnAction(evento -> consultar());
        view.txtConsultaMatricula.setOnAction(evento -> consultar());
        view.btnListarTodos.setOnAction(evento -> listarTodos());
        view.btnExcluir.setOnAction(evento -> excluir());
        view.btnGerarRelatorio.setOnAction(evento -> gerarRelatorio());

        listarTodos();
    }

    private void configurarTabela() {
        view.colMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        view.colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        view.colCpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCpf()));
        view.colNascimento.setCellValueFactory(c -> new SimpleStringProperty(
                FuncionarioView.FORMATO_DATA.format(c.getValue().getDataNascimento())));
        view.colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));
        view.colSalario.setCellValueFactory(c -> new SimpleStringProperty(moeda.format(c.getValue().getSalario())));
        view.colContratacao.setCellValueFactory(c -> new SimpleStringProperty(
                FuncionarioView.FORMATO_DATA.format(c.getValue().getDataContratacao())));
        view.colCidade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndereco().getCidade()));
        view.colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndereco().getEstado()));
        view.tabela.setItems(itensTabela);
    }

    private void configurarRelatorios() {
        view.cbTipoRelatorio.getItems().addAll(RELATORIO_CARGO, RELATORIO_FAIXA, RELATORIO_MEDIA,
                RELATORIO_CIDADE, RELATORIO_ESTADO);
        view.cbTipoRelatorio.valueProperty().addListener((obs, antigo, novo) -> atualizarFiltros(novo));
        view.cbTipoRelatorio.getSelectionModel().selectFirst();

        view.cbFiltroCargo.setItems(cargosFiltrados);
        view.cbFiltroCargo.getEditor().textProperty().addListener((obs, antigo, novo) -> filtrarCargos(novo));
    }

    private void filtrarCargos(String texto) {
        String selecionado = view.cbFiltroCargo.getSelectionModel().getSelectedItem();
        Platform.runLater(() -> {
            if (texto.equals(selecionado)) {
                return;
            }
            String busca = texto.trim().toLowerCase();
            cargosFiltrados.setPredicate(cargo -> cargo.toLowerCase().contains(busca));
            if (cargosFiltrados.isEmpty()) {
                view.cbFiltroCargo.hide();
            } else if (view.cbFiltroCargo.isFocused() && !view.cbFiltroCargo.isShowing()) {
                view.cbFiltroCargo.show();
            }
        });
    }

    private void atualizarFiltros(String tipo) {
        view.cbFiltroCargo.setDisable(!RELATORIO_CARGO.equals(tipo));
        view.txtSalarioMinimo.setDisable(!RELATORIO_FAIXA.equals(tipo));
        view.txtSalarioMaximo.setDisable(!RELATORIO_FAIXA.equals(tipo));
    }

    private void cadastrar() {
        try {
            funcionarioService.cadastrar(lerFormulario());
            limparFormulario();
            listarTodos();
            mostrarMensagem(Alert.AlertType.INFORMATION, "Funcionário cadastrado com sucesso.");
        } catch (ValidacaoException e) {
            mostrarMensagem(Alert.AlertType.WARNING, e.getMessage());
        } catch (PersistenciaException e) {
            mostrarMensagem(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private void consultar() {
        String matricula = texto(view.txtConsultaMatricula);
        if (matricula.isEmpty()) {
            mostrarMensagem(Alert.AlertType.WARNING, "Informe a matrícula para consulta.");
            return;
        }
        funcionarioService.buscarPorMatricula(matricula).ifPresentOrElse(funcionario -> {
            itensTabela.setAll(funcionario);
            view.tabela.getSelectionModel().select(funcionario);
            mostrarMensagem(Alert.AlertType.INFORMATION, detalhar(funcionario));
        }, () -> mostrarMensagem(Alert.AlertType.WARNING, "Funcionário com matrícula " + matricula + " não encontrado."));
    }

    private void listarTodos() {
        itensTabela.setAll(funcionarioService.listarTodos());
        cargos.setAll(funcionarioService.listarCargos());
        view.txtConsultaMatricula.clear();
    }

    private void excluir() {
        Funcionario selecionado = view.tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarMensagem(Alert.AlertType.WARNING, "Selecione um funcionário na tabela.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir o funcionário " + selecionado.getNome() + "?");
        confirmacao.setHeaderText(null);
        if (confirmacao.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }
        try {
            funcionarioService.excluir(selecionado.getMatricula());
            listarTodos();
            mostrarMensagem(Alert.AlertType.INFORMATION, "Funcionário excluído com sucesso.");
        } catch (ValidacaoException e) {
            mostrarMensagem(Alert.AlertType.WARNING, e.getMessage());
        } catch (PersistenciaException e) {
            mostrarMensagem(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private void gerarRelatorio() {
        List<Funcionario> funcionarios = funcionarioService.listarTodos();
        String tipo = view.cbTipoRelatorio.getValue();
        StringBuilder relatorio = new StringBuilder(tipo.toUpperCase()).append("\n\n");

        switch (tipo) {
            case RELATORIO_CARGO -> {
                String cargo = texto(view.cbFiltroCargo.getEditor());
                if (cargo.isEmpty()) {
                    mostrarMensagem(Alert.AlertType.WARNING, "Informe o cargo.");
                    return;
                }
                relatorio.append("Cargo: ").append(cargo).append("\n\n");
                escreverLista(relatorio, relatorioService.filtrarPorCargo(funcionarios, cargo));
            }
            case RELATORIO_FAIXA -> {
                BigDecimal minimo = converterValor(texto(view.txtSalarioMinimo));
                BigDecimal maximo = converterValor(texto(view.txtSalarioMaximo));
                if (minimo == null || maximo == null || minimo.compareTo(maximo) > 0) {
                    mostrarMensagem(Alert.AlertType.WARNING,
                            "Informe salários mínimo e máximo válidos (mínimo menor ou igual ao máximo).");
                    return;
                }
                relatorio.append("Faixa: ").append(moeda.format(minimo)).append(" a ")
                        .append(moeda.format(maximo)).append("\n\n");
                escreverLista(relatorio, relatorioService.filtrarPorFaixaSalarial(funcionarios, minimo, maximo));
            }
            case RELATORIO_MEDIA -> escreverMedias(relatorio, relatorioService.mediaSalarialPorCargo(funcionarios));
            case RELATORIO_CIDADE -> escreverGrupos(relatorio, relatorioService.agruparPorCidade(funcionarios));
            case RELATORIO_ESTADO -> escreverGrupos(relatorio, relatorioService.agruparPorEstado(funcionarios));
            default -> {
                return;
            }
        }
        view.txtRelatorio.setText(relatorio.toString());
    }

    private void escreverLista(StringBuilder relatorio, List<Funcionario> funcionarios) {
        if (funcionarios.isEmpty()) {
            relatorio.append("Nenhum funcionário encontrado.\n");
            return;
        }
        relatorio.append(String.format(FORMATO_LINHA, "Matrícula", "Nome", "Cargo", "Salário"));
        funcionarios.forEach(f -> relatorio.append(linha(f)));
        relatorio.append("\nTotal de funcionários: ").append(funcionarios.size())
                .append("\nSoma dos salários: ").append(moeda.format(relatorioService.somarSalarios(funcionarios)))
                .append("\n");
    }

    private void escreverMedias(StringBuilder relatorio, Map<String, BigDecimal> medias) {
        if (medias.isEmpty()) {
            relatorio.append("Nenhum funcionário cadastrado.\n");
            return;
        }
        relatorio.append(String.format("%-25s %15s%n", "Cargo", "Média salarial"));
        medias.forEach((cargo, media) -> relatorio.append(String.format("%-25s %15s%n", cargo, moeda.format(media))));
    }

    private void escreverGrupos(StringBuilder relatorio, Map<String, List<Funcionario>> grupos) {
        if (grupos.isEmpty()) {
            relatorio.append("Nenhum funcionário cadastrado.\n");
            return;
        }
        grupos.forEach((grupo, funcionarios) -> {
            relatorio.append(grupo).append(" (").append(funcionarios.size()).append(" funcionário(s))\n");
            funcionarios.forEach(f -> relatorio.append("   ").append(linha(f)));
            relatorio.append("\n");
        });
    }

    private String linha(Funcionario f) {
        return String.format(FORMATO_LINHA, f.getMatricula(), f.getNome(), f.getCargo(), moeda.format(f.getSalario()));
    }

    private String detalhar(Funcionario f) {
        return "Matrícula: " + f.getMatricula()
                + "\nNome: " + f.getNome()
                + "\nCPF: " + f.getCpf()
                + "\nData de nascimento: " + FuncionarioView.FORMATO_DATA.format(f.getDataNascimento())
                + " (" + f.getIdade() + " anos)"
                + "\nCargo: " + f.getCargo()
                + "\nSalário: " + moeda.format(f.getSalario())
                + "\nData de contratação: " + FuncionarioView.FORMATO_DATA.format(f.getDataContratacao())
                + "\nEndereço: " + f.getEndereco();
    }

    private Funcionario lerFormulario() {
        Endereco endereco = new Endereco(
                texto(view.txtLogradouro),
                texto(view.txtNumero),
                texto(view.txtComplemento),
                texto(view.txtBairro),
                texto(view.txtCidade),
                texto(view.txtEstado).toUpperCase(),
                texto(view.txtCep));
        return new Funcionario(
                texto(view.txtMatricula),
                texto(view.txtNome),
                texto(view.txtCpf),
                lerData(view.dpDataNascimento),
                texto(view.txtCargo),
                converterValor(texto(view.txtSalario)),
                lerData(view.dpDataContratacao),
                endereco);
    }

    private void limparFormulario() {
        List.of(view.txtMatricula, view.txtNome, view.txtCpf, view.txtCargo, view.txtSalario,
                view.txtLogradouro, view.txtNumero, view.txtComplemento, view.txtBairro,
                view.txtCidade, view.txtEstado, view.txtCep).forEach(TextField::clear);
        view.dpDataNascimento.setValue(null);
        view.dpDataContratacao.setValue(null);
        view.dpDataNascimento.getEditor().clear();
        view.dpDataContratacao.getEditor().clear();
    }

    private LocalDate lerData(DatePicker datePicker) {
        return datePicker.getConverter().fromString(datePicker.getEditor().getText());
    }

    private BigDecimal converterValor(String texto) {
        String valor = texto.replace("R$", "").trim();
        if (valor.contains(",")) {
            valor = valor.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String texto(TextField campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private void mostrarMensagem(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
