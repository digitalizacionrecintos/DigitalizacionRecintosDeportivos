package com.recintos.municipalidad.config;

import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioRecinto;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(RepositorioUsuario repositorioUsuario,
            RepositorioRecinto repositorioRecinto,
            RepositorioEvento repositorioEvento,
            com.recintos.municipalidad.repository.RepositorioCategoria repositorioCategoria,
            com.recintos.municipalidad.repository.RepositorioInscripcion repositorioInscripcion,
            PasswordEncoder passwordEncoder) {
        return args -> {

            if (repositorioUsuario.count() == 0) {
                crearUsuarios(repositorioUsuario, passwordEncoder);
            }

            if (repositorioRecinto.count() == 0) {
                crearRecintos(repositorioRecinto);
            }

            if (repositorioCategoria.count() == 0) {
                crearCategorias(repositorioCategoria);
            }

            if (repositorioEvento.count() == 0) {
                crearEventos(repositorioEvento, repositorioUsuario, repositorioRecinto, repositorioCategoria);
            }

            if (repositorioInscripcion.count() == 0) {
                crearInscripciones(repositorioInscripcion, repositorioUsuario, repositorioEvento);
            }
        };
    }

    private void crearUsuarios(RepositorioUsuario repo, PasswordEncoder encoder) {

        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellido("Sistema");
        admin.setCorreo("admin@municipalidad.com");
        admin.setPassword(encoder.encode("1234"));
        admin.setRol("ROLE_ADMIN");
        admin.setTelefono("+56911111111");
        repo.save(admin);

        Usuario encargado = new Usuario();
        encargado.setNombre("Juan");
        encargado.setApellido("Perez");
        encargado.setCorreo("encargado@municipalidad.com");
        encargado.setPassword(encoder.encode("1234"));
        encargado.setRol("ROLE_ENCARGADO");
        encargado.setTelefono("+56922222222");
        repo.save(encargado);

        Usuario cliente = new Usuario();
        cliente.setNombre("Maria");
        cliente.setApellido("Gonzalez");
        cliente.setCorreo("cliente@gmail.com");
        cliente.setPassword(encoder.encode("1234"));
        cliente.setRol("ROLE_CLIENTE");
        cliente.setTelefono("+56933333333");
        repo.save(cliente);

        Usuario cliente2 = new Usuario();
        cliente2.setNombre("Pedro");
        cliente2.setApellido("Soto");
        cliente2.setCorreo("pedro@gmail.com");
        cliente2.setPassword(encoder.encode("1234"));
        cliente2.setRol("ROLE_CLIENTE");
        cliente2.setTelefono("+56944444444");
        repo.save(cliente2);

        System.out.println("Usuarios iniciales creados.");
    }

    private void crearRecintos(RepositorioRecinto repo) {

        Recinto estadioCarlosDittborn = new Recinto();
        estadioCarlosDittborn.setNombre("Estadio Carlos Dittborn");
        estadioCarlosDittborn.setUbicacion("Avenida 18 de Septiembre #2000, Arica");
        estadioCarlosDittborn.setDescripcion(
                "Estadio principal de la ciudad, sede de eventos deportivos, comunitarios y culturales.");
        estadioCarlosDittborn.setCapacidad(500);
        estadioCarlosDittborn.setEstado("ACTIVO");
        estadioCarlosDittborn.setCoordenadasGPS("-18.4746, -70.3191");
        repo.save(estadioCarlosDittborn);

        Recinto piscinaOlimpica = new Recinto();
        piscinaOlimpica.setNombre("Piscina Olímpica Municipal");
        piscinaOlimpica.setUbicacion("Avenida Comandante San Martín s/n, Arica");
        piscinaOlimpica.setDescripcion("Piscina olímpica pública, utilizada para natación y competencias.");
        piscinaOlimpica.setCapacidad(100);
        piscinaOlimpica.setEstado("ACTIVO");
        piscinaOlimpica.setCoordenadasGPS("-18.4722, -70.3156");
        repo.save(piscinaOlimpica);

        Recinto gimnasioEplicentro = new Recinto();
        gimnasioEplicentro.setNombre("Gimnasio Eplicentro Futsal");
        gimnasioEplicentro.setUbicacion("Avenida Alejandro Azolas 2200, Arica");
        gimnasioEplicentro.setDescripcion(
                "Espacio para actividades deportivas y recreativas, incluyendo talleres y eventos comunitarios.");
        gimnasioEplicentro.setCapacidad(80);
        gimnasioEplicentro.setEstado("ACTIVO");
        gimnasioEplicentro.setCoordenadasGPS("-18.4689, -70.3201");
        repo.save(gimnasioEplicentro);

        Recinto zonaDeportivaCentenario = new Recinto();
        zonaDeportivaCentenario.setNombre("Zona deportiva Parque Centenario");
        zonaDeportivaCentenario.setUbicacion("Avenida Comandante San Martín s/n, Arica");
        zonaDeportivaCentenario.setDescripcion("Área con instalaciones deportivas, áreas de trote y un anfiteatro.");
        zonaDeportivaCentenario.setCapacidad(150);
        zonaDeportivaCentenario.setEstado("ACTIVO");
        zonaDeportivaCentenario.setCoordenadasGPS("-18.4735, -70.3178");
        repo.save(zonaDeportivaCentenario);

        Recinto paseoDeportivoMachas = new Recinto();
        paseoDeportivoMachas.setNombre("Paseo Deportivo Las Machas");
        paseoDeportivoMachas.setUbicacion("Avenida Las Dunas, sector Playa Las Machas, Arica");
        paseoDeportivoMachas.setDescripcion("Espacio costero para trotar y practicar deportes al aire libre.");
        paseoDeportivoMachas.setCapacidad(200);
        paseoDeportivoMachas.setEstado("ACTIVO");
        paseoDeportivoMachas.setCoordenadasGPS("-18.4556, -70.3289");
        repo.save(paseoDeportivoMachas);

        Recinto polideportivoPlayaArena = new Recinto();
        polideportivoPlayaArena.setNombre("Polideportivo Playa Arena Las Machas");
        polideportivoPlayaArena.setUbicacion("Sector Playa Las Machas, Arica");
        polideportivoPlayaArena.setDescripcion(
                "Recinto único en Arica que permite realizar y disfrutar de actividades deportivas en la playa.");
        polideportivoPlayaArena.setCapacidad(120);
        polideportivoPlayaArena.setEstado("ACTIVO");
        polideportivoPlayaArena.setCoordenadasGPS("-18.4545, -70.3295");
        repo.save(polideportivoPlayaArena);

        Recinto fortinSotomayor = new Recinto();
        fortinSotomayor.setNombre("Fortín Sotomayor");
        fortinSotomayor.setUbicacion("Rafael Sotomayor 698, Arica");
        fortinSotomayor.setDescripcion(
                "Histórico recinto deportivo, cuenta con modernas butacas y ha sido sede de diversas actividades deportivas.");
        fortinSotomayor.setCapacidad(100);
        fortinSotomayor.setEstado("ACTIVO");
        fortinSotomayor.setCoordenadasGPS("-18.4712, -70.3134");
        repo.save(fortinSotomayor);

        Recinto estadioSectorNorte = new Recinto();
        estadioSectorNorte.setNombre("Estadio del Sector Norte (Punta Norte)");
        estadioSectorNorte.setUbicacion("Avenida Capitán Ávalos s/n, Arica");
        estadioSectorNorte.setDescripcion(
                "Cuenta con una cancha de pasto sintético, gradas, camarines, oficinas y áreas recreativas.");
        estadioSectorNorte.setCapacidad(150);
        estadioSectorNorte.setEstado("ACTIVO");
        estadioSectorNorte.setCoordenadasGPS("-18.4523, -70.3267");
        repo.save(estadioSectorNorte);

        System.out.println("8 Recintos de Arica creados.");
    }

    private void crearCategorias(com.recintos.municipalidad.repository.RepositorioCategoria repo) {
        String[][] cats = {
                { "Fútbol", "Eventos y actividades relacionadas con fútbol" },
                { "Básquetbol", "Eventos y actividades de básquetbol" },
                { "Natación", "Clases y competencias de natación" },
                { "Yoga", "Sesiones de yoga y meditación" },
                { "Voleibol", "Torneos y prácticas de voleibol" },
                { "Atletismo", "Carreras, maratones y actividades atléticas" }
        };

        for (String[] c : cats) {
            com.recintos.municipalidad.model.Categoria cat = new com.recintos.municipalidad.model.Categoria();
            cat.setNombre(c[0]);
            cat.setDescripcion(c[1]);
            repo.save(cat);
        }
        System.out.println("6 Categorías deportivas creadas.");
    }

    private void crearEventos(RepositorioEvento repoEvento, RepositorioUsuario repoUsuario,
            RepositorioRecinto repoRecinto, com.recintos.municipalidad.repository.RepositorioCategoria repoCategoria) {
        Usuario encargado = repoUsuario.findByCorreo("encargado@municipalidad.com").orElse(null);
        if (encargado == null)
            return;

        List<Recinto> recintos = repoRecinto.findAll();
        List<com.recintos.municipalidad.model.Categoria> categorias = repoCategoria.findAll();
        if (recintos.isEmpty() || categorias.isEmpty())
            return;

        int[] years = { 2021, 2022, 2023, 2024 };

        for (int year : years) {

            for (int i = 0; i < 25; i++) {
                Recinto recinto = recintos.get((int) (Math.random() * recintos.size()));
                com.recintos.municipalidad.model.Categoria cat = categorias
                        .get((int) (Math.random() * categorias.size()));

                int month = (int) (Math.random() * 12) + 1;
                int day = (int) (Math.random() * 28) + 1;
                LocalDate fecha = LocalDate.of(year, month, day);

                String estado = "DISPONIBLE";
                if (fecha.isBefore(LocalDate.now())) {
                    estado = "TERMINADO";
                }

                crearUnEvento(repoEvento, "Evento " + cat.getNombre() + " " + i + "-" + year,
                        "Evento generado automaticamente", recinto, cat, encargado, fecha, estado);
            }
        }

        System.out.println("100 Eventos creados (25 por año desde 2021 hasta 2024).");
    }

    private void crearUnEvento(RepositorioEvento repo, String titulo, String desc, Recinto recinto,
            com.recintos.municipalidad.model.Categoria cat, Usuario encargado, LocalDate fecha, String estado) {
        Evento e = new Evento();
        e.setTitulo(titulo);
        e.setDescripcion(desc);
        e.setRecinto(recinto);
        e.setCategoria(cat);
        e.setEncargado(encargado);
        e.setFechaInicio(fecha);
        e.setHoraInicio(fecha.atTime(10, 0));
        e.setHoraFin(fecha.atTime(12, 0));
        e.setCupoMaximo(50 + (int) (Math.random() * 100));
        e.setEstado(estado);
        e.setMaximoPorInscripcion(5);
        repo.save(e);
    }

    private void crearInscripciones(com.recintos.municipalidad.repository.RepositorioInscripcion repoInscripcion,
            RepositorioUsuario repoUsuario, RepositorioEvento repoEvento) {
        Usuario cliente1 = repoUsuario.findByCorreo("cliente@gmail.com").orElse(null);
        Usuario cliente2 = repoUsuario.findByCorreo("pedro@gmail.com").orElse(null);
        List<Evento> eventos = repoEvento.findAll();

        if (cliente1 == null || eventos.isEmpty())
            return;

        for (Evento evento : eventos) {

            if (Math.random() > 0.5) {
                crearInscripcion(repoInscripcion, cliente1, evento);
            }
            if (cliente2 != null && Math.random() > 0.7) {
                crearInscripcion(repoInscripcion, cliente2, evento);
            }
        }
        System.out.println("Inscripciones masivas creadas.");
    }

    private void crearInscripcion(com.recintos.municipalidad.repository.RepositorioInscripcion repo, Usuario u,
            Evento e) {
        com.recintos.municipalidad.model.Inscripcion i = new com.recintos.municipalidad.model.Inscripcion();
        i.setNombre(u.getNombre());
        i.setApellido(u.getApellido());
        i.setEdad(25);
        i.setTutor(u);
        i.setEvento(e);
        i.setFechaHoraRegistro(LocalDateTime.now());

        String estadoAsistencia = "PENDIENTE";
        if ("TERMINADO".equals(e.getEstado())) {
            estadoAsistencia = Math.random() > 0.2 ? "ASISTIO" : "AUSENTE";
        }
        i.setEstadoAsistencia(estadoAsistencia);
        repo.save(i);
    }
}
