package com.example.Monitores;

import java.util.ArrayList;
import java.util.List;

public class Validador {
    private final List<Rango> rangosAceptables = new ArrayList<>();


    public Validador() {
        rangosAceptables.add(new Rango(0.0, 115.0, Tipos.TEMPERATURA));
        rangosAceptables.add(new Rango(0.0, 14.0, Tipos.PH));
        rangosAceptables.add(new Rango(0.0, 15.0, Tipos.OXIGENO));
    }
    public boolean validar(Double valor, Tipos tipo) {
        for (Rango rango : rangosAceptables) {
            if (rango.getTipo() == tipo) {
                if (valor >= rango.getMin() && valor <= rango.getMax()) {
                    return true;
                }
            }
        }
        return false;
    }
}
