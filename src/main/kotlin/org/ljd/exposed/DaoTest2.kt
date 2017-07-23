package org.ljd.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.dao.*

object TestUsers : IntIdTable() {
	val name = varchar("name", 50).index()
	val city = reference("city", TestCities)
	val age = integer("age")
}

object TestCities: IntIdTable() {
	val name = varchar("name", 50)
}

class TestUser(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<TestUser>(TestUsers)

	var name by TestUsers.name
	var city by TestCity referencedOn TestUsers.city
	var age by TestUsers.age
}

class TestCity(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<TestCity>(TestCities)

	var name by TestCities.name
	val users by TestUser referrersOn TestUsers.city
}

fun main(args: Array<String>) {
//	Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
// 	Database.connect("jdbc:mysql://127.0.0.1:3306/Employees?user=root&password=indy25tlx", driver = "org.mariadb.jdbc.Driver")
//	Database.connect("jdbc:postgresql://127.0.0.1:5434/Employees?user=postgres&password=indy25tlx", driver = "org.postgresql.Driver")
	Database.connect("jdbc:mysql://127.0.0.1:3307/employees?user=liam&password=indy25tlx", driver = "com.mysql.cj.jdbc.Driver")

	transaction {
		logger.addLogger(StdOutSqlLogger)

		create (TestCities, TestUsers)

		val stPete = TestCity.new {
			name = "St. Petersburg"
		}

		val munich = TestCity.new {
			name = "Munich"
		}

		TestUser.new {
			name = "a"
			city = stPete
			age = 5
		}

		TestUser.new {
			name = "b"
			city = stPete
			age = 27
		}

		TestUser.new {
			name = "c"
			city = munich
			age = 42
		}

		println("Cities: ${City.all().joinToString {it.name}}")
		println("Users in ${stPete.name}: ${stPete.users.joinToString {it.name}}")
		println("Adults: ${TestUser.find { Users.age greaterEq 18 }.joinToString {it.name}}")
	}
}