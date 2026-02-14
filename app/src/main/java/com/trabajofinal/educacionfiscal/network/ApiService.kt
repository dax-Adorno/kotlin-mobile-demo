package com.trabajofinal.educacionfiscal.network

import com.trabajofinal.educacionfiscal.network.models.Categoria
import com.trabajofinal.educacionfiscal.network.models.Ejemplo
import com.trabajofinal.educacionfiscal.network.models.Glosario
import com.trabajofinal.educacionfiscal.network.models.Impuestos
import com.trabajofinal.educacionfiscal.network.models.Vencimiento
import com.trabajofinal.educacionfiscal.network.models.LoginRequest
import com.trabajofinal.educacionfiscal.network.models.LoginResponse
import com.trabajofinal.educacionfiscal.network.models.RegistrarResponse
import com.trabajofinal.educacionfiscal.network.models.RegistrarRequest
import com.trabajofinal.educacionfiscal.network.models.Registros
import com.trabajofinal.educacionfiscal.network.models.RegistrosRequest
import com.trabajofinal.educacionfiscal.network.models.RegistrosResponse
import com.trabajofinal.educacionfiscal.network.models.Tutorial

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun registrarUsuario(
        @Body datos: RegistrarRequest
    ): Response<RegistrarResponse>

    @POST("auth/login")
    suspend fun login(
        @Body datos: LoginRequest
    ): Response<LoginResponse>

    @GET("vencimientos")
    suspend fun obtenerVencimientos(
        @Header("Authorization") token: String
    ): Response<List<Vencimiento>>

    @POST("registros")
    suspend fun guardarRegistro(
        @Header("Authorization") token: String,
        @Body datos: RegistrosRequest
    ): Response<Registros>
    @GET("registros/{id}")
    suspend fun obtenerRegistro(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Registros>

    @GET("registros")
    suspend fun obtenerRegistros(
        @Header("Authorization") token: String
    ): Response<List<Registros>>
    @GET(value="categorias")
    suspend fun obtenerCategorias(
        @Header(value="Authorization")token: String
    ): Response<List<Categoria>>
    @GET(value="ejemplos")
    suspend fun obtenerEjemplos(
        @Header(value="Authorization")token: String
    ): Response<List<Ejemplo>>
    @GET(value="ejemplos/{id}")
    suspend fun obtenerEjemplo(
        @Header(value="Authorization")token: String,
        @Path("id") id: Int
    ): Response<Ejemplo>
    @GET(value="glosarios")
    suspend fun obtenerGlosarios(
        @Header(value="Authorization")token: String
    ): Response<List<Glosario>>
    @GET(value="impuestos")
    suspend fun obtenerImpuestos(
        @Header(value="Authorization")token: String
    ): Response<List<Impuestos>>
    @GET(value="tutoriales")
    suspend fun obtenerTutoriales(

    ): Response<List<Tutorial>>
}
