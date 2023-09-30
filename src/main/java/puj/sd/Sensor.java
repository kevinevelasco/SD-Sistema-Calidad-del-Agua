package puj.sd;

import java.sql.Timestamp;
import java.util.Map;

public class Sensor {
    String tipo;
    double tiempo;
    ConfigFile archivoConfiguracion;
    Map<Timestamp, Double> medidas; //se guardan las medidas con su timestamp

    public boolean generarMedidas(){
        return true;
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
