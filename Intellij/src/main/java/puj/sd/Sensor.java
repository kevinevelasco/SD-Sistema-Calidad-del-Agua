package puj.sd;


import java.sql.Timestamp;
import java.util.Map;
import java.util.Random;

public class Sensor {
    String tipo;
    double tiempo;
    ConfigFile archivoConfiguracion;
    Map<Timestamp, Double> medidas; //se guardan las medidas con su timestamp

    public Sensor(String tipo, double tiempo, ConfigFile archivoConfiguracion) {
        this.tipo = tipo;
        this.tiempo = tiempo;
        this.archivoConfiguracion = archivoConfiguracion;
    }

    public void generarMedidas() {
        if(this.archivoConfiguracion == null){
            System.out.println("No se ha especificado un archivo de configuracion");
            return;
        } else {
            double min;
            double max;
            double probabilidadRango = this.archivoConfiguracion.getProbabilidadRango();
            double probabilidadFueraRango = this.archivoConfiguracion.getProbabilidadFueraRango();
            double probabilidadError = this.archivoConfiguracion.getProbabilidadError();
            Random random = new Random();
            //TODO falta realizar la logica relacionada con el while infinito que cada tiempo t siga generando medidas
            if (this.tipo.equalsIgnoreCase("temperatura")) {
                min = 68;
                max = 89;
                //TODO según la probabilidad de rango, fuera de rango y error se genera un valor
                double value = random.doubles(min, max).findFirst().getAsDouble();

            } else if (this.tipo.equalsIgnoreCase("PH")) {
                min = 6;
                max = 8;
                //TODO según la probabilidad de rango, fuera de rango y error se genera un valor
                double value = random.doubles(min, max).findFirst().getAsDouble();

            } else if (this.tipo.equalsIgnoreCase("oxigeno")) {
                min = 2;
                max = 11;
                //TODO según la probabilidad de rango, fuera de rango y error se genera un valor
                double value = random.doubles(min, max).findFirst().getAsDouble();

            } else {
                System.out.println("Tipo de sensor no valido");
            }
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    public ConfigFile getArchivoConfiguracion() {
        return archivoConfiguracion;
    }

    public Map<Timestamp, Double> getMedidas() {
        return medidas;
    }

    public void setMedidas(Map<Timestamp, Double> medidas) {
        this.medidas = medidas;
    }
}
