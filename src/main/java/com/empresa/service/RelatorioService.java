package com.empresa.service;

import com.empresa.model.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RelatorioService {

    public List<Funcionario> filtrarPorCargo(List<Funcionario> funcionarios, String cargo) {
        return funcionarios.stream()
                .filter(f -> f.getCargo().equalsIgnoreCase(cargo.trim()))
                .sorted(Comparator.comparing(Funcionario::getNome))
                .collect(Collectors.toList());
    }

    public List<Funcionario> filtrarPorFaixaSalarial(List<Funcionario> funcionarios, BigDecimal minimo, BigDecimal maximo) {
        return funcionarios.stream()
                .filter(f -> f.getSalario().compareTo(minimo) >= 0)
                .filter(f -> f.getSalario().compareTo(maximo) <= 0)
                .sorted(Comparator.comparing(Funcionario::getSalario))
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> mediaSalarialPorCargo(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getCargo, TreeMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), this::calcularMedia)));
    }

    public Map<String, List<Funcionario>> agruparPorCidade(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(f -> f.getEndereco().getCidade() + "/" + f.getEndereco().getEstado(),
                        TreeMap::new, Collectors.toList()));
    }

    public Map<String, List<Funcionario>> agruparPorEstado(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(f -> f.getEndereco().getEstado(), TreeMap::new, Collectors.toList()));
    }

    public BigDecimal somarSalarios(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularMedia(List<Funcionario> funcionarios) {
        return somarSalarios(funcionarios).divide(BigDecimal.valueOf(funcionarios.size()), 2, RoundingMode.HALF_UP);
    }
}
