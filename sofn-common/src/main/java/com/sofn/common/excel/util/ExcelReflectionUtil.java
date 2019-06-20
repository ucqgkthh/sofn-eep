package com.sofn.common.excel.util;

import com.google.common.collect.Lists;
import com.sofn.common.excel.ExcelException;
import com.sofn.common.excel.annotation.ExcelField;
import com.sofn.common.excel.annotation.ExcelFields;
import com.sofn.common.excel.annotation.ExcelImportCheck;
import com.sofn.common.excel.annotation.ExcelSheetInfo;
import com.sofn.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Excel反射相关工具
 * Created by heyongjie on 2019/6/13 13:53
 */
@Slf4j
public class ExcelReflectionUtil {

    /**
     * 获取从哪行开始读取
     *
     * @param cls Excel注解类
     * @return int  HeadNum
     */
    public static int getHeadNum(Class cls) {
        int headNum = 1;
        String title = getTableTitle(cls);
        if (StringUtils.isBlank(title)) {
            // 如果为空就从1开始
            return headNum;
        }
        // 如果标题不为空 就从2开始
        return 2;
    }

    /**
     * 返回一个类的结构信息
     *
     * @param cls 要获取的类
     * @return Object[] -->  第一个为Field，第二个为ExcelField注解，第三个为ExcelImportCheck注解，第4个为检查属性@ExcelFields
     */
    public static List<Object[]> getClassInfo(Class cls, ExcelField.Type type) {
        if(type == null){
            type = ExcelField.Type.ALL;
        }

        Field[] fields = cls.getDeclaredFields();
        List<Object[]> classInfo = Lists.newArrayList();
        for (Field field : fields) {
            Object[] obj = new Object[4];
            obj[0] = field; // 当前类的字段
            // 如果是集合直接continue
            if (field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(Map.class) ||
                    field.getType().isAssignableFrom(Set.class)) {
//                log.info("当前属性是集合{},忽略", field.getType());
                continue;
            }
            // 注解在属性上
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (excelField != null) {
                // 区别导入和导出
                ExcelField.Type type1 = excelField.type();
                // 当前字段的Type 既不等于传入的Type 又不等于All那就跳过
                if(type.value() !=excelField.type().value() && excelField.type().value() != ExcelField.Type.ALL.value()){
                    continue;
                }
                obj[1] = excelField;   // 当前字段的ExcelField注解
            }
            ExcelImportCheck excelImportCheck = field.getAnnotation(ExcelImportCheck.class);
            if (excelImportCheck != null) {
                obj[2] = excelImportCheck; //当前字段的 ExcelImportCheck 注解
            }

            ExcelFields excelFields = field.getAnnotation(ExcelFields.class);
            if (excelFields != null) {
                obj[3] = excelFields;
            }
            classInfo.add(obj);
        }
        classInfo.sort(Comparator.comparingInt(o -> ((o[1]) == null) ? 0 :  ((ExcelField)o[1]).sort()));
        return classInfo;
    }

    /**
     * 递归获取所有列信息
     *
     * @param cls 类
     * @return Object[]  1->String标题名称,2->Integer 标题宽度,3->ExcelField,4->ExcelImportCheck,5->Field
     */
    public static List<Object[]> getAllFieldInfo(Class cls, ExcelField.Type type) {
        List<Object[]> objects = Lists.newArrayList();
        // 获取类信息
        List<Object[]> classInfo = getClassInfo(cls,type);
        // Object[] -->  第一个为Field，第二个为ExcelField注解，第三个为ExcelImportCheck注解，第4个为检查属性@ExcelFields
        if (classInfo != null) {
            for (int i = 0; i < classInfo.size(); i++) {
                Object[] objs = classInfo.get(i);
                ExcelFields excelFields = (ExcelFields) objs[3];
                if (excelFields != null) {
                    // 递归查找
                    List<Object[]> sonTitleInfos = getAllFieldInfo(excelFields.clazz(),type);
                    objects.addAll(sonTitleInfos);
                } else {
                    ExcelField excelField = (ExcelField) objs[1];
                    if(excelField != null){
                        // Object[]  1->String标题名称,2->Integer 标题宽度,3->ExcelField,4->ExcelImportCheck
                        Object[] obj = new Object[5];
                        if (excelField != null) {
                            obj[0] = excelField.title(); // 列标题
                            obj[1] = excelField.width(); // 列宽度
                            obj[2] = excelField;   // 属性注解
                        }
                        obj[3] = objs[2];  //   检查注解
                        obj[4] = objs[0];   //   列属性
                        objects.add(obj);
                    }

                }
            }
        }

        // 根据Sort排序
        objects.sort(Comparator.comparingInt(o -> ((o[2]) == null) ? 0 :  ((ExcelField)o[2]).sort()));
        return objects;
    }

    /**
     * 获取SheetTitle
     *
     * @param cls 类信息
     * @return SheetTitle
     */
    public static String getSheetTitle(Class cls) {
        ExcelSheetInfo excelSheetInfo = (ExcelSheetInfo) cls.getAnnotation(ExcelSheetInfo.class);
        if (excelSheetInfo != null) {
            return excelSheetInfo.sheetName();
        }
        return "Sheet1";
    }

