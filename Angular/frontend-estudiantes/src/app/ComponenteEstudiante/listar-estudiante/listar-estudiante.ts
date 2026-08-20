import { Component, OnInit } from '@angular/core';
import { Estudiante, WSEstudiante } from '../../Servicios/ws-estudiante';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-listar-estudiante',
  standalone: true,               
  imports: [CommonModule],
  templateUrl: './listar-estudiante.html',
  styleUrls: ['./listar-estudiante.css']
})
export class ListarEstudianteComponent implements OnInit {
  estudiantes: Estudiante[] = [];
  mensaje: string = 'Cargando estudiantes...';

  constructor(private estudianteService: WSEstudiante) {}

  ngOnInit(): void {
    this.cargarEstudiantes();
  }

  cargarEstudiantes(): void {
    this.estudianteService.listar().subscribe({
      next: (resp) => {
        console.log('Respuesta del backend:', resp);
        this.estudiantes = resp.obj || [];      
        this.mensaje = resp.mensaje || 'Listado de estudiantes';      
      },
      error: (err) => {
        console.error('Error al obtener los estudiantes:', err);
        this.mensaje = 'Error al cargar los estudiantes';
        this.estudiantes = [];
      }
    });
  }
}