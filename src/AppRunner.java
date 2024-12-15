import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private final CoinAcceptor coinAcceptor;

    private static boolean isExit = false;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        coinAcceptor = new CoinAcceptor(100);
    }

    public static void run() {
        AppRunner app = new AppRunner();
        while (!isExit) {
            app.startSimulation();
        }
    }

    private void startSimulation() {
        print("В автомате доступны:");
        showProducts(products);

        print("Монет на сумму: " + coinAcceptor.getAmount());

        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);

    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (coinAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void chooseAction(UniversalArray<Product> products) {
        print(" a - Пополнить баланс");
        print(" p - Пополнить баланс с карты");
        showActions(products);
        print(" h - Выйти");
        String action = fromConsole().substring(0, 1);

        if ("p".equalsIgnoreCase(action)) {
            TopUpFromCard();
        }

        if ("a".equalsIgnoreCase(action)) {
            coinAcceptor.setAmount(coinAcceptor.getAmount() + 10);
            print("Вы пополнили баланс на 10");
            return;
        }
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                    coinAcceptor.setAmount(coinAcceptor.getAmount() - products.get(i).getPrice());
                    print("Вы купили " + products.get(i).getName());
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            if ("h".equalsIgnoreCase(action)) {
                isExit = true;
            } else {
                print("Недопустимая буква. Попрбуйте еще раз.");
                chooseAction(products);
            }
        }
    }

    public void TopUpFromCard() {
        BankCard bankCard = new BankCard();

        Scanner sc = new Scanner(System.in);

        System.out.print("Введите 14 значный номер карты: ");
        bankCard.setCardNumber(sc.nextLine());

        long number = String.valueOf(bankCard.getCardNumber()).length();

        if (number == 14) {
            System.out.print("Введите 4 значный пороль:");
            bankCard.setOnePassword(sc.nextInt());

            int password = String.valueOf(bankCard.getOnePassword()).length();

            if (password == 4) {
                System.out.print("Все верно, напишите число для пополнения: ");
                bankCard.setReplenishment(sc.nextInt());

                if (bankCard.getReplenishment() < 0) {
                    System.out.println("Сумма не должна быть меньше 0");
                } else if (bankCard.getReplenishment() > 10000) {
                    System.out.println("Сумма не должна превышать 10000");
                } else {
                    coinAcceptor.setAmount(coinAcceptor.getAmount() + bankCard.getReplenishment());

                    System.out.printf("Вы успешно пополнили на %s%n", bankCard.getReplenishment());
                }
            } else {
                System.out.println("Пороль должен быть 4 значным!");
            }
        } else {
            System.out.println("Карта номера должна быть 14 значной!");
        }
    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
