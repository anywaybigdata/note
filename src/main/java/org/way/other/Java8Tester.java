package org.way.other;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 参考
 * https://blog.csdn.net/zxzxzx0119/article/details/82392396
 * <p>
 * https://www.runoob.com/java/java8-lambda-expressions.html
 */
public class Java8Tester {

    //------------------------概念----------------------------------------------

    /**
     * Lam使代码变的更加简洁紧bda 表达式，也可称为闭包，它是推动 Java 8 发布的最重要新特性。
     * *
     * * Lambda 允许把函数作为一个方法的参数（函数作为参数传递进方法中）。
     * *
     * * 使用 Lambda 表达式可以简化。
     *
     *  Lambda表达式的实质是　对接口的实现；
     */
    @Test
    public void what() {
        // 类型声明
        MathOperation addition = (int a, int b) -> a + b;

        // 不用类型声明
        MathOperation subtraction = (a, b) -> a - b;

        // 大括号中的返回语句
        MathOperation multiplication = (int a, int b) -> {
            return a * b;
        };

        // 没有大括号及返回语句
        MathOperation division = (int a, int b) -> a / b;

        System.out.println("10 + 5 = " + operate(10, 5, addition));
        System.out.println("10 - 5 = " + operate(10, 5, subtraction));
        System.out.println("10 x 5 = " + operate(10, 5, multiplication));
        System.out.println("10 / 5 = " + operate(10, 5, division));

        // 不用括号
        GreetingService greetService1 = message ->
                System.out.println("Hello " + message);

        // 用括号
        GreetingService greetService2 = (message) ->
                System.out.println("Hello " + message);

        greetService1.sayMessage("Runoob");
        greetService2.sayMessage("Google");
    }


    @Test
    public void why() {
        //todo 案例一 简化代码
        Comparator<Integer> oldCompare = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {  //降序排列
                return Integer.compare(o2, o1);
            }
        };

        Comparator<Integer> lambdaCompare = (x, y) -> Integer.compare(y, x);

        //将数组转换成集合的
        List<Employee> employees = Arrays.asList(
                new Employee("张三", 23, 3333.33),
                new Employee("李四", 24, 4444.44),
                new Employee("王五", 25, 5555.55),
                new Employee("赵六", 26, 6666.66),
                new Employee("田七", 27, 7777.77)
        );

        /*
        todo 案例二
       需求1
        比如查询年龄>25岁的所有员工的信息；
        再如查询工资>4000的员工信息；
         */
        old1FindEmployee(employees);

        //优化方式一-使用策略模式来优化   不需要写两次迭代的代码
        strategyFindEmployee(employees);

        //优化方式二-使用匿名内部类优化    不需要创建新类
        innerClassFindEmployee(employees);

        //优化方式3 - 使用Lambda表达式  省去匿名内部类的没用的代码
        lambdaFindEmployee(employees);

        //优化方式四-使用Stream-API  完全不需要其他的代码，包括不需要filterEmployees()方法，代码很简洁:
        streamApiFindEmployee(employees);
    }

    @Test
    public void how() {
        //todo Java8四大内置函数式接口
        //我们发现，如果使用Lambda还要自己写一个接口的话太麻烦，所以Java自己提供了一些接口:


        //todo 方法引用和构造器引用
    }

    private void streamApiFindEmployee(List<Employee> employees) {
        employees.stream().filter((e) -> e.getSalary() < 4000).limit(2).forEach(System.out::println);
        System.out.println("------------------");
        employees.stream().map(Employee::getName).forEach(System.out::println); //打印所有的名字
    }

    //优化方式3 - 使用Lambda表达式
    private void lambdaFindEmployee(List<Employee> employees) {
        List<Employee> list1 = filterEmployees(employees, (e) -> e.getAge() > 25);
        list1.forEach(System.out::println);

        List<Employee> list2 = filterEmployees(employees, (e) -> e.getSalary() > 4000);
        list2.forEach(System.out::println);


    }

    //优化方式二-使用匿名内部类优化    不需要创建新类
    private void innerClassFindEmployee(List<Employee> employees) {
        List<Employee> list = filterEmployees(employees, new MyPredicate<Employee>() {
            @Override
            public boolean test(Employee employee) {
                return employee.getSalary() > 4000;
            }
        });
        for (Employee emp : list) {
            System.out.println(emp);
        }
    }

    //优化方式一-使用策略模式来优化
    private void strategyFindEmployee(List<Employee> employees) {
        List<Employee> list = filterEmployees(employees, new FilterEmployeeByAge());
        for (Employee emp : list) {
            System.out.println(emp);
        }
        System.out.println("------------------------");
        List<Employee> list2 = filterEmployees(employees, new FilterEmployeeBySalary());
        for (Employee emp : list2) {
            System.out.println(emp);
        }
    }

    //旧的查询
    private void old1FindEmployee(List<Employee> employees) {
        //年龄
        List<Employee> list = findEmployeesByAge(employees);
        for (Employee emp : list) {
            System.out.println(emp);
        }
        //工资
        System.out.println("---------------------");
        List<Employee> list2 = findEmployeesBySalary(employees);
        for (Employee emp : list2) {
            System.out.println(emp);
        }
    }




    interface MathOperation {
        int operation(int a, int b);
    }

    interface GreetingService {
        void sayMessage(String message);
    }

    private int operate(int a, int b, MathOperation mathOperation) {
        return mathOperation.operation(a, b);
    }

    public class Employee {
        private String name;
        private int age;
        private double salary;

        public Employee() {
        }

        public Employee(String name, int age, double salary) {
            this.name = name;
            this.age = age;
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public double getSalary() {
            return salary;
        }

        @Override
        public String toString() {
            return "name='" + name + '\'' +
                    ", age=" + age +
                    ", salary=" + salary;
        }
    }

    //原始方法 : 查询出年龄大于25岁的(这个是最原始的方法)
    public List<Employee> findEmployeesByAge(List<Employee> list) {
        List<Employee> emps = new ArrayList<>();
        for (Employee emp : list) {
            if (emp.getAge() > 25) {
                emps.add(emp);
            }
        }
        return emps;
    }

    //原始方法 : 查询出工资大于4000的(这个是最原始的方法)
//和上面的方法唯一的差别只有年龄和工资的改动，代码冗余
    public List<Employee> findEmployeesBySalary(List<Employee> list) {
        List<Employee> emps = new ArrayList<>();
        for (Employee emp : list) {
            if (emp.getSalary() > 4000) {
                emps.add(emp);
            }
        }
        return emps;
    }

    //
    public interface MyPredicate<T> {
        public boolean test(T t);
    }

    public class FilterEmployeeByAge implements MyPredicate<Employee> {
        @Override
        public boolean test(Employee employee) {
            return employee.getAge() > 25;
        }
    }

    public class FilterEmployeeBySalary implements MyPredicate<Employee> {
        @Override
        public boolean test(Employee employee) {
            return employee.getSalary() >= 4000;
        }
    }

    //优化一 把迭代抽出来,使用不同的行为
    public List<Employee> filterEmployees(List<Employee> list, MyPredicate<Employee> mp) {
        List<Employee> emps = new ArrayList<>();
        for (Employee emp : list) {
            if (mp.test(emp)) {  //调用相应的过滤器
                emps.add(emp);
            }
        }
        return emps;
    }
}
