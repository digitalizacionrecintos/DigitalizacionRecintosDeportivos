
export interface CrearEventoDTO {
    titulo: string;
    descripcion: string;
    horaInicio: string;
    horaFin: string;
    fechaInicio: string;
    categoriaId: number;
    cupoMaximo: number;
    publicoObjetivo: string;
    recintoId: number;
    encargadoId: number;
    imagen: string;
    imagenUrl?: string;
    cursoId?: number;
    maximoPorInscripcion?: number;
}

export interface EditarEventoDTO {
    descripcion: string;
    horaInicio: string;
    horaFin: string;
    fechaInicio: string;
    fechaFin: string;
    cupoMaximo: number;
    recintoId: number;
    encargadoId: number;
    publicoObjetivo: string;
    categoriaId: number;
    imagen: string;
    imagenUrl?: string;
    cursoId?: number;
    maximoPorInscripcion?: number;
}

export interface EventoDTO {
    idEvento: number;
    id?: number;
    titulo: string;
    descripcion: string;
    horaInicio: string;
    horaFin: string;
    fechaInicio: string;
    fechaFin?: string;
    cupoMaximo: number;
    publicoObjetivo: string;
    estado: 'EN_ESPERA' | 'DISPONIBLE' | 'TERMINADO' | 'TRANSCURRIENDO';
    categoriaId: number;
    recintoId: number;
    encargadoId: number;
    imagen: string;
    imagenUrl?: string;

    maximoPorInscripcion?: number;

    recinto?: RecintoDTO;
    encargado?: any;
    categoria?: CategoriaDTO;

    inscritos?: number;
    inscritosActuales?: number;

    cursoId?: number;
    cursoNombre?: string;
}


export interface CrearRecintoDTO {
    nombre: string;
    ubicacion: string;
    descripcion: string;
    capacidad: number;
    coordenadasGPS: string;
    imagen: string;
    imagenUrl?: string;
}

export interface EditarRecintoDTO {
    nombre: string;
    ubicacion: string;
    descripcion: string;
    capacidad: number;
    coordenadasGPS: string;
    imagen: string;
    imagenUrl?: string;
}

export interface RecintoDTO {
    idRecinto: number;
    id?: number;
    nombre: string;
    ubicacion: string;
    descripcion: string;
    capacidad: number;
    coordenadasGPS: string;
    imagen: string;
    imagenUrl?: string;
}


export interface CategoriaDTO {
    id: number;
    nombre: string;
    descripcion: string;
}


export interface EstadisticaResumenDTO {
    totalEventos: number;
    porcentajeAsistencia: number;
    tasaAusentismo: number;
    promedioEventosMensual: number;
}

export interface EstadisticaCategoriaDTO {
    nombre: string;
    cantidad: number;
}

export interface EstadisticaRecintoDTO {
    nombre: string;
    cantidad: number;
}

export interface DistribucionTemporalDTO {
    porMes: { [key: string]: number };
    porAnio: { [key: string]: number };
    porDia: { [key: string]: number };
}

export interface EstadisticaDTO {
    resumen: EstadisticaResumenDTO;
    categorias: EstadisticaCategoriaDTO[];
    recintos: EstadisticaRecintoDTO[];
    distribucionTemporal: DistribucionTemporalDTO;
}


export interface User {
    id?: number;
    email: string;
    nombre?: string;
    apellido?: string;
}


export interface LoginUsuarioDTO {
    correo: string;
    contrasena: string;
}

export interface RegistroUsuarioDTO {
    correo: string;
    contrasena: string;
    nombre: string;
    apellido: string;
    telefono: string;
}

export interface UsuarioDTO {
    id: number;
    correo: string;
    nombre: string;
    apellido: string;
    telefono: string;
    rol?: string;
}


export interface EncargadoDTO {
    idUsuario: number;
    nombre: string;
    apellido: string;
    correo: string;
    telefono: string;
    rol: string;
}


export interface UpdateEncargadoDTO {
    nombre?: string;
    apellido?: string;
    correo?: string;
    telefono?: string;
}


export interface ResponseUsuarioDTO {
    id: number;
    nombre: string;
    apellido: string;
    correo: string;
    rol: string;
}


export interface Usuario {
    idUsuario: number;
    nombre: string;
    apellido: string;
    correo: string;
    telefono: string;
    rol: string;
    fcmToken?: string;
}


export interface InscripcionDTO {
    idUsuario: number;
    idEvento: number;
}

export interface InscripcionDetalleDTO {
    id: number;
    usuario: UsuarioDTO;
    evento?: EventoDTO;
    fechaInscripcion: string;
}


export interface EstadisticasCursosDTO {
    resumen: ResumenCursos;
    cursosPopulares: CursoPopular[];
    ocupacion: OcupacionCursos;
    porCategoria: CategoriaCursos[];
    tendenciaMensual: TendenciaMensual[];
}

export interface ResumenCursos {
    totalCursos: number;
    totalInscritos: number;
    promedioInscritosPorCurso: number;
}

export interface CursoPopular {
    nombre: string;
    inscritos: number;
    cupoMaximo: number;
    porcentajeOcupacion: number;
    categoria: string;
}

export interface OcupacionCursos {
    llenos: number;
    altaOcupacion: number;
    bajaOcupacion: number;
}

export interface CategoriaCursos {
    categoria: string;
    inscritos: number;
    cursos: number;
}

export interface TendenciaMensual {
    mes: string;
    inscritos: number;
}