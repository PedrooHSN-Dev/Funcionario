package com.empresa;

import com.empresa.controller.FuncionarioController;
import com.empresa.exception.PersistenciaException;
import com.empresa.repository.FuncionarioRepository;
import com.empresa.service.FuncionarioService;
import com.empresa.service.RelatorioService;
import com.empresa.view.FuncionarioView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.nio.file.Path;

public class App extends Application {

    private static final Path ARQUIVO_CSV = Path.of("funcionarios.csv");

    @Override
    public void start(Stage stage) {
        FuncionarioService funcionarioService;
        try {
            funcionarioService = new FuncionarioService(new FuncionarioRepository(ARQUIVO_CSV));
        } catch (PersistenciaException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alerta.setHeaderText("Não foi possível carregar os dados");
            alerta.showAndWait();
            Platform.exit();
            return;
        }

        FuncionarioView view = new FuncionarioView();
        new FuncionarioController(view, funcionarioService, new RelatorioService()).inicializar();

        stage.setTitle("Cadastro de Funcionários");
        stage.setScene(new Scene(view.getRoot(), 1200, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
