package net.thumbtack.onlineshop.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Admin extends User{

    private String position;

    public Admin(int id, String firstName, String lastName, String patronymic, String login, String password, String position) {
        super(id, firstName, lastName, patronymic, login, password);
        this.position = position;
    }
}
