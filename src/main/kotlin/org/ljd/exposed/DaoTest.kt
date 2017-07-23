package org.ljd.exposed

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
	val name = varchar("name", 50).index()
	val city = reference("city", Cities)
	val age = integer("age")
}

object Cities : IntIdTable() {
	val name = varchar("name", 50)
}

class UserDaoTest(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<UserDaoTest>(Users)

	var name by Users.name
	var city by City referencedOn Users.city
	var age by Users.age
}

class City(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<City>(Cities)

	var name by Cities.name
	val users by UserDaoTest referrersOn Users.city
}

fun main(args: Array<String>) {
//	Database.connect("jdbc:mysql://127.0.0.1:3306/Employees?user=root&password=indy25tlx", driver = "org.mariadb.jdbc.Driver")
//	Database.connect("jdbc:postgresql://127.0.0.1:5434/Employees?user=postgres&password=indy25tlx", driver = "org.postgresql.Driver")
	Database.connect("jdbc:mysql://127.0.0.1:3307/employees?user=liam&password=indy25tlx", driver = "com.mysql.cj.jdbc.Driver")


	transaction {
		logger.addLogger(StdOutSqlLogger)
		debug = true

		createMissingTablesAndColumns(Cities, Users)

		println("===== Creating cities")
		val stPete = City.new {
			name = "St. Petersburg"
		}


		val munich = City.new {
			name = "Munich"
		}

//		println("===== Fetching cities")
//
//		val stPete = City.find { Cities.name eq "St. Petersburg"}.first()
//		val munich = City.find { Cities.name eq "Munich"}.first()

		println("===== Creating users")
		val albert = UserDaoTest.new {
			name = "albert"
			city = stPete
			age = 5
		}

		val bob = UserDaoTest.new {
			name = "bob"
			city = stPete
			age = 27
		}

		val chris = UserDaoTest.new {
			name = "chris"
			city = munich
			age = 42
		}


		println("===== Running queries")
		println("DSLCities: ${City.all().joinToString { it.name }}")
		println("DSLUsers in ${stPete.name}: ${stPete.users.joinToString { it.name }}")
		println("Adults: ${UserDaoTest.find { Users.age greaterEq 18 }.joinToString { it.name }}")


		drop(Users, Cities)
	}
}