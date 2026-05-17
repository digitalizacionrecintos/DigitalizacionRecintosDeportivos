import { EstadoEvento } from '../enums/estado-evento.enum';

export interface Evento {
    id: number;
    titulo: string;
    fecha: string;
    fechaInicio?: string;
    fechaFin?: string;
    horaInicio?: string;
    horaFin?: string;
    ubicacion: string;
    direccion: string;
    color: string;
    estado: EstadoEvento | string;
    descripcion: string;
    imagen: string;
    encargado: string;
    cupoMaximo: number;
    inscritosActuales: number;
    inscripciones?: any[];
    cargandoInscritos?: boolean;
    categoria?: {
        id: number;
        nombre: string;
        descripcion: string;
    };
    cursoId?: number;
    cursoNombre?: string;
}
