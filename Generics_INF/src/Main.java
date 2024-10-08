public class Main {
    public static void main(String[] args) {
        Integer[] intArray = {1, 3, 5, 7};
        auslesen(intArray);

        Double[] doubleArray = {2.1, 2.2, 2.4, 2.5};
        auslesen(doubleArray);

        Character[] charArray = {'a', 'b', '.', '_'};
        auslesen(charArray);
    }

    public static <T> void auslesen(T[] typeArray) {
        for (T t : typeArray) {
            System.out.println(t);
        }
    }
}