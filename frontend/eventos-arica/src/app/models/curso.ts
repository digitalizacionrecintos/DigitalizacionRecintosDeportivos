import { CategoriaDTO } from './api.models';

export interface Curso {
    idCurso?: number;
    nombre: string;
    descripcion: string;
    fechaInicio?: string;
    fechaFin?: string;
    horaInicio?: string;
    horaFin?: string;
    dias?: string;
    cupo?: number;
    recinto?: any;
    encargado?: any;
    categoria?: CategoriaDTO;
    maximoPorInscripcion?: number;
    horarios?: { dia: string; horaInicio: string; horaFin: string }[];
    sesiones?: any[];
    cantidadSesiones?: number;
    estado?: string;
}
