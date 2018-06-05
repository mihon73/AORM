package implementation.query

import org.testng.Assert
import org.testng.annotations.Test
import tanvd.aorm.DbType
import tanvd.aorm.InsertExpression
import tanvd.aorm.Row
import tanvd.aorm.expression.Column
import tanvd.aorm.implementation.InsertClickhouse
import tanvd.aorm.query.*
import tanvd.aorm.withDatabase
import utils.AormTestBase
import utils.ExampleTable
import utils.TestDatabase
import utils.getDate

@Suppress("UNCHECKED_CAST")
class QueryLimitTest : AormTestBase() {
    override fun executeBeforeMethod() {
        withDatabase(TestDatabase) {
            ExampleTable.create()
        }
    }

    @Test
    fun limit_limitByOneOrdered_gotOneRow() {
        withDatabase(TestDatabase) {
            val rows = arrayListOf(
                    Row(mapOf(ExampleTable.id to 2L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-01-01"),
                            ExampleTable.arrayValue to listOf("array1", "array2")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>),
                    Row(mapOf(ExampleTable.id to 3L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-02-02"),
                            ExampleTable.arrayValue to listOf("array3", "array4")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>)
            )
            InsertClickhouse.insert(TestDatabase, InsertExpression(ExampleTable, ExampleTable.columns, rows))

            val select = (ExampleTable.select() where (ExampleTable.value eq "value")).orderBy(ExampleTable.id to Order.ASC).limit(1L, 0L)

            Assert.assertEquals(select.toResult(), listOf(rows.first()))
        }
    }

    @Test
    fun limit_limitByZero_gotNoRows() {
        withDatabase(TestDatabase) {
            val rows = arrayListOf(
                    Row(mapOf(ExampleTable.id to 3L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-01-01"),
                            ExampleTable.arrayValue to listOf("array1", "array2")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>),
                    Row(mapOf(ExampleTable.id to 2L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-02-02"),
                            ExampleTable.arrayValue to listOf("array3", "array4")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>)
            )
            InsertClickhouse.insert(TestDatabase, InsertExpression(ExampleTable, ExampleTable.columns, rows))

            val select = (ExampleTable.select() where (ExampleTable.value eq "value")).orderBy(ExampleTable.id to Order.DESC).limit(0L, 0L)

            Assert.assertTrue(select.toResult().isEmpty())
        }
    }

    @Test
    fun limit_limitByTwo_gotAllRows() {
        withDatabase(TestDatabase) {
            val rows = arrayListOf(
                    Row(mapOf(ExampleTable.id to 3L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-01-01"),
                            ExampleTable.arrayValue to listOf("array1", "array2")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>),
                    Row(mapOf(ExampleTable.id to 2L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-02-02"),
                            ExampleTable.arrayValue to listOf("array3", "array4")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>)
            )
            InsertClickhouse.insert(TestDatabase, InsertExpression(ExampleTable, ExampleTable.columns, rows))

            val select = (ExampleTable.select() where (ExampleTable.value eq "value")).orderBy(ExampleTable.id to Order.DESC).limit(2L, 0L)

            Assert.assertEquals(select.toResult(), rows)
        }
    }

    @Test
    fun limit_orderedByTwoLimitByTwo_gotAllRows() {
        withDatabase(TestDatabase) {
            val rows = arrayListOf(
                    Row(mapOf(ExampleTable.id to 3L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-01-01"),
                            ExampleTable.arrayValue to listOf("array1", "array2")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>),
                    Row(mapOf(ExampleTable.id to 2L, ExampleTable.value to "value",
                            ExampleTable.date to getDate("2000-02-02"),
                            ExampleTable.arrayValue to listOf("array3", "array4")).toMutableMap() as MutableMap<Column<Any, DbType<Any>>, Any>)
            )
            InsertClickhouse.insert(TestDatabase, InsertExpression(ExampleTable, ExampleTable.columns, rows))

            val select = (ExampleTable.select() where (ExampleTable.value eq "value")).orderBy(ExampleTable.id to Order.DESC,
                    ExampleTable.value to Order.DESC).limit(2L, 0L)

            Assert.assertEquals(select.toResult(), rows)
        }
    }
}