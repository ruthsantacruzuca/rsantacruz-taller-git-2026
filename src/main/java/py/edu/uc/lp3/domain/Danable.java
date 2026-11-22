package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio: todo lo que puede contar su vida y su estado.
 * Lo implementan familias distintas (Jugador y Mob) y un endpoint de lectura
 * habla con el tipo de la interfaz, no con la clase concreta.
 */
public interface Danable {

    int getVida();

    String estado();
}
