package com.empresa.util;

import com.empresa.exception.ValidacaoException;
import com.empresa.model.Endereco;
import com.empresa.model.Funcionario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Validador {

    private static final Pattern MATRICULA = Pattern.compile("\\d{6}");
    private static final Pattern CPF = Pattern.compile("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}");
    private static final Pattern CEP = Pattern.compile("\\d{5}-?\\d{3}");
    private static final Pattern UF = Pattern.compile("[A-Z]{2}");
    private static final int TAMANHO_MINIMO_NOME = 3;
    private static final int IDADE_MINIMA = 18;

    private Validador() {
    }

    public static void validar(Funcionario funcionario) throws ValidacaoException {
        List<String> erros = new ArrayList<>();

        if (!corresponde(MATRICULA, funcionario.getMatricula())) {
            erros.add("Matrícula deve ter 6 dígitos numéricos.");
        }
        if (vazio(funcionario.getNome()) || funcionario.getNome().trim().length() < TAMANHO_MINIMO_NOME) {
            erros.add("Nome deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (!cpfValido(funcionario.getCpf())) {
            erros.add("CPF inválido.");
        }
        if (funcionario.getDataNascimento() == null) {
            erros.add("Data de nascimento é obrigatória.");
        } else if (funcionario.getIdade() < IDADE_MINIMA) {
            erros.add("Funcionário deve ter no mínimo " + IDADE_MINIMA + " anos.");
        }
        if (vazio(funcionario.getCargo())) {
            erros.add("Cargo é obrigatório.");
        }
        if (funcionario.getSalario() == null || funcionario.getSalario().signum() <= 0) {
            erros.add("Salário deve ser um valor positivo.");
        }
        if (funcionario.getDataContratacao() == null) {
            erros.add("Data de contratação é obrigatória.");
        } else if (funcionario.getDataContratacao().isAfter(LocalDate.now())) {
            erros.add("Data de contratação não pode ser futura.");
        }

        validarEndereco(funcionario.getEndereco(), erros);

        if (contemSeparador(funcionario)) {
            erros.add("Os campos não podem conter ponto-e-vírgula (;).");
        }
        if (!erros.isEmpty()) {
            throw new ValidacaoException(String.join("\n", erros));
        }
    }

    public static boolean cpfValido(String cpf) {
        if (!corresponde(CPF, cpf)) {
            return false;
        }
        String numeros = apenasDigitos(cpf);
        if (numeros.chars().distinct().count() == 1) {
            return false;
        }
        return numeros.charAt(9) - '0' == calcularDigito(numeros, 9)
                && numeros.charAt(10) - '0' == calcularDigito(numeros, 10);
    }

    public static String apenasDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    private static int calcularDigito(String numeros, int tamanho) {
        int soma = IntStream.range(0, tamanho)
                .map(i -> (numeros.charAt(i) - '0') * (tamanho + 1 - i))
                .sum();
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static void validarEndereco(Endereco endereco, List<String> erros) {
        if (endereco == null) {
            erros.add("Endereço é obrigatório.");
            return;
        }
        if (vazio(endereco.getLogradouro())) {
            erros.add("Logradouro é obrigatório.");
        }
        if (vazio(endereco.getNumero())) {
            erros.add("Número é obrigatório.");
        }
        if (vazio(endereco.getBairro())) {
            erros.add("Bairro é obrigatório.");
        }
        if (vazio(endereco.getCidade())) {
            erros.add("Cidade é obrigatória.");
        }
        if (!corresponde(UF, endereco.getEstado())) {
            erros.add("Estado deve ser a sigla com 2 letras (ex.: GO).");
        }
        if (!corresponde(CEP, endereco.getCep())) {
            erros.add("CEP inválido (formato 00000-000).");
        }
    }

    private static boolean contemSeparador(Funcionario f) {
        Endereco e = f.getEndereco() == null ? new Endereco() : f.getEndereco();
        return Stream.of(f.getMatricula(), f.getNome(), f.getCpf(), f.getCargo(),
                        e.getLogradouro(), e.getNumero(), e.getComplemento(), e.getBairro(),
                        e.getCidade(), e.getEstado(), e.getCep())
                .filter(Objects::nonNull)
                .anyMatch(campo -> campo.contains(";"));
    }

    private static boolean corresponde(Pattern padrao, String valor) {
        return valor != null && padrao.matcher(valor).matches();
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
