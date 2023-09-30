package puj.sd;

public class Main {
    public static void main(String[] args) {
        ConfigFile cf = new ConfigFile("src\\main\\resources\\config.txt");
        cf.leerArchivo();
        Sensor sensor = new Sensor("temperatura", 1, cf);
        for(int i = 0; i < 10; i++){
            sensor.generarMedidas();
        }
    }
}