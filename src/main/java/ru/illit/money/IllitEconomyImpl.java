package ru.illit.money;

import ru.illit.money.api.IllitEconomy;

import java.io.IOException;
import java.util.UUID;

public final class IllitEconomyImpl implements IllitEconomy {
    private final MoneyStore store;
    private final String currencyPlural;

    public IllitEconomyImpl(MoneyStore store, String currencyPlural) {
        this.store = store;
        this.currencyPlural = currencyPlural;
    }

    @Override public String currencyNamePlural() { return currencyPlural; }
    @Override public boolean has(UUID playerId, double amount) { return getBalance(playerId) >= amount; }
    @Override public double getBalance(UUID playerId) { return store.get(playerId); }

    @Override public void setBalance(UUID playerId, double amount) { store.set(playerId, amount); flush(); }
    @Override public void add(UUID playerId, double amount) { if (amount>0){ store.set(playerId, store.get(playerId)+amount); flush(); } }

    @Override public boolean take(UUID playerId, double amount) {
        if (amount<=0) return true;
        double cur = store.get(playerId);
        if (cur < amount) return false;
        store.set(playerId, cur-amount);
        flush();
        return true;
    }

    @Override public String format(double amount) { return U.fmt(amount); }

    private void flush() { try { store.save(); } catch (IOException ignored) {} }
}
