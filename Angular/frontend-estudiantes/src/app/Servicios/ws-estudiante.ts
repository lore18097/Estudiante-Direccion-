import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Direccion {
    id?: number;
    calle: string;
    colonia: string;
    ciudad: string;
}

export interface Estudiante {
    numeroControl: number;
    nombre: string;
    email: string;
    telefono: string;
    direccion?: Direccion;
}

export interface Respuesta {
    mensaje: string;
    success: boolean;
    obj: Estudiante[];
}

@Injectable({
    providedIn: 'root'
})
export class WSEstudiante {
    
    private apiUrl = 'http://localhost:8000/Estudiante/Listar';

    constructor(private http: HttpClient) { }

    listar(): Observable<Respuesta> {
        return this.http.get<Respuesta>(this.apiUrl);
    }
}