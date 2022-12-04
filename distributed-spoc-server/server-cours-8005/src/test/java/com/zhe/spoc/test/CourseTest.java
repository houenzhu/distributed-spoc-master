package com.zhe.spoc.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author HouEnZhu
 * @ClassName CourseTest
 * @Description TODO
 * @date 2022/11/10 15:27
 * @Version 1.0
 */

@SpringBootTest
public class CourseTest{

    @Test
    void name() {
        int[] array = new int[]{55, 33, 22, 66, 11};
        //输出排序前的array数组
        System.out.print("排序前：");
        System.out.println(Arrays.toString(array));
        //调用BubbleSort类中的sort方法对array数组进行排序
        sort(array);
        //输出冒泡排序后的array数组
        System.out.print("排序后：");
        System.out.println(Arrays.toString(array));
    }

    void sort(int[] array) {

        //i表示第几轮“冒泡”，j 表示“走访”到的元素索引。
        // 每一轮“冒泡”中，j 需要从列表开头“走访”到 array.length - 1 的位置。
        for (int i = 0; i < array.length - 1; i++ ) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
}
