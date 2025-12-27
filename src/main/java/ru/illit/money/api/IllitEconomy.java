package ru.illit.money.api;

import java.util.UUID;

public interface IllitEconomy {
    String currencyNamePlural();
    boolean has(UUID playerId, double amount);
    double getBalance(UUID playerId);
    void setBalance(UUID playerId, double amount);
    void add(UUID playerId, double amount);
    boolean take(UUID playerId, double amount);
    String format(double amount);
}
