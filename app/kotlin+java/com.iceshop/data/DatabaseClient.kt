package com.iceshop.data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Gerenciador de conexão com o banco de dados PostgreSQL
 *
 * ⚠️ IMPORTANTE:
 * - Para EMULADOR Android: use DB_HOST = "10.0.2.2"
 * - Para DISPOSITIVO FÍSICO: use o IP real do seu computador
 */
object DatabaseClient {
    private const val JDBC_DRIVER = "org.postgresql.Driver"
    private const val DB_HOST = "10.0.2.2"  // Emulador Android
    // private const val DB_HOST = "192.168.1.X"  // Dispositivo físico (substitua pelo IP real)
    private const val DB_PORT = "5432"
    private const val DB_NAME = "ice_cream_shop"
    private const val DB_USER = "postgres"
    private const val DB_PASSWORD = "root"  // Substitua pela sua senha

    private const val DB_URL = "jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"

    private var connection: Connection? = null

    /**
     * Obtém uma conexão com o banco de dados
     * Reutiliza a conexão existente se estiver ativa
     */
    fun getConnection(): Connection? {
        return try {
            if (connection == null || connection?.isClosed == true) {
                Class.forName(JDBC_DRIVER)
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
                println("✅ Conexão com banco de dados estabelecida!")
            }
            connection
        } catch (e: Exception) {
            println("❌ Erro ao conectar ao banco: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    fun closeConnection() {
        try {
            connection?.close()
            connection = null
            println("🔒 Conexão com banco de dados fechada.")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}
