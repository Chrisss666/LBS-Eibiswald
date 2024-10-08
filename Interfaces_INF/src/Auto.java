public class Auto implements Fahrzeug {
    private int aktuelleGeschwindigkeit;

    public Auto() {
        aktuelleGeschwindigkeit = 0;
    }

    @Override
    public void starten() {
        aktuelleGeschwindigkeit = 10;
        System.out.println("Das Auto startet und fährt los.");
    }

    @Override
    public void anhalten() {
        aktuelleGeschwindigkeit = 0;
        System.out.println("Das Auto hält an.");
    }

    @Override
    public int geschwindigkeit() {
        return aktuelleGeschwindigkeit;
    }

    public static void main(String[] args) {
        Auto meinAuto = new Auto();
        meinAuto.starten();
        System.out.println("Aktuelle Geschwindigkeit: " + meinAuto.geschwindigkeit() + " km/h");
        meinAuto.anhalten();
        System.out.println("Aktuelle Geschwindigkeit nach dem Anhalten: " + meinAuto.geschwindigkeit() + " km/h");
    }
}