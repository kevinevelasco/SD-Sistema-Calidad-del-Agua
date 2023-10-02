package com.example.Monitores;

public class Rango {
    private Double min;
    private Double max;
    private Tipos tipo;
    public Rango() {
    }

    public Rango(Double min, Double max, Tipos tipo) {
        this.min = min;
        this.max = max;
        this.tipo = tipo;
    }
    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }
}
