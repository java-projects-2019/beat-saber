import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //Задаёт наше число

        int a = 1; // Начальное значение диапазона - "от"
        int b = 6; // Конечное значение диапазона - "до"
        boolean end = false;

        int first = a + (int) (Math.random() * b); // Генерация 1-го число
        int second = a + (int) (Math.random() * b); // Генерация 2-го число
        while (second == first)
            second = a + (int) (Math.random() * b);
        int third = a + (int) (Math.random() * b); // Генерация 3-го числа
        while (third == second || third == first)
            third = a + (int) (Math.random() * b);
        int forth = a + (int) (Math.random() * b);
        while (forth == second || forth == first || forth == third)
            forth = a + (int) (Math.random() * b);

        // Выводит на экран быков и коров
        Scanner in = new Scanner(System.in);
        int[] p = new int[4];
        boolean[] h = new boolean[4];
        int k = 0;
        int n = 0;
        while (end == false) {
            for (int i = 0; i < p.length; i++)
                p[i] = in.nextInt();

            //Считаем быков

            if (p[0] == first) {
                k++;
                h[0] = true;
            }
            if (p[1] == second) {
                k++;
                h[1] = true;
            }
            if (p[2] == third){
                k++;
                h[2] = true;
            }
            if (p[3] == forth) {
                k++;
                h[3] = true;
            }

            //Теперь подсчитаем коров

            for (int i = 0; i < h.length; i++){
                if (h[i] != true)
                    if (p[i] == first || p[i] == second || p[i] == third || p[i] == forth)
                        n++;
            }


            //И выводим на экран
            System.out.printf("Bulls: %d  Cows: %d", k, n);

            //Вывожу число просто для удобства проверки
            System.out.println();
            System.out.print(first);
            System.out.print(second);
            System.out.print(third);
            System.out.println(forth);
            // Проверка окончания программы
            if (k == 4)
                end = true;

            //Обнуление счётчика быков и коров
            k = 0;
            n = 0;
        }
        System.out.print("Congratulations! You won!");
    }
}
