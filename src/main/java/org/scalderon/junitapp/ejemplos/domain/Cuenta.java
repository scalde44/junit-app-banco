package org.scalderon.junitapp.ejemplos.domain;

import org.scalderon.junitapp.ejemplos.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo, Banco banco) {
        this.persona = persona;
        this.saldo = saldo;
        this.banco = banco;
    }

    public String getPersona() {
        return persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto) {
        if (this.saldo.compareTo(monto) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = this.saldo.subtract(monto);
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

    public String getNombreBanco() {
        return this.banco.getNombre();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return persona.equals(cuenta.persona) && saldo.equals(cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persona, saldo);
    }
}
