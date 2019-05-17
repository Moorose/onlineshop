package net.thumbtack.onlineshop.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Client extends User {

    private String phone;
    private String email;
    private String address;
    private Deposit deposit;

    public Client(int id, String firstName, String lastName, String patronymic, String login, String password, String phone, String email, String address, int money, int depositVersion) {
        super(id, firstName, lastName, patronymic, login, password);
        this.phone = phone.replaceAll("[+ -]", "");
        this.email = email;
        this.address = address;
        this.deposit = new Deposit(money, depositVersion);
    }

    public Client(int id, String firstName, String lastName, String patronymic, String login, String password, String phone, String email, String address, int money) {
        this(id, firstName, lastName, patronymic, login, password, phone, email, address, money, 0);
    }

    public int getMoney() {
        return deposit.getMoney();
    }

}
