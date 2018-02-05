package com.aspire.thread.test;

import java.util.Random;

public class RandomTest {
	private static int range = 100;
    private static int[] result;

    public static void main(String args[]) {
        result = getNumber(range);
        for (int i = 0; i < range; i++) {
            System.out.print(result[i] + ", ");
        }
    }

    public static int[] getNumber(int total){
        int[] NumberBox = new int[total];           //����A
        int[] rtnNumber = new int[total];           //����B

        for (int i = 0; i < total; i++){
          NumberBox[i] = i;     //�Ȱ�N������������A��
        }
        int end = total - 1;

        for (int j = 0; j < total; j++){
          int num = new Random().nextInt(end + 1);  //ȡ�����
          rtnNumber[j] = NumberBox[num];            //���������������B
          NumberBox[num] = NumberBox[end];          //������A�����һ����������ȡ�������
          end--;                                    //��С�������ȡ��Χ
        }
        return rtnNumber;                           //����int������
    }
}
