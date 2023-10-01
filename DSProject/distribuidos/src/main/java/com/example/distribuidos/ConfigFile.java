package com.example.distribuidos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigFile {
    String nombre;
    double probabilidadRango;
    double probabilidadFueraRango;
    double probabilidadError;

    public ConfigFile(double probabilidadRango, double probabilidadFueraRango, double probabilidadError) {
        this.probabilidadRango = probabilidadRango;
        this.probabilidadFueraRango = probabilidadFueraRango;
        this.probabilidadError = probabilidadError;
    }

    public ConfigFile(String nombre) {
        this.nombre = nombre;
    }

    public boolean leerArchivo() {
        String[] fields = new String[3];
        if (this.nombre == null) {
            System.out.println("No se ha especificado un nombre de archivo");
            return false;
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(this.nombre))) {
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    String line = strCurrentLine.trim();
                    if(line.startsWith("Pin")){
                        fields[0] = line.split(" ")[1].trim();
                    } else if(line.startsWith("Pout")){
                        fields[1] = line.split(" ")[1].trim();
                    } else if(line.startsWith("Perror")){
                        fields[2] = line.split(" ")[1].trim();
                    }
                }
                this.probabilidadRango = Double.parseDouble(fields[0]);
                this.probabilidadFueraRango = Double.parseDouble(fields[1]);
                this.probabilidadError = Double.parseDouble(fields[2]);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getProbabilidadRango() {
        return probabilidadRango;
    }

    public void setProbabilidadRango(double probabilidadRango) {
        this.probabilidadRango = probabilidadRango;
    }

    public double getProbabilidadFueraRango() {
        return probabilidadFueraRango;
    }

    public void setProbabilidadFueraRango(double probabilidadFueraRango) {
        this.probabilidadFueraRango = probabilidadFueraRango;
    }

    public double getProbabilidadError() {
        return probabilidadError;
    }

    public void setProbabilidadError(double probabilidadError) {
        this.probabilidadError = probabilidadError;
    }
}

