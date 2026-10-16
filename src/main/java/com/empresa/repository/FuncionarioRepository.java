package com.empresa.repository;

import com.empresa.exception.PersistenciaException;
import com.empresa.model.Endereco;
import com.empresa.model.Funcionario;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FuncionarioRepository {

    private static final String SEPARADOR = ";";
    private static final int QUANTIDADE_CAMPOS = 14;
    private static final String CABECALHO = "matricula;nome;cpf;dataNascimento;cargo;salario;dataContratacao;"
            + "logradouro;numero;complemento;bairro;cidade;estado;cep";

    private final Path arquivo;

    public FuncionarioRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    public List<Funcionario> carregar() {
        if (Files.notExists(arquivo)) {
            return new ArrayList<>();
        }
        try (Stream<String> linhas = Files.lines(arquivo, StandardCharsets.UTF_8)) {
            return linhas.skip(1)
                    .filter(linha -> !linha.isBlank())
                    .map(this::paraFuncionario)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler o arquivo " + arquivo.toAbsolutePath(), e);
        }
    }

    public void salvar(List<Funcionario> funcionarios) {
        List<String> linhas = new ArrayList<>();
        linhas.add(CABECALHO);
        funcionarios.stream().map(this::paraLinha).forEach(linhas::add);
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar o arquivo " + arquivo.toAbsolutePath(), e);
        }
    }

    private String paraLinha(Funcionario f) {
        Endereco e = f.getEndereco();
        return String.join(SEPARADOR,
                f.getMatricula(),
                f.getNome(),
                f.getCpf(),
                f.getDataNascimento().toString(),
                f.getCargo(),
                f.getSalario().toPlainString(),
                f.getDataContratacao().toString(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento() == null ? "" : e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getEstado(),
                e.getCep());
    }

    private Funcionario paraFuncionario(String linha) {
        String[] c = linha.split(SEPARADOR, -1);
        if (c.length != QUANTIDADE_CAMPOS) {
            throw new PersistenciaException("Linha inválida no arquivo CSV: " + linha);
        }
        try {
            Endereco endereco = new Endereco(c[7], c[8], c[9], c[10], c[11], c[12], c[13]);
            return new Funcionario(c[0], c[1], c[2], LocalDate.parse(c[3]), c[4],
                    new BigDecimal(c[5]), LocalDate.parse(c[6]), endereco);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new PersistenciaException("Dados inválidos no arquivo CSV: " + linha, e);
        }
    }
}
