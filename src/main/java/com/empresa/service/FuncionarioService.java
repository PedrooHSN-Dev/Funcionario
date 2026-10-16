package com.empresa.service;

import com.empresa.exception.PersistenciaException;
import com.empresa.exception.ValidacaoException;
import com.empresa.model.Funcionario;
import com.empresa.repository.FuncionarioRepository;
import com.empresa.util.Validador;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final List<Funcionario> funcionarios;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
        this.funcionarios = repository.carregar();
    }

    public void cadastrar(Funcionario funcionario) throws ValidacaoException {
        Validador.validar(funcionario);
        if (buscarPorMatricula(funcionario.getMatricula()).isPresent()) {
            throw new ValidacaoException("Já existe funcionário com a matrícula " + funcionario.getMatricula() + ".");
        }
        if (existeCpf(funcionario.getCpf())) {
            throw new ValidacaoException("Já existe funcionário com o CPF " + funcionario.getCpf() + ".");
        }
        funcionarios.add(funcionario);
        try {
            repository.salvar(funcionarios);
        } catch (PersistenciaException e) {
            funcionarios.remove(funcionario);
            throw e;
        }
    }

    public void excluir(String matricula) throws ValidacaoException {
        Funcionario funcionario = buscarPorMatricula(matricula)
                .orElseThrow(() -> new ValidacaoException("Funcionário com matrícula " + matricula + " não encontrado."));
        funcionarios.remove(funcionario);
        try {
            repository.salvar(funcionarios);
        } catch (PersistenciaException e) {
            funcionarios.add(funcionario);
            throw e;
        }
    }

    public Optional<Funcionario> buscarPorMatricula(String matricula) {
        return funcionarios.stream()
                .filter(f -> f.getMatricula().equals(matricula))
                .findFirst();
    }

    public List<Funcionario> listarTodos() {
        return funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getMatricula))
                .collect(Collectors.toList());
    }

    public List<String> listarCargos() {
        return funcionarios.stream()
                .map(Funcionario::getCargo)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private boolean existeCpf(String cpf) {
        String numeros = Validador.apenasDigitos(cpf);
        return funcionarios.stream()
                .map(f -> Validador.apenasDigitos(f.getCpf()))
                .anyMatch(numeros::equals);
    }
}
