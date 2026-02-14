package com.milospavlovic4046.cryptotracker.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    // json-server: GET /users -> vraća listu
    suspend fun getUsers(): List<UserDto> {
        return client.get("$baseUrl/users").body()
    }

    // json-server: POST /users -> kreira user-a i vrati ga sa id
    suspend fun createUser(dto: UserDto): UserDto {
        return client.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()
    }

    // json-server: PUT /users/{id}
    suspend fun updateUser(remoteId: String, dto: UserDto): UserDto {
        return client.put("$baseUrl/users/$remoteId") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()
    }

    // json-server: DELETE /users/{id}
    suspend fun deleteUser(remoteId: String) {
        client.delete("$baseUrl/users/$remoteId")
    }
}