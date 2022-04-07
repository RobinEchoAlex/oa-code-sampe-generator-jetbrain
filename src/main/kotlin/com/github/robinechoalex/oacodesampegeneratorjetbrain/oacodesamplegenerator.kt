import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

//package com.github.robinechoalex.oacodesampegeneratorjetbrain
//
//
//    @JsModule("oacodesamplegenerator")
//    @JsNonModule
//    external fun <T> generator(specURL: String, singleOperation: String): Boolean
//
//
fun main() {


    // create a client
    val client = HttpClient.newHttpClient()

    // create a request
    val request = HttpRequest.newBuilder(
            URI.create("http://localhost:3000/query?url=https://docs.microsoft.com/en-us/rest/api/resources/deployment-operations/get-at-management-group-scope"))
            .header("accept", "application/json")
            .build()

    // use the client to send the request
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    // the response:
    System.out.println(response.body())
}
