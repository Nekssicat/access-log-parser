import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int x = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int y = new Scanner(System.in).nextInt();
        System.out.println("Сумма чисел = "+(x+y));
        System.out.println("Разность чисел (первое минус второе) = "+(x-y));
        System.out.println("Произведение чисел = "+(x*y));
        double del = (double) x/y;
        System.out.println("Частное чисел (первое делить на второе) = "+del);
    }
}