    /**
     * 获取表格标题信息
     *
     * @param cls Excel注解类
     * @return 表格标题信息
     */
    public static String getTableTitle(Class cls) {
        ExcelSheetInfo excelSheetInfo = (ExcelSheetInfo) cls.getAnnotation(ExcelSheetInfo.class);
        if (excelSheetInfo != null) {
            return excelSheetInfo.title();
        }
        return "Title";
    }


    /**
     * 根据Excel一行数据设置对象值  ExcelFields.sort失效  ExcelFields.column生效
     *
     * @param row        Excel一行数据
     * @param clazz      带有Excel注解的Bean
     * @param redCellNum 从那一列开始读取 默认从第0开始读取
     * @return 根据Excel的一行数据得到的Object对象
     * @throws Exception
     */
    public static Object setValueByRowAndClass(Row row, Class clazz, int redCellNum,ExcelField.Type type) throws Exception {
        if (row == null) {
            log.info("无操作，当前行为空");
            return null;
        }
        // 获取类信息
        List<Object[]> classInfo = getClassInfo(clazz,type);
        if (classInfo != null) {
            // 实例化类
            Object obj = clazz.newInstance();
            int num = 0;  // 列信息
            // 遍历列     从哪列开始到哪列结束
            for (int i = redCellNum; i < (redCellNum + classInfo.size()); i++) {
                // 判断是否含有ExcelFields注解，如果有就需要递归设置值并返回Object值
                // 遍历对象列  这里从0开始
                Object[] objs = classInfo.get(num);
                num++;

                Field field = (Field) objs[0];
                ExcelFields excelFields = (ExcelFields) objs[3];
                if (excelFields != null) {
                    // 子对象
                    Object tempObj = setValueByRowAndClass(row, excelFields.clazz(), i,type);
                    setValue(obj, tempObj, field);
                } else {
                    // 不含有ExcelFields就判断是否有ExcelField 注解 ，如果有就需要从中获取值设置
                    ExcelField excelField = (ExcelField) objs[1];
                    if (excelField != null) {
                        // 获取值
                        int index = (-1 != excelField.column()) ? excelField.column() : i;
                        Object val = row.getCell(index);
                        // 是否含有ExcelImportCheck  有的话就需要检查值是否符合规范
                        ExcelImportCheck excelImportCheck = (ExcelImportCheck) objs[2];
                        if (excelImportCheck != null) {
                            // 检查值是否符合要求
                            try{
                                ExcelCheckUtil.checkVal(excelImportCheck, val, row.getRowNum(), excelField, field);
                            }catch (ExcelException e){
                                throw new ExcelException(e.getMessage());
                            }
                        }
                        // 类型强制转换
                        val = getValue(val, excelField, field,row.getCell(i));
                        // 将值设置到对象中
                        setValue(obj, val, field);
                    }
                }
            }
            return obj;
        }
        log.info("无操作，类信息为空");
        return null;
    }


    /**
     * 设置对象的字段的值  通过更改字段的访问权限设置值
     *
     * @param obj   要设置值的对象
     * @param value 值
     * @param field 字段
     * @throws Exception
     */
    public static void setValue(Object obj, Object value, Field field) throws Exception {
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(false);
    }

    /**
     * 值强制转换    用于导入
     *
     * @param val   需要转换的值
     * @param ef    ExcelField
     * @param field 字段属性
     * @param <T>   转换后的类型
     * @return 转换后的值
     */
    public static <T> T getValue(Object val, ExcelField ef, Field field, Cell cell) {

        // 获取参数类型和类型强制转换
        Class<?> valType = Class.class;
        valType = field.getType();
        try {
            if (val != null) {
                if (valType == String.class) {
                    // 如果是String 返回原始值
                    String s = String.valueOf(val.toString());
                    // 如果字典值 翻译字典值返回
                    if(!StringUtils.isBlank(ef.dictType())){
                        s = ExcelDictUtil.getValue(ef.dictType(), s);
                    }
                    val = s;
                } else if (Integer.class.equals(valType) || int.class.equals(valType)) {
                    val = Double.valueOf(val.toString()).intValue();
                } else if (valType == Long.class || long.class.equals(valType)) {
                    val = Double.valueOf(val.toString()).longValue();
                } else if (valType == Double.class || double.class.equals(valType)) {
                    val = Double.valueOf(val.toString());
                } else if (valType == Float.class || float.class.equals(valType)) {
                    val = Float.valueOf(val.toString());
                } else if (valType == Date.class) {
                    // 如果是时间类型
                    if(val instanceof  Cell){
                        val = ((Cell) val).getDateCellValue();
                    }else if(val instanceof String){
                        // 如果值是字符串类型那么需要转换一次
                        String dataFormat = ef.dataFormat();
                        if("@".equals(dataFormat)){
                            dataFormat = DateUtils.DATE_PATTERN;
                        }
                        // 如果时间转换异常的话 直接抛出
                        try{
                            val = DateUtils.stringToDate((String) val,dataFormat);
                        }catch (Exception e){
                            e.printStackTrace();
                            throw new ExcelException("导入时时间转换异常，请查看时间是否正确");
                        }
                    }
                } else if (valType == BigDecimal.class) {
                    val = new BigDecimal(val.toString());
                } else  if (valType == BigInteger.class) {
                    val = new BigInteger(val.toString());
                }else{
                    // TODO 其他类型
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) val;
    }

}
