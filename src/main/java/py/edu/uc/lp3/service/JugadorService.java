package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.JugadorRepository;

@Service
public class JugadorService {

    private final JugadorRepository repositorio;

    public JugadorService(JugadorRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public Jugador crear(Jugador jugador) {
        ValidadorMinecraft.validarJugador(jugador);
        return repositorio.save(jugador);
    }

    @Transactional(readOnly = true)
    public List<Jugador> listar() {
        return repositorio.findAll();
    }
}
