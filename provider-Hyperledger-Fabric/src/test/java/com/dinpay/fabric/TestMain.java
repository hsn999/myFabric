package com.dinpay.fabric;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class TestMain {

    class Person{
        String name;
        String age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
    @Test
    public void test() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String app = "abcliuyw003";

        HashMap<String, String> map = new HashMap<>();

        map.put("appId", "lywe002020");
        map.put("appId1", "23");
        map.put("appId2", "43");
        map.put("appId3", "23424");

        Person person = new Person();
        person.setAge("33");
        person.setName("lywei");


//        System.out.println(BeanUtils.describe(map));

//        System.out.println(ReflectionToStringBuilder.toString(map));

        System.out.println(ToStringBuilder.reflectionToString(person, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(BeanUtils.describe(person).toString());
    }
}
