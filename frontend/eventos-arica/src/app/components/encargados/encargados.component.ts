import { Component, OnInit, ApplicationRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EncargadosService } from '../../services/encargados.service';
import { Usuario, RegistroUsuarioDTO, UpdateEncargadoDTO } from '../../models/api.models';
import { finalize, timeout, tap } from 'rxjs';

interface Encargado {
  idUsuario: number;
  nombre: string;
  apellido: string;
  correo: string;
  telefono: string;
  rol: string;
  contrasena?: string;
}

@Component({
  selector: 'app-encargados',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl:'./encargados.component.html',
  styles: []
})
export class EncargadosComponent implements OnInit {
  busqueda = '';
  mostrarModal = false;
  mostrarModalEliminar = false;
  modoEdicion = false;
  mostrarPassword = false;
  encargadoAEliminar: Encargado | null = null;


  cargando = false;
  mensajeError = '';
  mensajeExito = '';

  encargadoForm: Encargado = {
    idUsuario: 0,
    nombre: '',
    apellido: '',
    correo: '',
    telefono: '',
    rol: 'ROLE_ENCARGADO',
    contrasena: ''
  };

  encargados: Encargado[] = [];

  constructor(
    private encargadosService: EncargadosService,
    private appRef: ApplicationRef
  ) { }

  ngOnInit(): void {
    this.cargarEncargados();
  }

  cargarEncargados(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.encargadosService.getEncargados()
      .pipe(
        timeout(30000),
        finalize(() => {
          setTimeout(() => {
            this.cargando = false;
          }, 0);
        })
      )
      .subscribe({
        next: (usuarios: Usuario[]) => {
          setTimeout(() => {
            this.encargados = usuarios.map(u => ({
              idUsuario: u.idUsuario,
              nombre: u.nombre,
              apellido: u.apellido,
              correo: u.correo,
              telefono: u.telefono,
              rol: u.rol
            }));
            console.log(' Encargados cargados:', this.encargados);
          }, 0);
        },
        error: (error) => {
          console.error(' Error cargando encargados:', error);
          if (error.name === 'TimeoutError') {
            this.mensajeError = 'La carga tardó demasiado. Por favor recarga la página.';
          } else {
            this.mensajeError = 'Error al cargar los encargados. Por favor intenta de nuevo.';
          }
        }
      });
  }

