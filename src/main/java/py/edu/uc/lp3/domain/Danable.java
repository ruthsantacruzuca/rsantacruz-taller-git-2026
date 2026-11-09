package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio (POO-07): todo lo que puede recibir daño y contar su estado.
 * Lo implementan familias distintas (Jugador y Mob) y un endpoint de lectura habla
 * con el tipo de la interfaz, no con la clase concreta.
 */
public interface Danable {

    int getVida();

    void recibirDano(int dano);

    String estado();
}
