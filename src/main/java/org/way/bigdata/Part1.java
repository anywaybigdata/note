package org.way.bigdata;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import static org.apache.spark.sql.functions.col;

/**
 * 我的hdp是3.1.x的,spark是2.3.2的
 * 参考官网
 * http://spark.apache.org/docs/2.3.2/sql-programming-guide.html
 * <p>
 * git网站
 * https://github.com/apache/spark/tree/master/examples
 * <p>
 * 本地模式
 */
public class Part1 {
    public static void main(String[] args) {
        /*todo 1
         Starting Point: SparkSession
         */
        SparkSession spark = SparkSession
                .builder()
                //本地模式
                .master("local[2]")
                .appName("Java Spark SQL basic example")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();
        /*todo 2.1
        Creating DataFrames
         */
        //With a SparkSession, applications can create DataFrames from an existing RDD, from a Hive table, or from Spark data sources.
        Dataset<Row> df = spark.read().json("src/main/resources/people.json");

        // Displays the content of the DataFrame to stdout
        df.show();

        /* todo 2.2
         Untyped Dataset Operations (aka DataFrame Operations)
         */
        df.printSchema();


        // Select only the "name" column
        df.select("name").show();
        // +-------+
        // |   name|
        // +-------+
        // |Michael|
        // |   Andy|
        // | Justin|
        // +-------+

        // Select everybody, but increment the age by 1
        df.select(col("name"), col("age").plus(1)).show();
        // +-------+---------+
        // |   name|(age + 1)|
        // +-------+---------+
        // |Michael|     null|
        // |   Andy|       31|
        // | Justin|       20|
        // +-------+---------+

        // Select people older than 21
        df.filter(col("age").gt(21)).show();
        // +---+----+
        // |age|name|
        // +---+----+
        // | 30|Andy|
        // +---+----+

        // Count people by age
        df.groupBy("age").count().show();
        // +----+-----+
        //        // | age|count|
        //        // +----+-----+
        //        // |  19|    1|
        //        // |null|    1|
        //        // |  30|    1|
        //        // +----+-----+


        /*
        todo 2.3
        Running SQL Queries Programmatically
         */
        // Register the DataFrame as a SQL temporary view
        df.createOrReplaceTempView("people");

        Dataset<Row> sqlDF = spark.sql("SELECT * FROM people");
        sqlDF.show();
        // +----+-------+
        // | age|   name|
        // +----+-------+
        // |null|Michael|
        // |  30|   Andy|
        // |  19| Justin|
        // +----+-------+

        /*
        todo 2.4
         Global Temporary View
         */
        // Register the DataFrame as a global temporary view
        try {
            df.createGlobalTempView("people");
        } catch (AnalysisException e) {
            e.printStackTrace();
        }

        // Global temporary view is tied to a system preserved database `global_temp`
        spark.sql("SELECT * FROM global_temp.people").show();
        // +----+-------+
        // | age|   name|
        // +----+-------+
        // |null|Michael|
        // |  30|   Andy|
        // |  19| Justin|
        // +----+-------+

        // Global temporary view is cross-session
        spark.newSession().sql("SELECT * FROM global_temp.people").show();
        // +----+-------+
        // | age|   name|
        // +----+-------+
        // |null|Michael|
        // |  30|   Andy|
        // |  19| Justin|
        // +----+-------+


        /*
        todo 3
        Creating Datasets
         */
        System.out.println("------------------------Datasets---------------------------------------");

        // Create an instance of a Bean class
        Person person = new Person();
        person.setName("Andy");
        person.setAge(32);

        // Encoders are created for Java beans
        Encoder<Person> personEncoder = Encoders.bean(Person.class);
        Dataset<Person> javaBeanDS = spark.createDataset(
                Collections.singletonList(person),
                personEncoder
        );
        javaBeanDS.show();
        // +---+----+
        // |age|name|
        // +---+----+
        // | 32|Andy|
        // +---+----+

        // Encoders for most common types are provided in class Encoders
        Encoder<Integer> integerEncoder = Encoders.INT();
        Dataset<Integer> primitiveDS = spark.createDataset(Arrays.asList(1, 2, 3), integerEncoder);
        Dataset<Integer> transformedDS = primitiveDS.map(
                (MapFunction<Integer, Integer>) value -> value + 1,
                integerEncoder);
        transformedDS.collect(); // Returns [2, 3, 4]

        // DataFrames can be converted to a Dataset by providing a class. Mapping based on name
        String path = "src/main/resources/people.json";
        Dataset<Person> peopleDS = spark.read().json(path).as(personEncoder);
        peopleDS.show();
        // +----+-------+
        //        // | age|   name|
        //        // +----+-------+
        //        // |null|Michael|
        //        // |  30|   Andy|
        //        // |  19| Justin|
        //        // +----+-------+

        // 结束
        spark.stop();
    }


    public static class Person implements Serializable {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
