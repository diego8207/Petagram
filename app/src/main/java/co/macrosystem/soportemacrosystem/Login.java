package co.macrosystem.soportemacrosystem;

/**
 * Created by Diego Velez on 15/12/2016.
 */

public class Login {
    private String usuario;
    private String clave;
    private String nomApe;

    public Login() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNomApe() {
        return nomApe;
    }

    public void setNomApe(String nomApe) {
        this.nomApe = nomApe;
    }

    @Override
    public String toString() {
        return nomApe + " | " + usuario;
    }
}
