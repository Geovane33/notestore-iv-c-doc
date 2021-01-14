package br.senac.sp.pi.utils;

public enum PerfilType {
    Administrador("Administrador"), Estoquista("Estoquista"), Cliente("Cliente"), Gerente("Gerente");

    private final String perfil;

    PerfilType(String perfil) {
        this.perfil = perfil;
    }

    public String getPerfil() {
        return this.perfil;
    }

}
