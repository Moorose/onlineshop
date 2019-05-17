package net.thumbtack.onlineshop.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String password;

    public User(int id, String firstName, String lastName, String patronymic, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.login = login.toUpperCase();
        this.password = password;
    }
}