  get encargadosFiltrados(): Encargado[] {
    let filtered = this.encargados;


    if (this.busqueda.trim()) {
      const search = this.busqueda.toLowerCase();
      filtered = filtered.filter(e =>
        e.nombre.toLowerCase().includes(search) ||
        e.apellido.toLowerCase().includes(search) ||
        e.correo.toLowerCase().includes(search)
      );
    }

    return filtered;
  }

  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.encargadoForm = {
      idUsuario: 0,
      nombre: '',
      apellido: '',
      correo: '',
      telefono: '',
      rol: 'ROLE_ENCARGADO',
      contrasena: ''
    };
    this.mensajeError = '';
    this.mensajeExito = '';
    this.mostrarModal = true;
  }

  abrirModalEditar(encargado: Encargado): void {
    this.modoEdicion = true;
    this.encargadoForm = {
      ...encargado,
      contrasena: undefined
    };
    this.mensajeError = '';
    this.mensajeExito = '';
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    console.log(' cerrarModal() llamado');
    this.mostrarModal = false;
    this.appRef.tick();
    console.log(' Detección de cambios forzada');
  }

  guardarEncargado(): void {

    if (!this.encargadoForm.nombre || !this.encargadoForm.apellido ||
      !this.encargadoForm.correo || !this.encargadoForm.telefono) {
      this.mensajeError = 'Por favor completa todos los campos obligatorios';
      return;
    }


    if (!this.modoEdicion && !this.encargadoForm.contrasena) {
      this.mensajeError = 'La contraseña es obligatoria';
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    if (this.modoEdicion) {

      const updateData: UpdateEncargadoDTO = {
        nombre: this.encargadoForm.nombre,
        apellido: this.encargadoForm.apellido,
        correo: this.encargadoForm.correo,
        telefono: this.encargadoForm.telefono
      };

      this.encargadosService.updateEncargado(this.encargadoForm.idUsuario, updateData)
        .pipe(
          timeout(30000),
          finalize(() => {
            this.cargando = false;
            this.appRef.tick();
          })
        )
        .subscribe({
          next: (response) => {
            console.log(' Encargado actualizado:', response);
            this.mensajeExito = 'Encargado actualizado exitosamente';
            this.cargarEncargados();
            this.cerrarModal();


            setTimeout(() => this.mensajeExito = '', 3000);
          },
          error: (error) => {
            console.error(' Error actualizando encargado:', error);
            if (error.name === 'TimeoutError') {
              this.mensajeError = 'La operación tardó demasiado. Por favor intenta de nuevo.';
            } else {
              this.mensajeError = error.error?.message || 'Error al actualizar el encargado';
            }
          }
        });
    } else {

      const createData: RegistroUsuarioDTO = {
        nombre: this.encargadoForm.nombre,
        apellido: this.encargadoForm.apellido,
        correo: this.encargadoForm.correo,
        telefono: this.encargadoForm.telefono,
        contrasena: this.encargadoForm.contrasena!
      };

      this.encargadosService.createEncargado(createData)
        .pipe(
          timeout(30000),
          finalize(() => {
            this.cargando = false;
            this.appRef.tick();
          })
        )
        .subscribe({
          next: (response) => {
            console.log(' Encargado creado:', response);
            this.mensajeExito = 'Encargado creado exitosamente';
            this.cargarEncargados();
            this.cerrarModal();
            setTimeout(() => this.mensajeExito = '', 3000);
          },
          error: (error) => {
            console.error(' Error creando encargado:', error);
            if (error.name === 'TimeoutError') {
              this.mensajeError = 'La operación tardó demasiado. Por favor intenta de nuevo.';
            } else {
              this.mensajeError = error.error?.message || 'Error al crear el encargado';
            }
          }
        });
    }
  }

  confirmarEliminar(encargado: Encargado): void {
    this.encargadoAEliminar = encargado;
    this.mostrarModalEliminar = true;
  }

  eliminarEncargado(): void {
    if (!this.encargadoAEliminar) {
      console.warn(' No hay encargado seleccionado para eliminar');
      return;
    }

    console.log(' Eliminando encargado ID:', this.encargadoAEliminar.idUsuario);

    this.cargando = true;
    this.mensajeError = '';
    this.encargadosService.deleteEncargado(this.encargadoAEliminar.idUsuario)
      .pipe(
        timeout(30000),
        finalize(() => {
          console.log(' Finalizando - desactivando loading y cerrando modal');
          this.cargando = false;
          this.cancelarEliminar();
          this.appRef.tick();


          setTimeout(() => {
            console.log(' Recargando lista de encargados...');
            this.cargarEncargados();
          }, 500);
        })
      )
      .subscribe({
        next: (response) => {
          console.log(' Eliminado exitosamente:', response);
          this.mensajeExito = 'Encargado eliminado exitosamente';

          setTimeout(() => this.mensajeExito = '', 3000);
        },
        error: (error) => {
          console.error(' ERROR - Detalles completos:');
          console.error('  - Name:', error.name);
          console.error('  - Status:', error.status);
          console.error('  - StatusText:', error.statusText);
          console.error('  - Message:', error.message);
          console.error('  - Error body:', error.error);
          console.error('  - Full error:', error);

          let mensajeDetallado = 'Error al eliminar el encargado';

          if (error.name === 'TimeoutError') {
            mensajeDetallado = 'La operación tardó demasiado (30s). Verifica tu conexión.';
          } else if (error.status === 0) {
            mensajeDetallado = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
          } else if (error.status === 404) {
            mensajeDetallado = 'Encargado no encontrado (404)';
          } else if (error.status === 403) {
            mensajeDetallado = 'No tienes permisos para eliminar este encargado (403)';
          } else if (error.status === 409) {
            mensajeDetallado = error.error?.message || 'No se puede eliminar (conflicto - puede estar asignado a eventos)';
          } else if (error.status === 400) {
            mensajeDetallado = error.error?.message || 'Solicitud inválida (400)';
          } else if (error.status >= 500) {
            mensajeDetallado = `Error del servidor (${error.status}): ${error.error?.message || error.statusText}`;
          } else if (error.error?.message) {
            mensajeDetallado = error.error.message;
          }

          this.mensajeError = mensajeDetallado;

          setTimeout(() => {
            this.mensajeError = '';
            this.cancelarEliminar();
          }, 8000);
        },
        complete: () => {
          console.log(' COMPLETE - Observable completado');
        }
      });
  }

  cancelarEliminar(): void {
    console.log(' cancelarEliminar() llamado - cerrando modal');
    this.mostrarModalEliminar = false;
    this.encargadoAEliminar = null;
    this.appRef.tick();
    console.log(' Detección de cambios forzada');
  }
}